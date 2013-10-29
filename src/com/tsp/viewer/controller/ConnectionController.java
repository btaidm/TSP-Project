package com.tsp.viewer.controller;

import com.tsp.viewer.event.GameEvent;
import com.tsp.viewer.event.GameListener;
import com.tsp.packets.ActorUpdate;
import com.tsp.packets.AttackPacket;
import com.tsp.packets.MovementPacket;
import com.tsp.packets.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
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
	private DatagramSocket socket;
	InetAddress addr;
	int port = 12000;

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
			socket = new DatagramSocket();
		}
		catch (SocketException e)
		{
			LOGGER.error("{}", e);
			socket = null;
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
			socket = new DatagramSocket();
		}
		catch (SocketException e)
		{
			LOGGER.error("{}", e);
			socket = null;
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
			socket = new DatagramSocket();
		}
		catch (SocketException e)
		{
			LOGGER.error("{}", e);
			socket = null;
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
			socket = new DatagramSocket();
		}
		catch (SocketException e)
		{
			LOGGER.error("{}", e);
			socket = null;
		}
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
			default:
				break;
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
		                               (Integer) e.payload.get("X"),
		                               (Integer) e.payload.get("Y"),
		                               (Integer) e.payload.get("Z")));
	}

	/**
	 * Processes the end of turn
	 */
	private void processTick()
	{
		for (Packet packet : packets)
		{
			if (socket != null)
			{
				LOGGER.info("Sending Packet @ {}: {}", System.currentTimeMillis(), packet);
				byte[] data = packet.toJSONString().getBytes();
				DatagramPacket response = new DatagramPacket(data,
				                                             data.length,
				                                             addr,
				                                             12000);
				try
				{
					socket.send(response);
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
