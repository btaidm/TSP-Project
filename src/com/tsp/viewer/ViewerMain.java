package com.tsp.viewer;

import com.tsp.viewer.controller.GameController;
import com.tsp.viewer.controller.StartupController;
import com.tsp.viewer.controller.TCPClient;
import com.tsp.viewer.model.GameModel;
import com.tsp.viewer.view.GameView;
import com.tsp.viewer.view.StartupView;

import java.io.IOException;
import java.net.UnknownHostException;


public class ViewerMain
{
	public static void main(String[] arguments)
	{
		StartupController sc = new StartupController();
		StartupView sv = new StartupView(sc);
		sv.setVisible(true);

		TCPClient tcpClient = null;

		while (!sc.hasGameStarted())
		{
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try
		{
			if (!sc.isHost())
			{
				sv.dispose();
				GameModel gm = new GameModel((sc.getPlayer().trim()
						.equals("") ? "Player" : sc.getPlayer().trim()));
				tcpClient = new TCPClient(gm,
				                          (sc.getServer().trim().equals("") ? "localhost" : sc
						                          .getServer().trim()),
				                          12000);
				GameView gv = new GameView(gm, tcpClient);
				GameController gc = new GameController();

				gv.addListener(gc);
				gv.play();
			}
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
		System.exit(0);
	}
}
