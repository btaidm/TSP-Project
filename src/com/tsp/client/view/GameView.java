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

		//Use the model to draw on the screen
		for (int i = 0; i < this.model.dungeonRows(); i++)
		{
			for (int j = 0; j < this.model.dungeonCols(); j++)
			{
				this.term.set(i, j, this.model.get(i, j), 255, 0);
			}
		}
		this.curses.refresh();
	}

	public void process(int ch)
	{
		switch (ch)
		{
			case BlackenKeys.KEY_DOWN:
				if (this.model.attemptMove(new Point(0, 1)))
				{
					HashMap<String, Object> movement = new HashMap<String, Object>();
					movement.put("ID", new Integer(0));
					movement.put("X", new Integer(0));
					movement.put("Y", new Integer(1));
					fireEvent(EventType.TURN_MOVE, movement);
				}
				break;
			case BlackenKeys.KEY_UP:
				if (this.model.attemptMove(new Point(0, -1)))
				{
					HashMap<String, Object> movement = new HashMap<String, Object>();
					movement.put("ID", new Integer(0));
					movement.put("X", new Integer(0));
					movement.put("Y", new Integer(-1));
					fireEvent(EventType.TURN_MOVE, movement);
				}
				break;
			case BlackenKeys.KEY_LEFT:
				if (this.model.attemptMove(new Point(-1, 0)))
				{
					HashMap<String, Object> movement = new HashMap<String, Object>();
					movement.put("ID", new Integer(0));
					movement.put("X", new Integer(-1));
					movement.put("Y", new Integer(0));
					fireEvent(EventType.TURN_MOVE, movement);
				}
				break;
			case BlackenKeys.KEY_RIGHT:
				if (this.model.attemptMove(new Point(1, 0)))
				{
					HashMap<String, Object> movement = new HashMap<String, Object>();
					movement.put("ID", new Integer(0));
					movement.put("X", new Integer(1));
					movement.put("Y", new Integer(0));
					fireEvent(EventType.TURN_MOVE, movement);
				}
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
