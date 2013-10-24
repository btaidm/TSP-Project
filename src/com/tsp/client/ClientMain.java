package com.tsp.client;

import com.tsp.client.controller.ConnectionController;
import com.tsp.client.controller.GameController;
import com.tsp.client.controller.TCPClient;
import com.tsp.client.controller.StartupController;
import com.tsp.client.model.GameModel;
import com.tsp.client.view.GameView;
import com.tsp.client.view.StartupView;

import java.io.IOException;
import java.net.UnknownHostException;


public class ClientMain
{
	public static void main(String[] arguments)
	{
		StartupController sc = new StartupController();
		StartupView sv = new StartupView(sc);
		sv.setVisible(true);

		GameModel gm = new GameModel();
		TCPClient tcpClient = null;
		try
		{
			tcpClient = new TCPClient(gm);
			GameView gv = new GameView(gm, tcpClient);
			GameController gc = new GameController();
			ConnectionController cc = new ConnectionController();

			gv.addListener(gc);
			gv.addListener(cc);
			gv.play();
		}
		catch (UnknownHostException e)
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

	}
}
