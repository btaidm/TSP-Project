package com.tsp.client;

import com.tsp.client.controller.ConnectionController;
import com.tsp.client.controller.GameController;
import com.tsp.client.controller.TCPClient;
import com.tsp.client.controller.StartupController;
import com.tsp.client.model.GameModel;
import com.tsp.client.view.GameView;
import com.tsp.client.view.StartupView;
import com.tsp.server.controller.TCP.TCPServer;
import com.tsp.server.controller.UDP.UDPServer;
import com.tsp.server.model.ServerModel;

import java.io.IOException;
import java.net.UnknownHostException;


public class ClientMain
{
	static ServerModel serverModel = null;
	public static void main(String[] arguments) throws IOException
	{

		StartupController sc = new StartupController();
		StartupView sv = new StartupView(sc);
		sv.setVisible(true);
		Thread server = null;
		// Wait for user to start game
		while (!sc.hasGameStarted())
		{
			// Run server if user wants to host
			if (sc.isHost())
			{
				sv.dispose();
				(server = new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							runServer();
						}
						catch (IOException e)
						{
							e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
						}
					}
				})).start();
				break;
			}
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		// Launch client
		if (sv.isVisible())
			sv.dispose();
		runClient(sc, server);
		System.exit(0);
	}

	private static void runClient(StartupController sc, Thread server) throws IOException
	{
		GameModel gm = new GameModel(
				sc.getPlayer() != null ? sc.getPlayer().trim().equals("") ? "Player" : sc.getPlayer()
						.trim() : "Player");
		TCPClient tcpClient = new TCPClient(gm,
		                                    sc.getPlayer() != null ? (sc.getServer().trim()
				                                    .equals("") ? "localhost" : sc.getServer().trim()) : "localhost",
		                                    12000);
		GameView gv = null;
		try
		{
			gv = new GameView(gm, tcpClient);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		GameController gc = new GameController();
		ConnectionController cc = new ConnectionController(sc.getServer());

		gv.addListener(gc);
		gv.addListener(cc);
		gv.play();
		if(server != null)
		{
			serverModel.quit();
		}
	}

	private static void runServer() throws IOException
	{
		serverModel = new ServerModel();
		UDPServer udpServer = new UDPServer(serverModel);
		TCPServer tcpServer = new TCPServer(serverModel);
		tcpServer.start();
		udpServer.start();
		serverModel.run();
	}
}
