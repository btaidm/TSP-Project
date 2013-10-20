package com.tsp.server.model;

import com.tsp.game.map.MapGenerator;
import com.tsp.game.map.Point3D;
import com.tsp.game.characters.Player;
import com.tsp.packets.Packet;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/17/13
 * Time: 10:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class ServerModel
{
	public static final String PLAYER = "@";
	public static final String EMPTY_FLOOR = " ";
	public static final String WALL = "#";
	public static final String STAIR_UP = "\u25B2";
	public static final String STAIR_DOWN = "\u25BC";

	String[][][] dungeon;

	//Dungeon properties
	private final int ROWS = 24;
	private final int COLS = 80;
	private final int FLOORS = 4;

	ConcurrentHashMap<Integer, Player> players;
	HashMap<Integer, Character> otherChars;
	Queue<Packet> packets;

	public ServerModel()
	{
		players = new ConcurrentHashMap<Integer, Player>();
		otherChars = new HashMap<Integer, Character>();
		generateDungeon();

	}

	public int addPlayer(String playName)
	{
		Player player = new Player(playName, COLS, ROWS, FLOORS);
		Point3D point3D = player.getPos();
		while(!(dungeon[(int)point3D.getX()][(int)point3D.getY()][point3D.getZ()].equals(EMPTY_FLOOR)))
		{
			player.newPosition(COLS, ROWS, FLOORS);
		}
		players.put(player.getId(), player);
		return player.getId();
	}

	public Point3D getPlayerPos(int id)
	{
		return players.get(id).getPos();
	}

	public String[][][] getDungeon()
	{
		return dungeon;
	}

	public void generateDungeon()
	{
		MapGenerator f = new MapGenerator(WALL, EMPTY_FLOOR, STAIR_UP, STAIR_DOWN);
		dungeon = f.getMap(FLOORS, ROWS, COLS);
	}
}
