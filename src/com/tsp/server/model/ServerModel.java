package com.tsp.server.model;

import com.tsp.game.actors.AI;
import com.tsp.game.actors.Actor;
import com.tsp.game.actors.Player;
import com.tsp.game.map.Dungeon;
import com.tsp.game.map.Point3D;
import com.tsp.packets.*;
import com.tsp.server.controller.TCP.TCPServer;
import com.tsp.server.controller.UDP.UDPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA. User: Tim Date: 10/17/13 Time: 10:45 AM To change
 * this template use File | Settings | File Templates.
 */
public class ServerModel implements Runnable
{
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ServerModel.class);
	// Dungeon properties
	private final int ROWS = 24;
	private final int COLS = 80;
	private final int FLOORS = 4;
	HashMap<Integer, AI> ais;
	Dungeon dungeon;
	Queue<Packet> incomingPackets;
	HashMap<Integer, Actor> otherActors;
	Queue<Packet> outgoingPackets;
	ConcurrentHashMap<Integer, Player> players;
	boolean running = true;

	public ServerModel()
	{
		LOGGER.info("New Server Model");
		players = new ConcurrentHashMap<Integer, Player>();
		ais = new HashMap<Integer, AI>();
		otherActors = new HashMap<Integer, Actor>();
		incomingPackets = new LinkedList<Packet>();
		outgoingPackets = new LinkedList<Packet>();
		generateDungeon();
		dungeon.revealAll();
	}

	public void generateDungeon()
	{
		LOGGER.info("{}: {}: Model: Generating new dungeon", Thread
				.currentThread().getName(), Thread.currentThread().getId());
		dungeon = new Dungeon();
		LOGGER.info("Dungeon[{}][{}][{}]", dungeon.getDungeon().length,
		            dungeon.getDungeon()[0].length,
		            dungeon.getDungeon()[0][0].length);

	}

	public int addPlayer(String playName)
	{
		System.out.println("Adding Player: " + playName);
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

	public ArrayList<Actor> getActors()
	{
		LOGGER.info("Getting Actors");
		ArrayList<Actor> actors = new ArrayList<Actor>(otherActors.values());
		actors.addAll(players.values());
		actors.addAll(ais.values());
		return actors;
	}

	public String[][][] getDungeonArray()
	{
		LOGGER.info("{}: {}: Model: Getting Dungeon Map Array", Thread
				.currentThread().getName(), Thread.currentThread().getId());
		return dungeon.getDungeon();
	}

	public Dungeon getDungeon()
	{
		LOGGER.info("{}: {}: Model: Getting Dungeon Object", Thread
				.currentThread().getName(), Thread.currentThread().getId());
		return dungeon;
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
		LOGGER.info("get player id: {}", playerID);
		return players.get(playerID);
	}

	@Override
	public void run()
	{
		while (running)
		{
			// processPackets();
			proccessAttacks();
			processAI();
			sendPackets();
			try
			{
				Thread.sleep(5);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void proccessAttacks()
	{
		for (Player player : players.values())
		{
			if (player.isAttacking())
			{
				processAttack(player);
			}
		}
	}

	private synchronized void processAttack(Player player)
	{
		attemptAttack(player.getId(),
		              new Point3D((int) player.getDelta().getX(), (int) player.getDelta().getY()));


		//Player attacker = getPlayer(attackPacket.getAttacker());
		if (player.isAttacking())
		{
			ArrayList<Actor> actors = getActors();
			ActorUpdate attackUpdate = new ActorUpdate(player.getId());

			attackUpdate.insertValue("attacking", true);
			attackUpdate.insertValue("deltaX", (int) player.getDelta()
					.getX());
			attackUpdate.insertValue("deltaY", (int) player.getDelta()
					.getY());
			outgoingPackets.add(attackUpdate);

			Point3D attackDest = player.getAttackPos();
			for (Actor a : actors)
			{
				if (a.getId() != player.getId())
				{
					if (a.checkHit(attackDest))
					{
						player.hit(a);
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
		else if (!player.isAttacking())
		{
			ActorUpdate attackUpdate = new ActorUpdate(player.getId());
			attackUpdate.insertValue("attacking", false);
			attackUpdate.insertValue("deltaX", (int) 0);
			attackUpdate.insertValue("deltaY", (int) 0);
			outgoingPackets.add(attackUpdate);
		}
	}


	private synchronized void sendPackets()
	{
		// LOGGER.info("{}: {}: Model: Processing Outgoing Packets",
		// Thread.currentThread().getName(),Thread.currentThread().getId());
		while (!outgoingPackets.isEmpty())
		{
			TCPServer.addOutGoingPacket(outgoingPackets.poll());
		}
	}

	private synchronized void processAI()
	{
		// LOGGER.info("{}: {}: Model: AI turns",
		// Thread.currentThread().getName(),Thread.currentThread().getId());
		for (AI ai : ais.values())
		{
			ai.turn(dungeon, getActors());
		}
	}

	public void processPacket(Packet packet)
	{
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

	private synchronized void processAttack(AttackPacket attackPacket)
	{
		LOGGER.info("Processing attack from playerID: {}",
		            attackPacket.getAttacker());
		Player attacker = getPlayer(attackPacket.getAttacker());
		attemptAttack(attackPacket.getAttacker(),
		              new Point3D(attackPacket.getDeltaX(), attackPacket.getDeltaY()));
		{
			if (attacker.isAttacking())
			{
				ArrayList<Actor> actors = getActors();
				ActorUpdate attackUpdate = new ActorUpdate(attacker.getId());

				attackUpdate.insertValue("attacking", true);
				attackUpdate.insertValue("deltaX", (int) attacker.getDelta()
						.getX());
				attackUpdate.insertValue("deltaY", (int) attacker.getDelta()
						.getY());
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
			else if (!attacker.isAttacking())
			{
				ActorUpdate attackUpdate = new ActorUpdate(attackPacket.getAttacker());
				attackUpdate.insertValue("attacking", false);
				attackUpdate.insertValue("deltaX", (int) 0);
				attackUpdate.insertValue("deltaY", (int) 0);
				outgoingPackets.add(attackUpdate);
			}
		}
	}

	private synchronized void processUpdate(ActorUpdate actorUpdate)
	{
		LOGGER.info("Processing update for playerID: {}",
		            actorUpdate.getActorID());
		if (players.containsKey(actorUpdate.getActorID()))
		{
			if (actorUpdate.contains("remove"))
			{
				removePlayer(actorUpdate.getActorID());
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
					actor.setHealth(((Long) actorUpdate.getValue("health"))
							                .intValue());

				if (actorUpdate.contains("symbol"))
					actor.setSymbol((String) actorUpdate.getValue("symbol"));

				if (actorUpdate.contains("attacking")
				    && actorUpdate.contains("deltaX")
				    && actorUpdate.contains("deltaY"))
					((Player) actor).setAttacking((Boolean) actorUpdate
							.getValue("attacking"), new Point3D(
							((Long) actorUpdate.getValue("deltaX")).intValue(),
							((Long) actorUpdate.getValue("deltaY")).intValue(),
							0));
			}
			this.outgoingPackets.add(actorUpdate);
		}
	}

	private synchronized void processActor(ActorPacket actorPacket)
	{
		LOGGER.info("Processing Actor");
	}

	private synchronized void processMovement(MovementPacket movementPacket)
	{
		LOGGER.info("Processing movement for playerID: {}",
		            movementPacket.getM_playerID());

		if (players.containsKey(movementPacket.getM_playerID()))
		{
			if (attemptMove(movementPacket))
			{
				Player player = players.get(movementPacket.getM_playerID());
				ActorUpdate attackUpdate = new ActorUpdate(movementPacket.getM_playerID());
				attackUpdate.insertValue("attacking", false);
				attackUpdate.insertValue("deltaX", (int) 0);
				attackUpdate.insertValue("deltaY", (int) 0);
				outgoingPackets.add(attackUpdate);

				ActorUpdate actorUpdate = new ActorUpdate(
						movementPacket.getM_playerID());
				actorUpdate.insertValue("X", player.getX());
				actorUpdate.insertValue("Y", player.getY());
				actorUpdate.insertValue("Z", player.getZ());
				outgoingPackets.add(actorUpdate);
			}
		}

	}

	public void putIncoming(Packet packet)
	{
		synchronized (this)
		{
			incomingPackets.add(packet);
		}
	}

	public synchronized void removePlayer(Integer playerID)
	{
		System.out.println("Removing " + players.get(playerID));
		players.remove(playerID);
		ActorUpdate actorUpdate = new ActorUpdate(playerID);
		actorUpdate.insertValue("remove", "remove");

		outgoingPackets.add(actorUpdate);
	}

	public void quit() throws IOException
	{
		TCPServer.quit();
		UDPServer.quit();
	}

	public boolean attemptMove(MovementPacket movementPacket)
	{
		Player player = players.get(movementPacket.getM_playerID());
		Point3D newPosition = player.getPos().clone();
		newPosition.translate((int) movementPacket.getM_newX(), (int) movementPacket.getM_newY());

		if (player.isAttacking() && !player.attemptAttackReset())
			return false;

		// Verify the new Point3D is inside the map
		if (getDungeon().validPoint(newPosition))
		{
			int x = (int) newPosition.getX();
			int y = (int) newPosition.getY();
			int z = newPosition.getZ();
			if (!occupied(newPosition))
			{
				if (dungeon.isEmptyFloor(x, y, z))
				{
					player.setPos(newPosition);
					// dungeon.updateVisibleDungeon(me);
					return true;
				}
				else if (dungeon.isStairUp(x, y, z))
				{
					player.setPos(newPosition);
					player.move(new Point3D(0, 0, 1));
					// dungeon.updateVisibleDungeon(me);
					return true;
				}
				else if (dungeon.isStairDown(x, y, z))
				{
					player.setPos(newPosition);
					player.move(new Point3D(0, 0, -1));
					// dungeon.updateVisibleDungeon(me);
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		return false;
	}

	public boolean attemptAttack(int playerID, Point3D delta)
	{
		Player player = players.get(playerID);
		Point3D newPosition = player.getPos().clone();
		newPosition.translate(delta.x, delta.y);

		int x = newPosition.x;
		int y = newPosition.y;
		int z = newPosition.getZ();

		if (player.isAttacking() && player.attemptAttackReset())
			return false;

		if (!player.isAttacking() && (getDungeon().validPoint(newPosition)
		                              && (dungeon.isEmptyFloor(new Point3D(x, y, z)) || occupied(newPosition))))
		{
			player.setAttacking(true, delta);
			return true;
		}

		return false;
	}

	private boolean occupied(Point3D point)
	{
		for (Actor actor : getActors())
		{
			if (actor.getPos().equals(point))
				return true;
		}
		return false;
	}
}
