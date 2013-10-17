package com.tsp.client.controller;

import com.tsp.client.event.GameEvent;
import com.tsp.client.event.GameListener;
import com.tsp.packets.MovementPacket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;

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
	private JSONArray array;
	private DatagramSocket socket;

	public ConnectionController()
	{
		this.array = new JSONArray();
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
			default:
				break;
		}
	}

	private void processMove(GameEvent e)
	{

		array.add(new MovementPacket((Integer) e.payload.get("ID"),
		                             (Integer) e.payload.get("X"),
		                             (Integer) e.payload.get("Y"),
		                             false,
		                             0,
		                             0));
	}

	private void processTick()
	{
		//for (Object o : array)
		//{
		//	LOGGER.info("Sending Movement @ {}: {}", System.currentTimeMillis(), o);
		//}
		if (socket != null && !array.isEmpty())
		{
			LOGGER.info("Sending Array @ {}: {}", System.currentTimeMillis(), array);
			byte[] data = array.toString().getBytes();
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
			array.clear();
		}
	}
}
