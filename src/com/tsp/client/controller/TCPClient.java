package com.tsp.client.controller;

import com.tsp.client.model.GameModel;
import com.tsp.game.actors.Actor;
import com.tsp.game.actors.Player;
import com.tsp.packets.ActorPacket;
import com.tsp.packets.Packet;
import com.tsp.packets.QuitPacket;
import com.tsp.util.Rolling;
import com.tsp.util.SocketIO;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

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
	private final Rolling rolling = new Rolling(100);
	InetAddress addr;
	SocketChannel clientSocket;
	GameModel model;
	int port = 12000;
	boolean running = true;
	//DataInputStream is;
	//DataOutputStream os;
    SocketIO socketIO;

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
	
	public int getPort()
	{
		return port;
	}
	
	public InetAddress getAddr()
	{
		return addr;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run()
	{
		running = true;
		try
		{
			connect();
			model.setDungeon(getDungeon());
			sendName(model.getMe().getName());
			int id = this.getID();
			if (id == -1)
			{
				//If receive -1: server error: QUIT
				model.setQuit(true);
				while (running)
				{
					try
					{
						Thread.sleep(100);
					}
					catch (Exception e)
					{
					}

				}
			}
			else
			{
				//Continue with client
				model.setID(id);
				model.setMe(getPlayer());

				//Tells the model that every thing is ready
				model.setReady(true);
				Selector selector = Selector.open();
				clientSocket.configureBlocking(false);
				clientSocket.register(selector, SelectionKey.OP_READ);
				while (running)
				{
					try
					{
						this.wait(10);
					} catch (Exception e)
					{
					}
					long start = System.currentTimeMillis();
					selector.select(100);
					Iterator<SelectionKey> it = selector.selectedKeys().iterator();
					while (it.hasNext())
					{
						SelectionKey selKey = it.next();
						it.remove();
						processSelectionKey(selKey);
					}
					long end = System.currentTimeMillis();
					rolling.add(end-start);
				}
			}
		}
		catch (IOException e)
		{
			LOGGER.info("{}", e.toString());
			model.setQuit(true);
		}
		System.out.println("Quiting");
	}

	/**
	 * Gets the dungeon map from the server
	 * @return an array containing the dungeon
	 * @throws IOException when the {@link DataInputStream} errors
	 */
	public String[][][] getDungeon() throws IOException
	{
		int Columns = socketIO.ReadInt();
		int Rows = socketIO.ReadInt();
		int Floors = socketIO.ReadInt();
		String[][][] dungeon = new String[Floors][Columns][Rows];
		for (int z = 0; z < Floors; z++)
			for (int x = 0; x < Columns; x++)
				for (int y = 0; y < Rows; y++)
					dungeon[z][x][y] = socketIO.ReadString();
		return dungeon;
	}

	/**
	 * Gets the ID of the player
	 * @return the ID of the player
	 * @throws IOException when the {@link DataInputStream} errors
	 */
	public int getID() throws IOException
	{
		return socketIO.ReadInt();
	}

	/**
	 * Sends the name of the client/player
	 * @param name The name of the client/player. I.E {@value "Tim"}
	 * @throws IOException when the {@link DataOutputStream} errors
	 */
	public void sendName(String name) throws IOException
	{
		socketIO.WriteString(name);
	}

	/**
	 * Connects to the server
	 * @throws IOException on Socket Error, and data stream errors
	 */
	public void connect() throws IOException
	{
		clientSocket = SocketChannel.open();
        clientSocket.connect(new InetSocketAddress(addr, port));
		socketIO = new SocketIO(clientSocket);
	}

	/**
	 * Receives the player from the server
	 *
	 * @return the {@link Actor} that contains the player information
	 * @throws IOException when the socket has an error
	 */
	private Player getPlayer() throws IOException
	{
		String json = socketIO.ReadString();
		Object object = JSONValue.parse(json);
		if (object != null && object instanceof JSONObject)
		{
			Packet packet = Packet.parseJSONObject((JSONObject) object);
			if (packet.getPacketType() == Packet.PacketType.ACTOR_PACKET)
				if (((ActorPacket) packet).getActor().getType() == Actor.ActorType.ACTOR_PLAYER)
				{
					return (Player)(((ActorPacket) packet).getActor());
				}
		}
		return null;
	}

	private void processSelectionKey(SelectionKey selKey) throws IOException
	{
		// Since the ready operations are cumulative,
		// need to check readiness for each operation
		if (selKey.isValid() && selKey.isConnectable())
		{
			// Get channel with connection request
			SocketChannel sChannel = (SocketChannel) selKey.channel();

			boolean success = sChannel.finishConnect();
			if (!success)
			{
				// An error occurred; handle it

				// Unregister the channel with this selector
				selKey.cancel();
			}
		}
		if (selKey.isValid() && selKey.isReadable())
		{
			// Get channel with bytes to read
			// SocketChannel sChannel = (SocketChannel)selKey.channel();
			model.insertPacket(getPacket());
			// See Reading from a SocketChannel
		}
		if (selKey.isValid() && selKey.isWritable())
		{
			// Get channel that's ready for more bytes
			// SocketChannel sChannel = (SocketChannel)selKey.channel();
			// See Writing to a SocketChannel
		}
	}

	/**
	 * Gets a packet from the server
	 * @return null if nothing was there, and a packet from the JSON sent from the server
	 */
	public Packet getPacket()
	{
		try
		{
			

			String json = socketIO.ReadString();

			

			Object object = JSONValue.parse(json);
			if (object != null && object instanceof JSONObject)
			{
				
				LOGGER.debug("Average Retrieve time: {}", rolling.getAverage());
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
	 * Stops the TCP Thread and then sends the quit packet
	 * @throws IOException
	 */
	public void quit() throws IOException
	{
		running = false;
		sendQuit();
	}

	/**
	 * Sends a quit packet to the server showing the leaving of a player
	 * @throws IOException
	 */
	public void sendQuit() throws IOException
	{
		if (socketIO != null)
		{
			socketIO.WriteString(new QuitPacket().toJSONString());
		}

		if (clientSocket != null)
			clientSocket.close();
	}

	/**
	 * Sets the running value of the TCP thread
	 * @param running
	 */
	public void setRunning(boolean running)
	{
		this.running = running;
	}


}
