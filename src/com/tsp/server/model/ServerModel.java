package com.tsp.server.model;

import com.tsp.game.actors.AI;
import com.tsp.game.actors.Actor;
import com.tsp.game.map.MapGenerator;
import com.tsp.game.map.Point3D;
import com.tsp.game.actors.Player;
import com.tsp.packets.Packet;
import com.tsp.server.controller.TCP.TCPServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/17/13
 * Time: 10:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class ServerModel implements Runnable
{

	String[][][] dungeon;

	//Dungeon properties
	private final int ROWS = 24;
	private final int COLS = 80;
	private final int FLOORS = 4;

	ConcurrentHashMap<Integer, Player> players;
	HashMap<Integer, AI> ais;
	HashMap<Integer, Actor> otherActors;
	Queue<Packet> incomingPackets;
	Queue<Packet> outgoingPackets;

	public ServerModel()
	{
		players = new ConcurrentHashMap<Integer, Player>();
		otherActors = new HashMap<Integer, Actor>();
		incomingPackets = new LinkedBlockingQueue<Packet>();
		outgoingPackets = new LinkedList<Packet>();
		generateDungeon();

	}

	public int addPlayer(String playName)
	{
		Player player = new Player(playName, COLS, ROWS, FLOORS);
		Point3D point3D = player.getPos();
		while (!(dungeon[(int) point3D.getX()][(int) point3D.getY()][point3D.getZ()].equals(EMPTY_FLOOR)))
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

	public int getColumns()
	{
		return COLS;
	}

	public int getFloors()
	{
		return FLOORS;
	}

	public int getRows()
	{
		return ROWS;
	}

	public Player getPlayer(Integer playerID)
	{
		return players.get(playerID);
	}

	public int addPlayer(Player player)
	{
		players.put(player.getId(), player);
		return player.getId();
	}

	public ArrayList<Actor> getActors()
	{
		ArrayList<Actor> actors = new ArrayList<Actor>(otherActors.values());
		actors.addAll(players.values());
		actors.addAll(ais.values());
		return actors;
	}

	@Override
	public void run()
	{
		while (true)
		{
			processPackets();
			processAI();
			sendPackets();
		}
	}

	private void sendPackets()
	{
		while (!outgoingPackets.isEmpty())
		{
			TCPServer.addOutGoingPacket(outgoingPackets.poll());
		}
	}

	private void processAI()
	{
		for(AI ai : ais.values())
		{
			ai.turn(dungeon, );
		}
	}

	private void processPackets()
	{
		//To change body of created methods use File | Settings | File Templates.
	}
}
