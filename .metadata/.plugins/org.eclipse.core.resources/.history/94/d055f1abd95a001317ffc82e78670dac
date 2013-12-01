package com.tsp.server.controller.TCP;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsp.game.actors.Actor;
import com.tsp.packets.ActorPacket;
import com.tsp.packets.Packet;
import com.tsp.packets.ScorePacket;
import com.tsp.server.model.ServerModel;
import com.tsp.util.KDTuple;
import com.tsp.util.SocketIO;

/**
 * Created with IntelliJ IDEA. User: Tim Date: 10/19/13 Time: 1:37 PM To change
 * this template use File | Settings | File Templates.
 */
class clientThread extends Thread
{
	private static final Logger LOGGER = LoggerFactory
			.getLogger(clientThread.class);

	private String clientName = null;
	// private DataInputStream is = null;
	// private DataOutputStream os = null;
	private SocketChannel clientSocket = null;
	private final clientThread[] threads;
	private int maxClientsCount;
	private ServerModel serverModel = null;
	private Integer playerID;
	Queue<Packet> outGoingPackets;
	private boolean running = true;
	boolean viewer = false;
	private SocketIO socketIO;

	public void addOutGoingPacket(Packet packet)
	{
		synchronized (this)
		{
			outGoingPackets.add(packet);
		}
	}

	public clientThread(SocketChannel clientSocket, clientThread[] threads,
			ServerModel sm)
	{
		this.outGoingPackets = new LinkedList<Packet>();
		this.clientSocket = clientSocket;
		this.threads = threads;
		maxClientsCount = threads.length;
		serverModel = sm;
		socketIO = new SocketIO(clientSocket);
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
			String name = socketIO.ReadString().trim();

			viewer = name.equals("TSPVIEWER");
			if (!viewer)
			{
				String PlayName = name;
				synchronized (this)
				{
					clientName = PlayName;
					playerID = serverModel.addPlayer(PlayName);
					socketIO.WriteInt(playerID);

					sendPlayer();
					sendNewPlayer();
				}

			} else
			{
				socketIO.WriteInt(-1);
			}
			sendActors();
			Selector selector = Selector.open();
			clientSocket.configureBlocking(false);
			clientSocket.register(selector, clientSocket.validOps());
			// clientSocket.socket().setSoTimeout(100);
			/* Start the conversation. */
			while (running)
			{
				try
				{
					this.wait(10);
				} catch (Exception e)
				{
				}
				selector.select(100);
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (it.hasNext())
				{
					SelectionKey selKey = it.next();
					it.remove();
					processSelectionKey(selKey);
				}

			}

		} catch (IOException e)
		{
			e.printStackTrace();

		} finally
		{
			try
			{
				quit();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
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
			processIncoming();
			// See Reading from a SocketChannel
		}
		if (selKey.isValid() && selKey.isWritable())
		{
			// Get channel that's ready for more bytes
			// SocketChannel sChannel = (SocketChannel)selKey.channel();
			processPackets();
			// See Writing to a SocketChannel
		}
	}

	private void sendPlayer() throws IOException
	{
		ActorPacket player = new ActorPacket(serverModel.getPlayer(playerID));
		socketIO.WriteString(player.toJSONString());
	}

	private void processIncoming()
	{
		try
		{
			String json = socketIO.ReadString();
			Object object = JSONValue.parse(json);
			if (object != null && object instanceof JSONObject)
			{
				Packet packet = Packet.parseJSONObject((JSONObject) object);
				if (packet.getPacketType() == Packet.PacketType.QUIT_PACKET)
					running = false;
			}
		} catch (IOException e)
		{
		}
	}

	private void processPackets() throws IOException
	{
		synchronized (this)
		{
			while (!outGoingPackets.isEmpty())
			{
				Packet packet = outGoingPackets.poll();
				long start = System.currentTimeMillis();
				socketIO.WriteString(packet.toJSONString());
				long end = System.currentTimeMillis();
				LOGGER.debug("Sent Packet: {}: {} ms", packet.toString(), end
						- start);
			}
		}
	}

	public void quit() throws IOException
	{
		/*
		 * Clean up. Set the current thread variable to null so that a new
		 * client could be accepted by the server.
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
			 * Close the output stream, close the input stream, close the
			 * socket.
			 */
			if (!viewer)
				serverModel.removePlayer(playerID);
			// is.close();
			// os.close();
			clientSocket.close();
		}
	}

	private void sendDungeon(String[][][] strings) throws IOException
	{
		socketIO.WriteInt(serverModel.getColumns());
		socketIO.WriteInt(serverModel.getRows());
		socketIO.WriteInt(serverModel.getFloors());
		for (int z = 0; z < serverModel.getFloors(); z++)
			for (int x = 0; x < serverModel.getColumns(); x++)
				for (int y = 0; y < serverModel.getRows(); y++)
				{
					socketIO.WriteString(strings[z][x][y]);
				}
	}

	private void sendActors() throws IOException
	{
		LOGGER.info("Sending Actors");
		ArrayList<Actor> actors = serverModel.getActors();
		for (Actor actor : actors)
		{
			socketIO.WriteString(new ActorPacket(actor).toJSONString());
		}

	}

	protected void sendNewPlayer() throws IOException
	{
		ActorPacket player = new ActorPacket(serverModel.getPlayer(playerID));
		TCPServer.addOutGoingPacket(player);
	}
}
