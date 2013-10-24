package com.tsp.server.controller.TCP;

import com.tsp.packets.Packet;
import com.tsp.server.model.ServerModel;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/19/13
 * Time: 12:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class TCPServer extends Thread
{
	private static ServerSocket serverSocket = null;
	// The client socket.
	private static Socket clientSocket = null;

	// This chat server can accept up to maxClientsCount clients' connections.
	private static final int maxClientsCount = 8;
	private static final clientThread[] threads = new clientThread[maxClientsCount];
	private final ServerModel serverModel;

	public TCPServer(ServerModel sm) throws IOException
	{
		serverSocket = new ServerSocket(12000);
		serverModel = sm;
	}

	@Override
	public void run()
	{
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				int i = 0;
				for (i = 0; i < maxClientsCount; i++) {
					if (threads[i] == null) {
						(threads[i] = new clientThread(clientSocket, threads, serverModel)).start();
						break;
					}
				}
				if (i == maxClientsCount) {
					PrintStream os = new PrintStream(clientSocket.getOutputStream());
					os.println("Server too busy. Try later.");
					os.close();
					clientSocket.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}

	public static void addOutGoingPacket(Packet e)
	{
		threads[0].addOutGoingPacket(e);
	}
}
