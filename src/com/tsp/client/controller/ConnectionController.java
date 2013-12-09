package com.tsp.client.controller;

import com.tsp.client.event.GameEvent;
import com.tsp.client.event.GameListener;
import com.tsp.packets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Map;

/**
 * The Connection Controller is a controller for sending data to the UDP portion of the server
 *
 * @author Tim Bradt <tjbradt@mtu.edu>
 * @version v1.0
 * @see GameListener
 * @since v1.0
 */
public class ConnectionController implements GameListener
{
	/**
	 * This is the logger for the {@link ConnectionController}
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionController.class);
	ArrayList<Packet> packets;
	private DatagramChannel socket;
	InetAddress addr;
	int port = 12000;
	ByteBuffer sendBuf = ByteBuffer.allocate(1024);
	ByteBuffer recBuf = ByteBuffer.allocate(1024);

	/**
	 * Creates a new Connection Controller with the default network address of the localhost
	 *
	 * @throws UnknownHostException if the host is unknown
	 */
	public ConnectionController() throws UnknownHostException
	{
		this.packets = new ArrayList<Packet>();
		addr = InetAddress.getLocalHost();
		try
		{
			socket = DatagramChannel.open();
			socket.socket().bind(new InetSocketAddress(0));
		}
		catch (SocketException e)
		{
			LOGGER.error("{}", e);
			socket = null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new Connection Controller with network address of the given address
	 *
	 * @param address the address the client connects to
	 * @throws UnknownHostException if the host is unknown
	 */
	public ConnectionController(InetAddress address) throws UnknownHostException
	{
		this.packets = new ArrayList<Packet>();
		addr = address;
		try
		{
			socket = DatagramChannel.open();
			socket.socket().bind(new InetSocketAddress(0));
		}
		catch (SocketException e)
		{
			LOGGER.error("{}", e);
			socket = null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Creates a ConnectionController based on a hostname
	 *
	 * @param hostname the hostname of the server
	 * @throws UnknownHostException if the host is unknown
	 */
	public ConnectionController(String hostname) throws UnknownHostException
	{
		this.packets = new ArrayList<Packet>();
		addr = InetAddress.getByName(hostname);
		try
		{
			socket = DatagramChannel.open();
			socket.socket().bind(new InetSocketAddress(0));
		}
		catch (SocketException e)
		{
			LOGGER.error("{}", e);
			socket = null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Creates a ConnectionController based on a hostname and port
	 *
	 * @param hostname the hostname of the server
	 * @param port     the port of the server
	 * @throws UnknownHostException if the host is unknown
	 */
	public ConnectionController(String hostname, int port) throws UnknownHostException
	{
		this.packets = new ArrayList<Packet>();
		addr = InetAddress.getByName(hostname);
		this.port = port;
		try
		{
			socket = DatagramChannel.open();
			socket.socket().bind(new InetSocketAddress(0));
		}
		catch (SocketException e)
		{
			LOGGER.error("{}", e);
			socket = null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public UDPClient createUDPListener()
	{
		return new UDPClient(socket);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param e
	 */
	@Override
	public void receiveEvent(GameEvent e)
	{
		switch (e.type)
		{
			case TURN_END:
				processTick();
				break;
			case TURN_MOVE:
				processMove(e);
				break;
			case TURN_ATTACK:
				processAttack(e);
				break;
			case TURN_UPDATE:
				processUpdate(e);
				break;
			case TURN_INIT:
				processInit();
				break;
			default:
				break;
		}
	}

	private void processInit()
	{
		sendBuf.clear();
		sendBuf.put((new MessagePacket("")).toJSONString().getBytes());
		sendBuf.flip();

		try
		{
			socket.send(sendBuf, new InetSocketAddress(addr, port));
		}
		catch (IOException e)
		{
			LOGGER.error("{}", e);
		}
	}

	private void processUpdate(GameEvent e)
	{
		if (e.payload.containsKey("ID"))
		{
			ActorUpdate actorUpdate = new ActorUpdate((Integer) e.payload.get("ID"));
			e.payload.remove("ID");
			for (Map.Entry<String, Object> entry : e.payload.entrySet())
			{
				actorUpdate.insertValue(entry.getKey(), entry.getValue());
			}
			packets.add(actorUpdate);
		}
	}

	/**
	 * Processes an attack event
	 *
	 * @param e {@link GameEvent} that contains an attack
	 */
	private void processAttack(GameEvent e)
	{
		packets.add(new AttackPacket((Integer) e.payload.get("ID"),
		                             (Integer) e.payload.get("X"),
		                             (Integer) e.payload.get("Y")));
	}

	/**
	 * Processes a move event
	 *
	 * @param e {@link GameEvent} that contains a movement
	 */
	private void processMove(GameEvent e)
	{

		packets.add(new MovementPacket((Integer) e.payload.get("ID"),
		                               ((Double) e.payload.get("X")).intValue(),
		                               ((Double) e.payload.get("Y")).intValue(),
		                               (Integer) e.payload.get("Z")));
	}

	/**
	 * Processes the end of turn
	 */
	private void processTick()
	{
		if (System.currentTimeMillis() % 400 < 20)
		{
			packets.add(new MessagePacket(""));
		}
		for (Packet packet : packets)
		{
			if (socket != null)
			{
				LOGGER.info("Sending Packet @ {}: {}", System.currentTimeMillis(), packet);
				sendBuf.clear();
				sendBuf.put(packet.toJSONString().getBytes());
				sendBuf.flip();

				try
				{
					socket.send(sendBuf, new InetSocketAddress(addr, port));
				}
				catch (IOException e)
				{
					LOGGER.error("{}", e);
				}
			}
		}
		packets.clear();
	}
}
