package com.tsp.viewer.model;

import com.tsp.game.actors.Actor;
import com.tsp.game.actors.Player;
import com.tsp.game.map.Dungeon;
import com.tsp.game.map.Point3D;
import com.tsp.packets.ActorUpdate;
import com.tsp.packets.Packet;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class GameModel
{

	Queue<Packet> packets = new LinkedList<Packet>();
	boolean quit = false;

	//Actor and dungeon
	Point3D attackLocation;
	String attackAnimation = "-";

	//Game tile types
	private HashMap<Integer, Actor> otherActors;
	private boolean ready = false;

	private Dungeon dungeon;
	private String playerName;
	private int id;

	public GameModel()
	{
		otherActors = new HashMap<Integer, Actor>();
	}

	public GameModel(String playerName)
	{
		this();
		this.playerName = playerName;
	}

	/* Convinience methods for getting dungeon and actor */
	public Dungeon getDungeon()
	{
		return this.dungeon;
	}


	/* Methods used to get symbols and colors for drawing in the map */
	public String getSymbol(int y, int x, int z)
	{
		Point3D point = new Point3D(x, y, z);

		for (Actor a : otherActors.values())
		{
			if (a.getPos().equals(point))
			{
				return a.getSymbol();
			}
			if (a instanceof Player)
			{
				if (((Player) a).isAttacking() && ((Player) a).getAttackPos().equals(point))
				{
					return ((Player) a).getAttackSymbol();
				}
			}
		}

		return this.dungeon.getTile(new Point3D(x, y, z));
	}

	public int getColor(int x, int y, int z)
	{
		for (Actor a : otherActors.values())
		{
			if (a.getPos().equals(new Point3D(x, y, z)))
			{
				return a.getColor();
			}
		}


		if (dungeon.isStairUp(x, y, z) || dungeon.isStairDown(x, y, z))
			return (int) (255.0 / 2);
		return 255;
	}

	/* Convinience setters and getters, also game state information*/
	public void setID(int id)
	{
		this.id = id;
	}

	public void setDungeon(String[][][] dungeon)
	{
		this.dungeon = new Dungeon(dungeon);
		this.dungeon.revealAll();
	}


	public void setQuit(boolean quit)
	{
		this.quit = quit;
	}

	public boolean getQuit()
	{
		return quit;
	}

	public boolean getReady()
	{
		return ready;
	}

	public void setReady(boolean ready)
	{
		this.ready = ready;
	}

	/* Client-Server Communication functions */
	public boolean packetsAviable()
	{
		boolean ret;
		synchronized (this)
		{
			ret = !packets.isEmpty();
		}
		return ret;
	}

	public Packet getPacket()
	{
		Packet ret;
		synchronized (this)
		{
			ret = packets.poll();
		}
		return ret;
	}

	public void insertPacket(Packet e)
	{
		if (e != null)
		{
			synchronized (this)
			{
				packets.add(e);
			}
		}
	}

	public boolean hasPackets()
	{
		return !packets.isEmpty();
	}

	public void update(ActorUpdate actorUpdate)
	{
		if (otherActors.containsKey(actorUpdate.getActorID()))
		{
			if (actorUpdate.contains("remove"))
			{
				otherActors.remove(actorUpdate.getActorID());
			}
			else
			{
				Actor actor = otherActors.get(actorUpdate.getActorID());

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

				if (actorUpdate.contains("attacking") &&
				    actorUpdate.contains("deltaX") &&
				    actorUpdate.contains("deltaY"))
					((Player) actor)
							.setAttacking((Boolean) actorUpdate.getValue("attacking"),
							              new Point3D(((Long) actorUpdate.getValue("deltaX")).intValue(),
							                          ((Long) actorUpdate.getValue("deltaY")).intValue(),
							                          0));
			}
		}
	}

	public void addActor(Actor actor)
	{
		if (!otherActors.containsKey(actor.getId()))
			otherActors.put(actor.getId(), actor);
	}

	private boolean occupied(Point3D point)
	{
		for (Actor actor : otherActors.values())
		{
			if (actor.getPos().equals(point))
				return true;
		}
		return false;
	}

}
