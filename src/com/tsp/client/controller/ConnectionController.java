package com.tsp.client.controller;

import com.tsp.client.event.GameEvent;
import com.tsp.client.event.GameListener;
import com.tsp.packets.AttackPacket;
import com.tsp.packets.MovementPacket;
import com.tsp.packets.Packet;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/16/13
 * Time: 11:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionController implements GameListener
{

	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionController.class);
	ArrayList<Packet> packets;
	private DatagramSocket socket;
	InetAddress addr;
	int port = 12000;

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
			default:
				break;
		}
	}

	private void processAttack(GameEvent e)
	{
		packets.add(new AttackPacket((Integer) e.payload.get("ID"),
		                             (Integer) e.payload.get("X"),
		                             (Integer) e.payload.get("Y")));
	}

	private void processMove(GameEvent e)
	{

		packets.add(new MovementPacket((Integer) e.payload.get("ID"),
		                               (Integer) e.payload.get("X"),
		                               (Integer) e.payload.get("Y"),
		                               (Integer) e.payload.get("Z")));
	}

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
				                                             InetAddress.getLoopbackAddress(),
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
