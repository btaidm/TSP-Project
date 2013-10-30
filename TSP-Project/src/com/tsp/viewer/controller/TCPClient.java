package com.tsp.viewer.controller;

import com.tsp.viewer.model.GameModel;
import com.tsp.game.actors.Actor;
import com.tsp.game.actors.Player;
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
 * The TCP Client is the main communication between the server and client
 * <p>It is a threaded part of the client at receives packets from the server
 * and then puts the parsed packet on an incoming packet queue in the game model</p>
 *
 * @author Tim Bradt <tjbradt@mtu.edu>
 * @version v1.0
 * @since v1.0
 */
public class TCPClient extends Thread
{
	/**
	 *
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TCPClient.class);
	GameModel model;
	InetAddress addr;
	int port = 12000;
	Socket clientSocket;
	DataInputStream is;
	DataOutputStream os;
	boolean running = true;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run()
	{
		try
		{
			connect();
			model.setDungeon(getDungeon());
			sendName("TSPVIEWER");
			int id = this.getID();
			model.setID(id);
			model.setReady(true);
			//Sets a timeout for continuous operation
			clientSocket.setSoTimeout(100);
			while (running)
			{
				synchronized (this)
				{
					//Gets a packet and put it on the queue
					model.insertPacket(getPacket());
					try
					{
						Thread.sleep(50);
					}
					catch (InterruptedException e)
					{

					}
				}
			}

		}
		catch (IOException e)
		{
			LOGGER.info("{}", e.toString());
			model.setQuit(true);
		}
	}

	/**
	 * Receives the player from the server
	 *
	 * @return the {@link Actor} that contains the player information
	 * @throws IOException when the socket has an error
	 */
	private Player getPlayer() throws IOException
	{
		String json = is.readUTF();
		Object object = JSONValue.parse(json);
		if (object != null && object instanceof JSONObject)
		{
			Packet packet = Packet.parseJSONObject((JSONObject) object);
			if (packet.getPacketType() == Packet.PacketType.ACTOR_PACKET)
				if (((ActorPacket) packet).getActor().getType() == Actor.ActorType.ACTOR_PLAYER)
				{
					return (Player) (((ActorPacket) packet).getActor());
				}
		}
		return null;
	}

	public TCPClient(GameModel model) throws UnknownHostException
	{
		this.model = model;
		addr = InetAddress.getLocalHost();
	}

	/**
	 * Creates a TCPClient with address set to given address and given port
	 *
	 * @param model   the {@link GameModel} that contains the game data
	 * @param address the address of the server
	 * @param port    the port of the server
	 */
	public TCPClient(GameModel model, InetAddress address, int port)
	{
		this.model = model;
		addr = address;
		this.port = port;
	}

	/**
	 * Creates a TCPClient with address set to given address and given port
	 *
	 * @param model the {@link GameModel} that contains the game data
	 * @param host  the hostname of the server
	 * @param port  the port of the server
	 * @throws UnknownHostException when the host name is unknown
	 */
	public TCPClient(GameModel model, String host, int port) throws UnknownHostException
	{
		this.model = model;
		addr = InetAddress.getByName(host);
		this.port = port;
	}

	/**
	 * Connects to the server
	 *
	 * @throws IOException on Socket Error, and data stream errors
	 */
	public void connect() throws IOException
	{
		clientSocket = new Socket(addr, port);
		is = new DataInputStream(clientSocket.getInputStream());
		os = new DataOutputStream(clientSocket.getOutputStream());
	}

	/**
	 * Sends the name of the client/player
	 *
	 * @param name The name of the client/player. I.E {@value "Tim"}
	 * @throws IOException when the {@link DataOutputStream} errors
	 */
	public void sendName(String name) throws IOException
	{
		os.writeUTF(name);
	}

	/**
	 * Gets the ID of the player
	 *
	 * @return the ID of the player
	 * @throws IOException when the {@link DataInputStream} errors
	 */
	public int getID() throws IOException
	{
		return is.readInt();
	}

	/**
	 * Gets the dungeon map from the server
	 *
	 * @return an array containing the dungeon
	 * @throws IOException when the {@link DataInputStream} errors
	 */
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

	/**
	 * Gets a packet from the server
	 *
	 * @return null if nothing was there, and a packet from the JSON sent from the server
	 */
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

	/**
	 * Sends a quit packet to the server showing the leaving of a player
	 *
	 * @throws IOException
	 */
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

	/**
	 * Stops the TCP Thread and then sends the quit packet
	 *
	 * @throws IOException
	 */
	public void quit() throws IOException
	{
		running = false;
		sendQuit();
	}

	/**
	 * Sets the running value of the TCP thread
	 *
	 * @param running
	 */
	public void setRunning(boolean running)
	{
		this.running = running;
	}


}
