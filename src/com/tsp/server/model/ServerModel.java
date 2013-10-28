package com.tsp.server.model;

import com.tsp.game.actors.AI;
import com.tsp.game.actors.Actor;
import com.tsp.game.map.Dungeon;
import com.tsp.game.map.Point3D;
import com.tsp.game.actors.Player;
import com.tsp.packets.*;
import com.tsp.server.controller.TCP.TCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

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
		incomingPackets = new LinkedList<Packet>();
		outgoingPackets = new LinkedList<Packet>();
		generateDungeon();

	}

	public int addPlayer(String playName)
	{
		LOGGER.info("{}: {}: Model: Adding player: {}",
		            Thread.currentThread().getName(),
		            Thread.currentThread().getId(),
		            playName);
		Player player = new Player(playName, COLS, ROWS, FLOORS);
		Point3D point3D = player.getPos();
		boolean spotFound = false;
		for (int time = 0; time < 30; time++)
		{
			if (dungeon.walkableTile(player.getPos()))
			{
				spotFound = true;
				break;
			}
			player.newPosition(COLS, ROWS, FLOORS);
		}

		if (!spotFound)
		{
			try
			{
				player.setPos(dungeon.findFirstWalkablePoint(this.getActors()));
			}
			catch (Exception e)
			{
				return -1;
			}
		}
		players.put(player.getId(), player);
		return player.getId();
	}


	public String[][][] getDungeonArray()
	{
		LOGGER.info("{}: {}: Model: Getting Dungeon Map Array",
		            Thread.currentThread().getName(),
		            Thread.currentThread().getId());
		return dungeon.getDungeon();
	}

	public Dungeon getDungeon()
	{
		LOGGER.info("{}: {}: Model: Getting Dungeon Object",
		            Thread.currentThread().getName(),
		            Thread.currentThread().getId());
		return dungeon;
	}

	public void generateDungeon()
	{
		LOGGER.info("{}: {}: Model: Generating new dungeon",
		            Thread.currentThread().getName(),
		            Thread.currentThread().getId());
		dungeon = new Dungeon();
		LOGGER.info("Dungeon[{}][{}][{}]",
		            dungeon.getDungeon().length,
		            dungeon.getDungeon()[0].length,
		            dungeon.getDungeon()[0][0].length);

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
		LOGGER.info("get player id: {}",
		            playerID);
		return players.get(playerID);
	}

	public ArrayList<Actor> getActors()
	{
		LOGGER.info("Getting Actors");
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
				e.printStackTrace();
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
		for (AI ai : ais.values())
		{
			ai.turn(dungeon, getActors());
		}
	}

	private void processPackets()
	{
		synchronized (this)
		{
			while (!incomingPackets.isEmpty())
			{
				Packet packet = incomingPackets.poll();
				switch (packet.getPacketType())
				{
					case MOVEMENTPACKET:
						MovementPacket movementPacket = (MovementPacket) packet;
						processMovement(movementPacket);
						System.out.println(packet.toString());
						break;
					case ACTOR_PACKET:
						ActorPacket actorPacket = (ActorPacket) packet;
						processActor(actorPacket);
						System.out.println(packet.toString());
						break;
					case UPDATE_PACKET:
						ActorUpdate actorUpdate = (ActorUpdate) packet;
						processUpdate(actorUpdate);
						System.out.println(packet.toString());
						break;
					case ATTACK_PACKET:
						AttackPacket attackPacket = (AttackPacket) packet;
						processAttack(attackPacket);
						System.out.println(packet.toString());
						break;
					default:
						break;
				}
			}
		}
	}

	private void processAttack(AttackPacket attackPacket)
	{
		LOGGER.info("Processing attack from playerID: {}", attackPacket.getAttacker());
		ArrayList<Actor> actors = getActors();
		Player attacker = getPlayer(attackPacket.getAttacker());
		attacker.setAttacking(true, new Point3D(attackPacket.getDeltaX(), attackPacket.getDeltaY(), 0));
		ActorUpdate attackUpdate = new ActorUpdate(attacker.getId());

		attackUpdate.insertValue("attacking", true);
		attackUpdate.insertValue("deltaX", (int) attacker.getDelta().getX());
		attackUpdate.insertValue("deltaY", (int) attacker.getDelta().getY());
		outgoingPackets.add(attackUpdate);

		Point3D attackDest = attacker.getAttackPos();
		for (Actor a : actors)
		{
			if (a.getId() != attackPacket.getAttacker())
			{
				if (a.checkHit(attackDest))
				{
					attacker.hit(a);
					ActorUpdate aUpdate = new ActorUpdate(a.getId());
					if (a.getHealth() <= 0)
						aUpdate.insertValue("remove", "remove");
					else
						aUpdate.insertValue("health", a.getHealth());
					outgoingPackets.add(aUpdate);
				}
			}
		}
	}

	private void processUpdate(ActorUpdate actorUpdate)
	{
		LOGGER.info("Processing update for playerID: {}", actorUpdate.getActorID());
		if (players.containsKey(actorUpdate.getActorID()))
		{
			if (actorUpdate.contains("remove"))
			{
				players.remove(actorUpdate.getActorID());
			}
			else
			{
				Actor actor = players.get(actorUpdate.getActorID());

				if (actorUpdate.contains("X"))
					actor.setX(((Long) actorUpdate.getValue("X")).intValue());

				if (actorUpdate.contains("Y"))
					actor.setY(((Long) actorUpdate.getValue("Y")).intValue());

				if (actorUpdate.contains("Z"))
					actor.setZ(((Long) actorUpdate.getValue("Z")).intValue());

				if (actorUpdate.contains("health"))
					actor.setHealth(((Long) actorUpdate.getValue("health")).intValue());

				if (actorUpdate.contains("symbol"))
					actor.setSymbol((String) actorUpdate.getValue("symbol"));

				if (actorUpdate.contains("attacking") && actorUpdate.contains("deltaX") &&
				    actorUpdate.contains("deltaY"))
					((Player) actor)
							.setAttacking((Boolean) actorUpdate.getValue("attacking"),
							              new Point3D(((Long) actorUpdate.getValue("deltaX")).intValue(),
							                          ((Long) actorUpdate.getValue("deltaY")).intValue(),
							                          0));
			}
			this.outgoingPackets.add(actorUpdate);
		}
	}

	private void processActor(ActorPacket actorPacket)
	{
		LOGGER.info("Processing Actor");
	}

	private void processMovement(MovementPacket movementPacket)
	{
		LOGGER.info("Processing movement for playerID: {}", movementPacket.getM_playerID());

		if (players.containsKey(movementPacket.getM_playerID()))
		{
			players.get(movementPacket.getM_playerID()).setPos(new Point3D(movementPacket.getM_newX(),
			                                                               movementPacket.getM_newY(),
			                                                               movementPacket.getM_newZ()));
			ActorUpdate actorUpdate = new ActorUpdate(movementPacket.getM_playerID());
			actorUpdate.insertValue("X", movementPacket.getM_newX());
			actorUpdate.insertValue("Y", movementPacket.getM_newY());
			actorUpdate.insertValue("Z", movementPacket.getM_newZ());
			outgoingPackets.add(actorUpdate);
		}

	}

	public void putIncoming(Packet packet)
	{
		synchronized (this)
		{
			incomingPackets.add(packet);
		}
	}

	public void removePlayer(Integer playerID)
	{
		System.out.println("Removing " + players.get(playerID));
		players.remove(playerID);
		ActorUpdate actorUpdate = new ActorUpdate(playerID);
		actorUpdate.insertValue("remove", "remove");

		outgoingPackets.add(actorUpdate);
	}
}
