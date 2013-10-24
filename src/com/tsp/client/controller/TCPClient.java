package com.tsp.client.controller;

import com.tsp.client.model.GameModel;
import com.tsp.game.actors.Actor;
import com.tsp.packets.ActorPacket;
import com.tsp.packets.Packet;
import com.tsp.packets.QuitPacket;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/20/13
 * Time: 6:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class TCPClient extends Thread
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TCPClient.class);
	GameModel model;
	InetAddress addr;
	int port = 12000;
	Socket clientSocket;
	DataInputStream is;
	DataOutputStream os;
	boolean running = true;

	@Override
	public void run()
	{
		try
		{
			connect();
			sendName(model.getName());
			model.setID(getID());
			model.setDungeon(getDungeon());
			model.setMe(getPlayer());
			model.setReady(true);
			clientSocket.setSoTimeout(100);
			while (running)
			{
				synchronized (this)
				{
					model.insertPacket(getPacket());
				}
			}
		}
		catch (IOException e)
		{
			LOGGER.info("{}", e.toString());
			model.setQuit(true);
		}
	}

	private Actor getPlayer() throws IOException
	{
		String json = is.readUTF();
		Object object = JSONValue.parse(json);
		if (object != null && object instanceof JSONObject)
		{
			Packet packet = Packet.parseJSONObject((JSONObject) object);
			if (packet.getPacketType() == Packet.PacketType.ACTORPACKET)
				if(((ActorPacket) packet).getActor().getType() == Actor.ActorType.ACTOR_PLAYER)
				{
					return ((ActorPacket) packet).getActor();
				}
		}
		return null;
	}

	public TCPClient(GameModel model) throws UnknownHostException
	{
		this.model = model;
		addr = InetAddress.getLocalHost();
	}


	public TCPClient(GameModel model, InetAddress address, int port)
	{
		this.model = model;
		addr = address;
		this.port = port;
	}

	public TCPClient(GameModel model, String host, int port) throws UnknownHostException
	{
		this.model = model;
		addr = InetAddress.getByName(host);
		this.port = port;
	}

	public void connect() throws IOException
	{
		clientSocket = new Socket(addr, port);
		is = new DataInputStream(clientSocket.getInputStream());
		os = new DataOutputStream(clientSocket.getOutputStream());
	}

	public void sendName(String name) throws IOException
	{
		os.writeUTF(name);
	}

	public int getID() throws IOException
	{
		return is.readInt();
	}

	public String[][][] getDungeon() throws IOException
	{
		int Columns = is.readInt();
		int Rows = is.readInt();
		int Floors = is.readInt();
		String[][][] dungeon = new String[Floors][Columns][Rows];
		for (int z = 0; z < Floors; z++)
			for (int x = 0; x < Columns; x++)
				for (int y = 0; y < Rows; y++)
					dungeon[z][x][y] = is.readUTF();
		return dungeon;
	}

	public Packet getPacket()
	{
		try
		{
			String json = is.readUTF();
			Object object = JSONValue.parse(json);
			if (object != null && object instanceof JSONObject)
			{
				Packet packet = Packet.parseJSONObject((JSONObject) object);
				return packet;
			}
		}
		catch (IOException e)
		{
			return null;
		}
		return null;
	}

	public void sendQuit() throws IOException
	{
		if (os != null)
		{
			os.writeUTF(new QuitPacket().toJSONString());
			os.close();
		}

		if (is != null)
			is.close();

		if (clientSocket != null)
			clientSocket.close();
	}

	public void quit() throws IOException
	{
		running = false;
		sendQuit();
	}

	public void setRunning(boolean running)
	{
		this.running = running;
	}


}
