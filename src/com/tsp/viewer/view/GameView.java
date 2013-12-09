package com.tsp.viewer.view;

import com.googlecode.blacken.colors.ColorNames;
import com.googlecode.blacken.colors.ColorPalette;
import com.googlecode.blacken.swing.SwingTerminal;
import com.googlecode.blacken.terminal.BlackenKeys;
import com.googlecode.blacken.terminal.CursesLikeAPI;
import com.googlecode.blacken.terminal.TerminalScreenSize;
import com.tsp.packets.ActorPacket;
import com.tsp.packets.ActorUpdate;
import com.tsp.packets.Packet;
import com.tsp.viewer.controller.TCPClient;
import com.tsp.viewer.event.EventType;
import com.tsp.viewer.event.GameEvent;
import com.tsp.viewer.event.GameListener;
import com.tsp.viewer.event.Listenable;
import com.tsp.viewer.model.GameModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GameView implements Listenable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GameView.class);

	SwingTerminal term;
	CursesLikeAPI curses;
	GameModel model;
	boolean quit = false;
	ArrayList<GameListener> listeners;
	boolean attacked = false;
	private TCPClient tcpClient;
	private Point attackDelta;

	public GameView(GameModel model, TCPClient tcpClient) throws InterruptedException
	{

		this.model = model;
		this.tcpClient = tcpClient;
		this.listeners = new ArrayList<GameListener>();
		if (setUp())
		{
			this.term = new SwingTerminal();

			this.term.init("TSP Roguelike", 26 * 2 + 2, 86 * 2 + 2, TerminalScreenSize.SIZE_MAX);
			//term.resize(25 * 2 + 2, 80 * 2 + 2);

			this.curses = new CursesLikeAPI(this.term);
			//this.curses.resize(25 * 2 + 2, 80 * 2 + 2);

			ColorPalette palette = new ColorPalette();
			palette.addAll(ColorNames.XTERM_256_COLORS, false);
			palette.putMapping(ColorNames.SVG_COLORS);

			this.curses.setPalette(palette);
		}

	}

	public boolean setUp() throws InterruptedException
	{
		tcpClient.start();
		while (!model.getReady() && !model.getQuit())
		{
			Thread.sleep(20);
		}
		if (model.getQuit())
		{
			quit = true;
			return false;
		}
		return true;
	}


	public void play() throws IOException
	{
		// Main loop of the game happens here
		int ch = BlackenKeys.NO_KEY;
		while (!quit && !model.getQuit())
		{
			processPackets();
			ch = this.curses.getch(50);
			process(ch);
			refresh();

			fireEvent(EventType.TURN_END, new HashMap<String, Object>());
		}

		this.close();
	}

	private void processPackets()
	{
		while (model.hasPackets())
		{
			Packet packet = model.getPacket();
			switch (packet.getPacketType())
			{
				case ACTOR_PACKET:
					ActorPacket actorPacket = (ActorPacket) packet;
					model.addActor(actorPacket.getActor());
					break;
				case UPDATE_PACKET:
					ActorUpdate actorUpdate = (ActorUpdate) packet;
					model.update(actorUpdate);
					break;
				default:
					break;
			}
		}
	}

	private void close() throws IOException
	{
		if (curses != null)
			curses.quit();

		if (term != null)
			term.quit();
		tcpClient.quit();
	}

	public void refresh()
	{
		this.curses.clear();
		this.curses.setCursorLocation(-1, -1);


		//Use the model to draw on the screen

		for (int i = 0; i < 4; i++)
		{
			drawFloor(i);
		}

		this.curses.refresh();
	}

	public void drawFloor(int floor)
	{
		int xOffset = 0;
		int yOffset = 0;
		switch (floor)
		{
			case 0:
			{
				xOffset = 0;
				yOffset = 0;
				break;
			}
			case 1:
			{
				xOffset = 81;
				yOffset = 0;
				break;
			}
			case 2:
			{
				xOffset = 0;
				yOffset = 25;
				break;
			}
			case 3:
			{
				xOffset = 81;
				yOffset = 25;
				break;
			}
		}

		for (int i = 0; i < this.model.getDungeon().getRows(); i++)
		{
			for (int j = 0; j < this.model.getDungeon().getColumns(); j++)
			{
				this.term.set(i + yOffset,
				              j + xOffset,
				              this.model.getSymbol(i, j, floor),
				              model.getColor(j, i, floor),
				              0);
			}
		}

	}

	public void process(int ch)
	{
		boolean moved = false;
		switch (ch)
		{
			default:
			case BlackenKeys.NO_KEY:
			{
				break;
			}
			case BlackenKeys.KEY_ESCAPE:
				quit = true;
				break;
		}
	}

	@Override
	public void addListener(GameListener gl)
	{
		this.listeners.add(gl);
	}

	@Override
	public void removeListener(GameListener gl)
	{
		this.listeners.remove(gl);
	}

	@Override
	public void fireEvent(EventType e, HashMap<String, Object> payload)
	{
		GameEvent ge = new GameEvent(e, payload);

		for (GameListener l : this.listeners)
		{
			l.receiveEvent(ge);
		}
	}
}
