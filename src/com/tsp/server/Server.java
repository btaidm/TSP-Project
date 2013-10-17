package com.tsp.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/14/13
 * Time: 2:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class Server
{
	public static void main(String[] args) throws Exception
	{
		DatagramSocket serverSocket = new DatagramSocket(12000, InetAddress.getLoopbackAddress());
		byte[] receiveData = new byte[1024];
		while(true)
		{
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			new Thread(new RespondeWorker(serverSocket, receivePacket)).start();
		}
	}
}
