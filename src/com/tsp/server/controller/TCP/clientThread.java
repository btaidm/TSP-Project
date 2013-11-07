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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
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
	private SocketChannel clientSocket = null;
	private final clientThread[] threads;
	private int maxClientsCount;
	private ServerModel serverModel = null;
	private Integer playerID;
	Queue<Packet> outGoingPackets;
	private boolean running = true;
	boolean viewer = false;


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
			outGoingPackets.add(packet);
		}
	}

	public clientThread(SocketChannel clientSocket, clientThread[] threads, ServerModel sm)
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

			sendDungeon(serverModel.getDungeonArray());

			int count = is.available();
			while (count <= 0)
			{
				count = is.available();
			}
			byte[] name = new byte[count];

			is.read(name);

			viewer = BytesToString(name).trim().equals("TSPVIEWER");
			if (!viewer)
			{
				String PlayName = BytesToString(name).trim();
				synchronized (this)
				{
					clientName = PlayName;
					playerID = serverModel.addPlayer(PlayName);
					os.writeInt(playerID);

					sendPlayer();
					sendNewPlayer();
				}

			}
			else
			{
				os.writeInt(-1);
			}
			sendActors();

			clientSocket.socket().setSoTimeout(100);
			/* Start the conversation. */
			while (running)
			{
				try
				{
					this.wait(10);
				}
				catch (Exception e)
				{
				}
				processIncoming();
				processPackets();
			}

		}
		catch (IOException e)
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
		writeString(player.toJSONString());
	}

	private void processIncoming()
	{
		try
		{
			String json = readString();
			Object object = JSONValue.parse(json);
			if (object != null && object instanceof JSONObject)
			{
				Packet packet = Packet.parseJSONObject((JSONObject) object);
				if (packet.getPacketType() == Packet.PacketType.QUIT_PACKET)
					running = false;
			}
		}
		catch (IOException e)
		{
		}
	}

	private String readString() throws IOException
	{
		ByteBuffer bytes = ByteBuffer.allocate(4);
		int bytesRead = clientSocket.read(bytes);
		if(bytesRead != 4)
			throw new IOException("Could not read in bytes for String Length");
		int size = bytes.getInt();
		ByteBuffer stringBuff = ByteBuffer.allocate(size);
		int totalBytesRead = 0;
		while(totalBytesRead < size)
		{
			totalBytesRead+=clientSocket.read(stringBuff);
		}
		CharBuffer decodedString = Charset.forName("UTF-8").decode(stringBuff);
		String ret = decodedString.toString();
		LOGGER.trace("{}",ret);
		return ret;
	}

	private void processPackets() throws IOException
	{
		synchronized (this)
		{
			while (!outGoingPackets.isEmpty())
			{
				Packet packet = outGoingPackets.poll();
				long start = System.currentTimeMillis();
				writeString(packet.toJSONString());
				long end = System.currentTimeMillis();
				LOGGER.debug("Sent Packet: {}: {} ms", packet.toString(), end - start);
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
	  /*
	   * Close the output stream, close the input stream, close the socket.
       */
			if (!viewer)
				serverModel.removePlayer(playerID);
			//is.close();
			//os.close();
			clientSocket.close();
		}
	}

	private void sendDungeon(String[][][] strings) throws IOException
	{
		os.writeInt(serverModel.getColumns());
		os.writeInt(serverModel.getRows());
		os.writeInt(serverModel.getFloors());
		for (int z = 0; z < serverModel.getFloors(); z++)
			for (int x = 0; x < serverModel.getColumns(); x++)
				for (int y = 0; y < serverModel.getRows(); y++)
				{
					writeString(strings[z][x][y]);
				}
	}

	private void sendActors() throws IOException
	{
		LOGGER.info("Sending Actors");
		ArrayList<Actor> actors = serverModel.getActors();
		for (Actor actor : actors)
		{
			writeString(new ActorPacket(actor).toJSONString());
		}

	}

	protected void sendNewPlayer() throws IOException
	{
		ActorPacket player = new ActorPacket(serverModel.getPlayer(playerID));
		TCPServer.addOutGoingPacket(player);
	}

	protected void writeString(String string) throws IOException
	{
		byte[] stringBytes = string.getBytes(Charset.forName("UTF-8"));
		Integer size = stringBytes.length;
		ByteBuffer buf = ByteBuffer.allocate(4 + size);
		buf.putInt(size).put(stringBytes);
		clientSocket.write(buf);
	}

	protected void writ
}

