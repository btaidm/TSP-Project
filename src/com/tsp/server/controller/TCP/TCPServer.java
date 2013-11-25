package com.tsp.server.controller.TCP;

import com.tsp.packets.Packet;
import com.tsp.server.model.ServerModel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/19/13
 * Time: 12:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class TCPServer extends Thread
{
	//private static ServerSocket serverSocket = null;
	private static ServerSocketChannel serverSocketChannel = null;
	// The client socket.
	//private static Socket clientSocket = null;
	private static SocketChannel clientSocketChannel = null;

	// This chat server can accept up to maxClientsCount clients' connections.
	private static final int maxClientsCount = 8;
	private static final clientThread[] threads = new clientThread[maxClientsCount];
	private final ServerModel serverModel;
	private static boolean quit = false;

	public TCPServer(ServerModel sm) throws IOException
	{
		super("TCP Server");
		//serverSocket = new ServerSocket(12000);
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(12000), 8);
		serverModel = sm;
	}

	@Override
	public void run()
	{
		while (!quit)
		{
			try
			{
				clientSocketChannel = serverSocketChannel.accept();
				int i = 0;
				for (i = 0; i < maxClientsCount; i++)
				{
					if (threads[i] == null)
					{
						(threads[i] = new clientThread(clientSocketChannel, threads, serverModel)).start();
						break;
					}
				}
				if (i == maxClientsCount)
				{
					clientSocketChannel.close();
				}
				Thread.sleep(50);
			}
			catch (IOException e)
			{
				System.out.println(e);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}

		}
	}

	public static void addOutGoingPacket(Packet e)
	{
		for (int i = 0; i < maxClientsCount; i++)
		{
			if (threads[i] != null)
			{
				threads[i].addOutGoingPacket(e);
			}
		}
	}

	public static void quit() throws IOException
	{
		for (int i = 0; i < maxClientsCount; i++)
		{
			if (threads[i] != null)
			{
				threads[i].quit();
			}
		}
	}
}
