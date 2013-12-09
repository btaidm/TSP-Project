package com.tsp.client.controller;

import com.tsp.client.model.GameModel;
import com.tsp.packets.Packet;
import com.tsp.util.Rolling;
import com.tsp.util.SocketIO;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;

/**
 * Created by Tim on 12/9/13.
 */
public class UDPClient extends Thread
{
	/**
	 *
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TCPClient.class);
	private final Rolling rolling = new Rolling(100);
	InetAddress addr;
	DatagramChannel clientSocket;
	GameModel model;
	int port = 12000;
	boolean running = true;
	ByteBuffer recBuf = ByteBuffer.allocate(1024);
	//DataInputStream is;
	//DataOutputStream os;

	public UDPClient(DatagramChannel clientSocket)
	{
		this.clientSocket = clientSocket;
	}

	public UDPClient(GameModel model, DatagramChannel clientSocket)
	{
		this.model = model;
		this.clientSocket = clientSocket;
	}

	public void setModel(GameModel model)
	{
		this.model = model;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run()
	{
		running = true;
		//connect();
		while (running)
		{
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			model.insertPacket(getPacket());
		}

		System.out.println("Quiting");
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
			recBuf.clear();
			clientSocket.receive(recBuf);
			recBuf.flip();
			CharBuffer json = Charset.defaultCharset().decode(recBuf);
			Object object = JSONValue.parse(json.toString());
			if (object != null && object instanceof JSONObject)
			{
				//LOGGER.debug("Average Retrieve time: {}", rolling.getAverage());
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
	 *
	 * @throws IOException
	 */
	public void quit() throws IOException
	{
		running = false;
	}

	/**
	 * Sends a quit packet to the server showing the leaving of a player
	 *
	 * @throws IOException
	 */
	public void sendQuit() throws IOException
	{
		if (clientSocket != null)
			clientSocket.close();
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
