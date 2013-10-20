package com.tsp.client.view;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

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

public class GameView implements Listenable
{

	SwingTerminal term;
	CursesLikeAPI curses;
	GameModel model;

	ArrayList<GameListener> listeners;

	public GameView(GameModel model)
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
		this.listeners = new ArrayList<GameListener>();
	}

	public void play()
	{
		// Main loop of the game happens here
		int ch = BlackenKeys.NO_KEY;
		while (true)
		{
			ch = this.curses.getch();
			process(ch);
			refresh();

			fireEvent(EventType.TURN_END, new HashMap<String, Object>());
		}
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
				this.term.set(i, j, this.model.get(i, j, zLevel), 255, 0);
			}
		}
		this.curses.refresh();
	}

	public void process(int ch)
	{
		switch (ch)
		{
			case BlackenKeys.KEY_DOWN:
			case 'j':
				if (this.model.attemptMove(GameModel.DOWN))
				{
					HashMap<String, Object> movement = new HashMap<String, Object>();
					movement.put("ID", new Integer(0));
					movement.put("X", GameModel.DOWN.x);
					movement.put("Y", GameModel.DOWN.y);
					fireEvent(EventType.TURN_MOVE, movement);
				}
				break;
			case BlackenKeys.KEY_UP:
			case 'k':
				if (this.model.attemptMove(GameModel.UP))
				{
					HashMap<String, Object> movement = new HashMap<String, Object>();
					movement.put("ID", new Integer(0));
					movement.put("X", GameModel.UP.x);
					movement.put("Y", GameModel.UP.y);
					fireEvent(EventType.TURN_MOVE, movement);
				}
				break;
			case BlackenKeys.KEY_LEFT:
			case 'h':
				if (this.model.attemptMove(GameModel.LEFT))
				{
					HashMap<String, Object> movement = new HashMap<String, Object>();
					movement.put("ID", new Integer(0));
					movement.put("X", GameModel.LEFT.x);
					movement.put("Y", GameModel.LEFT.y);
					fireEvent(EventType.TURN_MOVE, movement);
				}
				break;
			case BlackenKeys.KEY_RIGHT:
			case 'l':
				if (this.model.attemptMove(GameModel.RIGHT))
				{
					HashMap<String, Object> movement = new HashMap<String, Object>();
					movement.put("ID", new Integer(0));
					movement.put("X", GameModel.RIGHT.x);
					movement.put("Y", GameModel.RIGHT.y);
					fireEvent(EventType.TURN_MOVE, movement);
				}
				break;
			case 'a':
				this.model.attemptAttack(GameModel.LEFT);
				break;
			case 'd':
				this.model.attemptAttack(GameModel.RIGHT);
				break;
			case 's':
				this.model.attemptAttack(GameModel.DOWN);
				break;
			case 'w':
				this.model.attemptAttack(GameModel.UP);
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
