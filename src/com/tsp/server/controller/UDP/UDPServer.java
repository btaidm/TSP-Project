package com.tsp.server.controller.UDP;

import com.tsp.packets.Packet;
import com.tsp.server.model.ServerModel;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/14/13
 * Time: 2:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class UDPServer extends Thread
{
	private static boolean quit = false;
	private static ByteBuffer sendBuf = ByteBuffer.allocate(1024);
	private static DatagramChannel serverChannel = null;

	ServerModel model;
	private static ArrayList<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();

	public UDPServer(ServerModel serverModel)
	{
		super("UDP Server");
		this.model = serverModel;
	}

	@Override
	public void run()
	{
		//DatagramSocket serverSocket = null;
		try
		{
			//serverSocket = new DatagramSocket(12000);
			(serverChannel = DatagramChannel.open()).socket().bind(new InetSocketAddress(12000));
			while (!quit)
			{
				//byte[] receiveData = new byte[1024];
				//DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				ByteBuffer recBuf = ByteBuffer.allocate(1024);
				recBuf.clear();
				//serverSocket.receive(receivePacket);
				InetSocketAddress receiveAddr = (InetSocketAddress) serverChannel.receive(recBuf);
				if (!addresses.contains(receiveAddr)) addresses.add(receiveAddr);
				new Thread(new RespondWorker(serverChannel, recBuf, receiveAddr, model)).start();
				Thread.sleep(50);
			}
		}
		catch (SocketException e)
		{
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		catch (IOException e)
		{
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		finally
		{
			if (serverChannel != null && serverChannel.isOpen())
				try
				{
					serverChannel.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
		}
	}

	public static void quit()
	{
		quit = true;
	}

	public static void sendPacket(Packet e)
	{
		for (InetSocketAddress addr : addresses)
		{
			sendBuf.clear();
			sendBuf.put(e.toJSONString().getBytes());
			sendBuf.flip();

			try
			{
				serverChannel.send(sendBuf, addr);
			}
			catch (IOException ex)
			{
			}
		}
	}
}
