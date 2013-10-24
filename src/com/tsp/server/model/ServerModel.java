package com.tsp.server.model;

import com.tsp.game.actors.AI;
import com.tsp.game.actors.Actor;
import com.tsp.game.map.Dungeon;
import com.tsp.game.map.Point3D;
import com.tsp.game.actors.Player;
import com.tsp.packets.Packet;
import com.tsp.server.controller.TCP.TCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerModel.class);

	Dungeon dungeon;

	//Dungeon properties
	private final int ROWS = 24;
	private final int COLS = 80;
	private final int FLOORS = 4;

	boolean running = true;

	ConcurrentHashMap<Integer, Player> players;
	HashMap<Integer, AI> ais;
	HashMap<Integer, Actor> otherActors;
	Queue<Packet> incomingPackets;
	Queue<Packet> outgoingPackets;

	public ServerModel()
	{
		LOGGER.info("New Server Model");
		players = new ConcurrentHashMap<Integer, Player>();
		ais = new HashMap<Integer, AI>();
		otherActors = new HashMap<Integer, Actor>();
		incomingPackets = new LinkedBlockingQueue<Packet>();
		outgoingPackets = new LinkedList<Packet>();
		generateDungeon();

	}

	public int addPlayer(String playName)
	{
		LOGGER.info("{}: {}: Model: Adding player: {}", Thread.currentThread().getName(),Thread.currentThread().getId(), playName);
		Player player = new Player(playName, COLS, ROWS, FLOORS);
		Point3D point3D = player.getPos();
		while (!dungeon.walkableTile(point3D))
		{
			player.newPosition(COLS, ROWS, FLOORS);
		}
		players.put(player.getId(), player);
		return player.getId();
	}


	public String[][][] getDungeonArray()
	{
		LOGGER.info("{}: {}: Model: Getting Dungeon Map Array", Thread.currentThread().getName(),Thread.currentThread().getId());
		return dungeon.getDungeon();
	}

	public Dungeon getDungeon()
	{
		LOGGER.info("{}: {}: Model: Getting Dungeon Object", Thread.currentThread().getName(),Thread.currentThread().getId());
		return dungeon;
	}

	public void generateDungeon()
	{
		LOGGER.info("{}: {}: Model: Generating new dungeon", Thread.currentThread().getName(),Thread.currentThread().getId());
		dungeon = new Dungeon();
		LOGGER.info("Dungeon[{}][{}][{}]",dungeon.getDungeon().length,dungeon.getDungeon()[0].length,dungeon.getDungeon()[0][0].length);

	}

	public int getColumns()
	{
		return dungeon.getColumns();
	}

	public int getFloors()
	{
		return dungeon.getFloors();
	}

	public int getRows()
	{
		return dungeon.getRows();
	}

	public Player getPlayer(Integer playerID)
	{
		LOGGER.info("{}: {}: Model: get player id: {}", Thread.currentThread().getName(),Thread.currentThread().getId(), playerID);
		return players.get(playerID);
	}

	public ArrayList<Actor> getActors()
	{
		LOGGER.info("{}: {}: Model: Getting Actors", Thread.currentThread().getName(),Thread.currentThread().getId());
		ArrayList<Actor> actors = new ArrayList<Actor>(otherActors.values());
		actors.addAll(players.values());
		actors.addAll(ais.values());
		return actors;
	}

	@Override
	public void run()
	{
		while (running)
		{
			processPackets();
			processAI();
			sendPackets();
			try
			{
				Thread.sleep(20);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		}
	}

	private void sendPackets()
	{
		//LOGGER.info("{}: {}: Model: Processing Outgoing Packets", Thread.currentThread().getName(),Thread.currentThread().getId());
		while (!outgoingPackets.isEmpty())
		{
			TCPServer.addOutGoingPacket(outgoingPackets.poll());
		}
	}

	private void processAI()
	{
		//LOGGER.info("{}: {}: Model: AI turns", Thread.currentThread().getName(),Thread.currentThread().getId());
		for(AI ai : ais.values())
		{
			ai.turn(dungeon, getActors());
		}
	}

	private void processPackets()
	{
		//LOGGER.info("{}: {}: Model: Processing Incoming Packets", Thread.currentThread().getName(),Thread.currentThread().getId());
	}
}
