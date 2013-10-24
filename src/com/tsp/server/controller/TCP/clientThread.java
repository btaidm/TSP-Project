package com.tsp.server.controller.TCP;

import com.tsp.game.actors.Actor;
import com.tsp.packets.ActorPacket;
import com.tsp.packets.Packet;
import com.tsp.server.model.ServerModel;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/19/13
 * Time: 1:37 PM
 * To change this template use File | Settings | File Templates.
 */
class clientThread extends Thread
{
	private static final Logger LOGGER = LoggerFactory.getLogger(clientThread.class);

	private String clientName = null;
	private DataInputStream is = null;
	private DataOutputStream os = null;
	private Socket clientSocket = null;
	private final clientThread[] threads;
	private int maxClientsCount;
	private ServerModel serverModel = null;
	private Integer playerID;
	Queue<Packet> outGoingPackets;
	private boolean running = true;


	private String BytesToString(byte[] bytes) throws IOException
	{
		InputStreamReader input = new InputStreamReader(
				new ByteArrayInputStream(bytes), Charset.forName("UTF-8"));

		StringBuilder str = new StringBuilder();

		for (int value; (value = input.read()) != -1; )
			str.append((char) value);


		return str.toString();
	}

	public void addOutGoingPacket(Packet packet)
	{
		synchronized (this)
		{
			for (int i = 0; i < maxClientsCount; i++)
			{
				if (threads[i] != null)
				{
					outGoingPackets.add(packet);
				}
			}

		}
	}

	public clientThread(Socket clientSocket, clientThread[] threads, ServerModel sm)
	{
		this.outGoingPackets = new LinkedList<Packet>();
		this.clientSocket = clientSocket;
		this.threads = threads;
		maxClientsCount = threads.length;
		serverModel = sm;
	}

	public void run()
	{
		int maxClientsCount = this.maxClientsCount;
		clientThread[] threads = this.threads;
		try
		{

		   /*
		    * Create input and output streams for this client.
	        */
			is = new DataInputStream(clientSocket.getInputStream());
			os = new DataOutputStream(clientSocket.getOutputStream());

			int count = is.available();

			byte[] name = new byte[count];

			is.read(name);

			String PlayName = BytesToString(name);
			synchronized (this)
			{
				for (int i = 0; i < maxClientsCount; i++)
				{
					if (threads[i] != null && threads[i] == this)
					{
						clientName = "@" + PlayName;
						break;
					}
				}
				playerID = serverModel.addPlayer(PlayName);
				os.writeInt(playerID);
				sendDungeon(serverModel.getDungeonArray());
				sendPlayer();
				sendActors();
			}



		    /* Start the conversation. */
			while (running)
			{
				processIncoming();
				processPackets();
				this.wait(10);
				break;
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();

		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				quit();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void sendPlayer() throws IOException
	{
		ActorPacket player = new ActorPacket(serverModel.getPlayer(playerID));
		os.writeUTF(player.toJSONString());
	}

	private void processIncoming()
	{
		try
		{
			String json = is.readUTF();
			Object object = JSONValue.parse(json);
			if (object != null && object instanceof JSONObject)
			{
				Packet packet = Packet.parseJSONObject((JSONObject) object);
					if (packet.getPacketType() == Packet.PacketType.QUITPACKET)
						running = false;
			}
		}
		catch (IOException e)
		{
		}
	}

	private void processPackets() throws IOException
	{
		synchronized (this)
		{
			while (!outGoingPackets.isEmpty())
			{
				os.writeUTF(outGoingPackets.poll().toJSONString());
			}
		}
	}

	public void quit() throws IOException
	{
		/*
	   * Clean up. Set the current thread variable to null so that a new client
       * could be accepted by the server.
       */
		synchronized (this)
		{
			for (int i = 0; i < maxClientsCount; i++)
			{
				if (threads[i] == this)
				{
					threads[i] = null;
				}
			}
		}
	  /*
	   * Close the output stream, close the input stream, close the socket.
       */
		is.close();
		os.close();
		clientSocket.close();
	}

	private void sendDungeon(String[][][] strings) throws IOException
	{
		os.writeInt(serverModel.getColumns());
		os.writeInt(serverModel.getRows());
		os.writeInt(serverModel.getFloors());
		for (int z = 0; z < serverModel.getFloors(); z++)
			for (int x = 0; x < serverModel.getColumns(); x++)
				for (int y = 0; y < serverModel.getRows(); y++)
					os.writeUTF(strings[z][x][y]);
	}

	private void sendBytes(byte[] myByteArray) throws IOException
	{
		sendBytes(myByteArray, 0, myByteArray.length);
	}

	private void sendBytes(byte[] myByteArray, int start, int length) throws IOException
	{
		if (length < 0)
			throw new IllegalArgumentException("Negative length not allowed");
		if (start < 0 || start >= myByteArray.length)
			throw new IndexOutOfBoundsException("Out of Bounds: " + start);

		os.writeInt(length);
		if (length > 0)
		{
			os.write(myByteArray, start, length);
		}
	}

	private void sendActors() throws IOException
	{
		LOGGER.info("{}: {}: {}: Sending Actors", Thread.currentThread().getName(),Thread.currentThread().getId(), this.getClass());
		ArrayList<Actor> actors = serverModel.getActors();
		for (Actor actor : actors)
		{
			os.writeUTF(new ActorPacket(actor).toJSONString());
		}

	}

	protected void sendNewPlayer() throws IOException
	{
		ActorPacket player = new ActorPacket(serverModel.getPlayer(playerID));
		for (int i = 0; i < maxClientsCount; i++)
		{
			if (threads[i] != null && threads[i] != this)
			{
				threads[i].addOutGoingPacket(player);
			}
		}
	}
}

