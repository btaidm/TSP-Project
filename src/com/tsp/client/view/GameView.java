package com.tsp.client.view;

import java.awt.Point;
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
import com.tsp.game.map.Point3D;
import com.tsp.server.controller.TCP.TCPServer;

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

	public GameView(GameModel model, TCPClient tcpClient)
	{

		this.term = new SwingTerminal();
		this.term.init("TSP Rouglike", 25, 80);

		this.curses = new CursesLikeAPI(this.term);
		this.curses.resize(24, 80);

		ColorPalette palette = new ColorPalette();
		palette.addAll(ColorNames.XTERM_256_COLORS, false);
		palette.putMapping(ColorNames.SVG_COLORS);

		this.curses.setPalette(palette);

		this.model = model;
		this.tcpClient = tcpClient;
		this.listeners = new ArrayList<GameListener>();
	}

	public void setUp()
	{
		tcpClient.
	}


	public void play()
	{
		// Main loop of the game happens here
		int ch = BlackenKeys.NO_KEY;
		while (!quit)
		{
			ch = this.curses.getch(50);
			process(ch);
			refresh();

			fireEvent(EventType.TURN_END, new HashMap<String, Object>());
		}
		curses.quit();
		term.quit();
		this.close();
	}

	private void close()
	{

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
				int color = 255;
				if(this.model.getPlayerLocation().equals(new Point3D(j,i,zLevel)))
				{
					color = 255 / 2;
				}
				this.term.set(i, j, this.model.get(i, j, zLevel), color, 0);
			}
		}
		this.curses.refresh();
	}

	public void process(int ch)
	{
		switch (ch)
		{
			case BlackenKeys.NO_KEY:
			{
				if(attacked)
				{
					model.resetAttack();
				}
				break;
			}
			case BlackenKeys.KEY_ESCAPE:
				quit = true;
				break;
			case BlackenKeys.KEY_DOWN:
			case 'j':
				if (this.model.attemptMove(GameModel.DOWN))
				{
					HashMap<String, Object> movement = new HashMap<String, Object>();
					movement.put("ID", id);
					movement.put("X", 0);
					movement.put("Y", 1);
					movement.put("Z", model.getCurrentLevel());
					fireEvent(EventType.TURN_MOVE, movement);
				}
				break;
			case BlackenKeys.KEY_UP:
			case 'k':
				if (this.model.attemptMove(GameModel.UP))
				{
					HashMap<String, Object> movement = new HashMap<String, Object>();
					movement.put("ID", 0);
					movement.put("X", 0);
					movement.put("Y", -1);
					movement.put("Z", model.getCurrentLevel());
					fireEvent(EventType.TURN_MOVE, movement);
				}
				break;
			case BlackenKeys.KEY_LEFT:
			case 'h':
				if (this.model.attemptMove(GameModel.LEFT))
				{
					HashMap<String, Object> movement = new HashMap<String, Object>();
					movement.put("ID", 0);
					movement.put("X", -1);
					movement.put("Y", 0);
					movement.put("Z", model.getCurrentLevel());
					fireEvent(EventType.TURN_MOVE, movement);
				}
				break;
			case BlackenKeys.KEY_RIGHT:
			case 'l':
				if (this.model.attemptMove(GameModel.RIGHT))
				{
					HashMap<String, Object> movement = new HashMap<String, Object>();
					movement.put("ID", 0);
					movement.put("X", 1);
					movement.put("Y", 0);
					movement.put("Z", model.getCurrentLevel());
					fireEvent(EventType.TURN_MOVE, movement);
				}
				break;
			case 'a':
				attacked = this.model.attemptAttack(GameModel.LEFT);
				break;
			case 'd':
				attacked = this.model.attemptAttack(GameModel.RIGHT);
				break;
			case 's':
				attacked = this.model.attemptAttack(GameModel.DOWN);
				break;
			case 'w':
				attacked = this.model.attemptAttack(GameModel.UP);
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
