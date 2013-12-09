package com.tsp.client.view;

import com.googlecode.blacken.colors.ColorNames;
import com.googlecode.blacken.colors.ColorPalette;
import com.googlecode.blacken.swing.SwingTerminal;
import com.googlecode.blacken.terminal.BlackenKeys;
import com.googlecode.blacken.terminal.CursesLikeAPI;
import com.googlecode.blacken.terminal.TerminalScreenSize;
import com.tsp.client.controller.TCPClient;
import com.tsp.client.event.EventType;
import com.tsp.client.event.GameEvent;
import com.tsp.client.event.GameListener;
import com.tsp.client.event.Listenable;
import com.tsp.client.model.GameModel;
import com.tsp.game.actors.Actor;
import com.tsp.game.actors.Player;
import com.tsp.game.map.Point3D;
import com.tsp.packets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GameView implements Listenable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GameView.class);

	private final int OFFSET_TOP = 2;
	private final int OFFSET_BOTTOM = 2;
	private final int OFFSET_LEFT = 0;
	private final int DUNGEON_HEIGHT = 24;
	private final int SCREEN_HEIGHT = OFFSET_TOP + OFFSET_BOTTOM + DUNGEON_HEIGHT;
	private final int SCREEN_WIDTH = 80;
	private final int HARD_MESSAGE_LENGTH_LIMIT = 21;
	
	SwingTerminal term;
	CursesLikeAPI curses;
	GameModel model;
	boolean quit = false;
	ArrayList<GameListener> listeners;
	
	private TCPClient tcpClient;
	private boolean esc = false;
	private boolean restart = true;
	private boolean playing = true;
	private boolean initStuff = true;
	private boolean kdfeed = true;
	private long actorMovingTimeStart;

	public GameView(GameModel model, TCPClient tcpClient) throws InterruptedException
	{

		this.model = model;
		this.tcpClient = tcpClient;
		this.listeners = new ArrayList<GameListener>();
		this.term = new SwingTerminal();
	}

	public boolean setUp() throws InterruptedException
	{

		(tcpClient = new TCPClient(this.model, tcpClient.getAddr(), tcpClient.getPort())).start();
		fireEvent(EventType.TURN_INIT, new HashMap<String, Object>());
		model.setQuit(false);
		model.setReady(false);
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

	private void init() throws InterruptedException
	{
		restart = false;
		if (setUp())
		{
			if (initStuff)
			{
				this.term.init("TSP Rouglike", SCREEN_HEIGHT, SCREEN_WIDTH, TerminalScreenSize.SIZE_MEDIUM);
				term.resize(SCREEN_HEIGHT, SCREEN_WIDTH);

				this.curses = new CursesLikeAPI(this.term);
				this.curses.resize(SCREEN_HEIGHT - 1, SCREEN_WIDTH);

				ColorPalette palette = new ColorPalette();
				palette.addAll(ColorNames.XTERM_256_COLORS, false);
				palette.putMapping(ColorNames.SVG_COLORS);

				this.curses.setPalette(palette);
				initStuff = false;
			}
			model.setQuit(false);
			quit = false;
			playing = true;
		}
		else
		{
			esc = true;
		}
	}

	private void run() throws IOException, InterruptedException
	{
		// Main loop of the game happens here
		int ch = BlackenKeys.NO_KEY;
		while (!quit && !model.getQuit())
		{
			processPackets();
			long start = System.currentTimeMillis();
			ch = this.curses.getch(10);
			process(ch);
			refresh();

			fireEvent(EventType.TURN_END, new HashMap<String, Object>());
		}
		quit = false;
		playing = false;
		while (!esc && !quit)
		{
			ch = this.curses.getch(50);
			process(ch);
			fadeToGameOver();

			fireEvent(EventType.TURN_END, new HashMap<String, Object>());
		}

		this.close();
	}
	
	public void play() throws IOException, InterruptedException
	{
		while (restart)
		{
			init();
			run();
		}
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
			case MESSAGE_PACKET:
				MessagePacket messageUpdate = (MessagePacket) packet;
				model.addMessage(messageUpdate);
				break;
			case SCORE_PACKET:
				ScorePacket scoreUpdate = (ScorePacket) packet;
				model.updateScore(scoreUpdate);
				break;
			default:
				break;
			}
		}
	}

	private void close() throws IOException, InterruptedException
	{
		if (curses != null && !restart)
			curses.quit();

		if (term != null && !restart)
			term.quit();
		tcpClient.quit();
		tcpClient.join();
	}

	public void refresh()
	{
		this.curses.clear();
		this.curses.setCursorLocation(-1, -1);

		int zLevel = this.model.getMe().getZ();


		// Draw HUD
		Player p = this.model.getMe();
		drawString(p.getName(), OFFSET_LEFT, 0, p.getColor(), 0);

		String floorString = String.format("Floor %d", zLevel);
		drawString(floorString, SCREEN_WIDTH + OFFSET_LEFT - floorString.length(), 0, p.getColor(), 0);

		StringBuilder healthBuilder = new StringBuilder();
		for (int i = 0; i < p.getHealth(); i++)
		{
			healthBuilder.append("\u2764");
		}
		drawString(healthBuilder.toString(),
				OFFSET_LEFT,
				OFFSET_BOTTOM + DUNGEON_HEIGHT + OFFSET_TOP - 2,
				p.getColor(),
				0);

		String weapongString = "Currently Wielding " + p.getWeaponName();
		drawString(weapongString,
				SCREEN_WIDTH + OFFSET_LEFT - weapongString.length(),
				OFFSET_BOTTOM + DUNGEON_HEIGHT + OFFSET_TOP - 2,
				p.getColor(),
				0);

		// Draw the message screen
		int level = 0;
		java.util.List<String> queue = (this.kdfeed ? this.model.getMessages() : this.model.getScores());
		for (String m : queue) {
			if (m.length() > HARD_MESSAGE_LENGTH_LIMIT) {
				m = m.substring(0, HARD_MESSAGE_LENGTH_LIMIT);
			}
			drawString(m, OFFSET_LEFT + SCREEN_WIDTH+1, level, 255, 0);
			level++;
		}

		// Draw Map
		//Use the model to draw on the screen
		for (int i = 0; i < this.model.getDungeon().getRows(); i++)
		{
			for (int j = 0; j < this.model.getDungeon().getColumns(); j++)
			{
				// Add offsets to center the dungeon onscreen and allow room for the HUD
				this.term.set(i + OFFSET_TOP,
						j + OFFSET_LEFT,
						this.model.getSymbol(i, j, zLevel),
						model.getColor(j, i, zLevel),
						0);
			}
		}
		this.curses.refresh();
	}

	public void refresh(int fade)
	{
		this.curses.clear();
		this.curses.setCursorLocation(-1, -1);

		int zLevel = this.model.getMe().getZ();

		// Draw HUD
		Player p = this.model.getMe();
		drawString(p.getName(), OFFSET_LEFT, 0, 255 - fade, 0);

		String floorString = String.format("Floor %d", zLevel);
		drawString(floorString, SCREEN_WIDTH + OFFSET_LEFT - floorString.length(), 0, 255 - fade, 0);

		StringBuilder healthBuilder = new StringBuilder();
		for (int i = 0; i < p.getHealth(); i++)
		{
			healthBuilder.append("\u2764");
		}

		for (int i = (p.getHealth() < 0 ? 0 : p.getHealth()); i < 10; i++)
		{
			healthBuilder.append(" ");
		}

		drawString(healthBuilder.toString(),
				OFFSET_LEFT,
				OFFSET_BOTTOM + DUNGEON_HEIGHT + OFFSET_TOP - 2,
				255 - fade,
				0);

		String weapongString = "Currently Wielding " + p.getWeaponName();
		drawString(weapongString,
				SCREEN_WIDTH + OFFSET_LEFT - weapongString.length(),
				OFFSET_BOTTOM + DUNGEON_HEIGHT + OFFSET_TOP - 2,
				255 - fade,
				0);

		// Draw Map
		//Use the model to draw on the screen
		for (int i = 0; i < this.model.getDungeon().getRows(); i++)
		{
			for (int j = 0; j < this.model.getDungeon().getColumns(); j++)
			{
				// Add offsets to center the dungeon onscreen and allow room for the HUD
				this.term.set(i + OFFSET_TOP,
						j + OFFSET_LEFT,
						this.model.getSymbol(i, j, zLevel),
						255 - fade,
						0);
			}
		}
	}

	public void process(int ch)
	{
		boolean attacked = false;
		Point attackDelta = null;
		boolean moved = false;
		Point3D moveDelta = null;
		switch (ch)
		{
			default:
			case BlackenKeys.NO_KEY:
			{

				break;
			}
			case BlackenKeys.KEY_ESCAPE:
				quit = true;
				esc = true;
				break;
			case 'r':
				if (!playing)
				{
					quit = true;
					restart = true;
					fadeCount = 0;
				}
				break;
			case BlackenKeys.KEY_DOWN:
			case 'j':
				if (!model.getQuit())
				{
					moved = true;
					moveDelta = Point3D.DOWN;
				}
				break;
			case BlackenKeys.KEY_UP:
			case 'k':
				if (!model.getQuit())
				{
					moved = true;
					moveDelta = Point3D.UP;
				}
				break;
			case BlackenKeys.KEY_LEFT:
			case 'h':
				if (!model.getQuit())
				{
					moved = true;
					moveDelta = Point3D.LEFT;
				}
				break;
			case BlackenKeys.KEY_RIGHT:
			case 'l':
				if (!model.getQuit())
				{
					moved = true;
					moveDelta = Point3D.RIGHT;
				}
				break;
			case 'a':
				if (!model.getQuit())
					if (attacked = this.model.attemptAttack(Point3D.LEFT))
					{
						attackDelta = Point3D.LEFT;
					}
				break;
			case 'd':
				if (!model.getQuit())
					if (attacked = this.model.attemptAttack(Point3D.RIGHT))
					{
						attackDelta = Point3D.RIGHT;
					}
				break;
			case 's':
				if (!model.getQuit())
					if (attacked = this.model.attemptAttack(Point3D.DOWN))
					{
						attackDelta = Point3D.DOWN;
					}
				break;
			case 'w':
				if (!model.getQuit())
					if (attacked = this.model.attemptAttack(Point3D.UP))
					{
						attackDelta = Point3D.UP;
					}
				break;
			case BlackenKeys.KEY_TAB:
				this.kdfeed = !this.kdfeed;
		}
		if (moved && model.attemptMove(moveDelta))
		{
			HashMap<String, Object> movement = new HashMap<String, Object>();
			movement.put("ID", model.getMe().getId());
			movement.put("X", moveDelta.getX());
			movement.put("Y", moveDelta.getY());
			movement.put("Z", moveDelta.getZ());
			fireEvent(EventType.TURN_MOVE, movement);
			model.setMoving(true);
			actorMovingTimeStart = System.currentTimeMillis();
		}
		else
		{
			if(model.Moving() && System.currentTimeMillis() - actorMovingTimeStart > 250)
				model.setMoving(false);
		}
		if (attacked)
		{
			Actor player = model.getMe();
			HashMap<String, Object> attack = new HashMap<String, Object>();
			attack.put("ID", player.getId());
			attack.put("X", (int) attackDelta.getX());
			attack.put("Y", (int) attackDelta.getY());
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

	// Convinience method for drawing strings in the terminal
	public void drawString(String s, int x, int y, int fg, int bg)
	{
		for (int i = 0; i < s.length(); i++)
		{
			String current = String.valueOf(s.charAt(i));
			this.curses.set(y, x + i, current, fg, bg);
		}
	}

	private int fadeCount = 0;

	public void fadeToGameOver()
	{

		int fade = fadeCount / 2;
		refresh(fade);
		drawString("GAME OVER",
				(curses.getWidth() / 2) - "GAME OVER".length() / 2,
				((curses.getHeight()) / 2 - 1),
				232 + fade,
				0);
		drawString("YOU ARE DEAD",
				((curses.getWidth() / 2)) - "YOU ARE DEAD".length() / 2,
				((curses.getHeight()) / 2 + 1),
				232 + fade,
				0);
		drawString("ESC - QUIT    R - RESTART",
				((curses.getWidth() / 2)) - "ESC - QUIT    R - RESTART".length() / 2,
				((curses.getHeight()) / 2 + 3),
				232 + fade,
				0);
		curses.refresh();
		if (fade < 23)
			fadeCount++;
	}
}
