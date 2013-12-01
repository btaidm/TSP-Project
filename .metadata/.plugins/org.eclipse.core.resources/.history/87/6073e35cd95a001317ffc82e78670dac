package com.tsp.server;

import com.tsp.server.controller.TCP.TCPServer;
import com.tsp.server.controller.UDP.UDPServer;
import com.tsp.server.model.ServerModel;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/17/13
 * Time: 10:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class ServerMain
{

	public ServerMain()
	{

	}

	public static void main(String[] args) throws Exception
	{
		ServerModel serverModel = new ServerModel();
		UDPServer udpServer = new UDPServer(serverModel);
		TCPServer tcpServer = new TCPServer(serverModel);
		tcpServer.start();
		udpServer.start();

		serverModel.run();
	}
}
