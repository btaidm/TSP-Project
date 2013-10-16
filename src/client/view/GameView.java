package client.view;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Properties;

import client.event.EventType;
import client.event.GameEvent;
import client.event.GameListener;
import client.event.Listenable;
import client.model.GameModel;

import com.googlecode.blacken.colors.ColorNames;
import com.googlecode.blacken.colors.ColorPalette;
import com.googlecode.blacken.swing.SwingTerminal;
import com.googlecode.blacken.terminal.BlackenKeys;
import com.googlecode.blacken.terminal.CursesLikeAPI;

public class GameView implements Listenable {

	SwingTerminal term;
	CursesLikeAPI curses;
	GameModel model;

	ArrayList<GameListener> listeners;
	
	public GameView(GameModel model) {

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

	public void play() {
		// Main loop of the game happens here
		int ch = BlackenKeys.NO_KEY;
		while(true) {
			ch = this.curses.getch();
			process(ch);
			refresh();
			
			fireEvent(EventType.TURN_END, new Properties());
		}
	}

	public void refresh() {
		this.curses.clear();
		this.curses.setCursorLocation(-1, -1);

		//Use the model to draw on the screen
		for (int i = 0; i < this.model.dungeonRows(); i++) {
			for (int j = 0; j < this.model.dungeonCols(); j++) {
				this.term.set(i, j, this.model.get(i, j), 255, 0);
			}
		}
		this.curses.refresh();
	}

	public void process(int ch) {
		switch(ch) {
		case BlackenKeys.KEY_DOWN:
			this.model.attemptMove(new Point(0, 1));
			break;
		case BlackenKeys.KEY_UP:
			this.model.attemptMove(new Point(0, -1));
			break;
		case BlackenKeys.KEY_LEFT:
			this.model.attemptMove(new Point(-1, 0));
			break;
		case BlackenKeys.KEY_RIGHT:
			this.model.attemptMove(new Point(1, 0));
			break;
		}
	}

	@Override
	public void addListener(GameListener gl) {
		this.listeners.add(gl);
	}

	@Override
	public void removeListener(GameListener gl) {
		this.listeners.remove(gl);
	}

	@Override
	public void fireEvent(EventType e, Properties payload) {
		GameEvent ge = new GameEvent(e, payload);
		
		for (GameListener l : this.listeners) {
			l.receiveEvent(ge);
		}
	}
}
