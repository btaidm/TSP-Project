package com.tsp.client.view;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.tsp.client.controller.TCPClient;
import com.tsp.client.event.EventType;
import com.tsp.client.event.GameListener;
import com.tsp.client.event.Listenable;
import com.tsp.client.model.GameModel;
import com.tsp.client.event.GameEvent;

import com.googlecode.blacken.colors.ColorNames;
import com.googlecode.blacken.colors.ColorPalette;
import com.googlecode.blacken.swing.SwingTerminal;
import com.googlecode.blacken.terminal.BlackenKeys;
import com.googlecode.blacken.terminal.CursesLikeAPI;
import com.tsp.game.actors.Actor;
import com.tsp.game.map.Point3D;
import com.tsp.packets.ActorPacket;
import com.tsp.packets.ActorUpdate;
import com.tsp.packets.Packet;

public class GameView implements Listenable
{

	SwingTerminal term;
	CursesLikeAPI curses;
	GameModel model;
	boolean quit = false;
	ArrayList<GameListener> listeners;
	private int id;
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

			this.term.init("TSP Rouglike", 25, 80);

			this.curses = new CursesLikeAPI(this.term);
			this.curses.resize(24, 80);

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
		while(model.hasPackets())
		{
			Packet packet = model.getPacket();
			switch (packet.getPacketType())
			{
				case ACTOR_PACKET:
					ActorPacket actorPacket = (ActorPacket)packet;
					model.addActor(actorPacket.getActor());
					break;
				case UPDATE_PACKET:
					ActorUpdate actorUpdate = (ActorUpdate)packet;
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

		int zLevel = this.model.getCurrentLevel();

		//Use the model to draw on the screen
		for (int i = 0; i < this.model.dungeonRows(); i++)
		{
			for (int j = 0; j < this.model.dungeonCols(); j++)
			{
				this.term.set(i, j, this.model.get(i, j, zLevel), model.getColor(j, i, zLevel), 0);
			}
		}
		this.curses.refresh();
	}

	public void process(int ch)
	{
		boolean moved = false;
		switch (ch)
		{
			case BlackenKeys.NO_KEY:
			{
				if (attacked && model.resetAttack())
				{
					attackDelta = null;
					attacked = false;
				}
				break;
			}
			case BlackenKeys.KEY_ESCAPE:
				quit = true;
				break;
			case BlackenKeys.KEY_DOWN:
			case 'j':
				moved = this.model.attemptMove(Point3D.DOWN);
				break;
			case BlackenKeys.KEY_UP:
			case 'k':
				moved = this.model.attemptMove(Point3D.UP);
				break;
			case BlackenKeys.KEY_LEFT:
			case 'h':
				moved = this.model.attemptMove(Point3D.LEFT);
				break;
			case BlackenKeys.KEY_RIGHT:
			case 'l':
				moved = this.model.attemptMove(Point3D.RIGHT);
				break;
			case 'a':
				if(attacked = this.model.attemptAttack(Point3D.LEFT))
				{
					attackDelta = Point3D.LEFT;
				}
				break;
			case 'd':
				if(attacked = this.model.attemptAttack(Point3D.RIGHT))
				{
					attackDelta = Point3D.RIGHT;
				}
				break;
			case 's':
				if(attacked = this.model.attemptAttack(Point3D.DOWN))
				{
					attackDelta = Point3D.DOWN;
				}
				break;
			case 'w':
				if(attacked = this.model.attemptAttack(Point3D.UP))
				{
					attackDelta = Point3D.UP;
				}
				break;
		}
		if(moved)
		{
			Actor player = model.getMe();
			HashMap<String, Object> movement = new HashMap<String, Object>();
			movement.put("ID", player.getId());
			movement.put("X", player.getX());
			movement.put("Y", player.getY());
			movement.put("Z", model.getCurrentLevel());
			fireEvent(EventType.TURN_MOVE, movement);
		}
		if(attacked)
		{
			Actor player = model.getMe();
			HashMap<String, Object> attack = new HashMap<String, Object>();
			attack.put("ID", player.getId());
			attack.put("X", (int)attackDelta.getX());
			attack.put("Y", (int)attackDelta.getY());
			fireEvent(EventType.TURN_ATTACK, attack);
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
