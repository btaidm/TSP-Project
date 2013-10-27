package com.tsp.client.model;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import com.tsp.game.actors.Actor;
import com.tsp.game.actors.Player;
import com.tsp.game.map.Dungeon;
import com.tsp.game.map.Point3D;
import com.tsp.packets.ActorUpdate;
import com.tsp.packets.Packet;

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

	private Player me;
	private Dungeon dungeon;
	
	public GameModel()
	{
		otherActors = new HashMap<Integer, Actor>();
	}

	/* Convinience methods for getting dungeon and actor */
	public Dungeon getDungeon() {
		return this.dungeon;
	}
	
	public Player getMe()
	{
		if (me == null) {
			me = new Player("RandomPlayer", getDungeon().getRows(), getDungeon().getColumns(), getDungeon().getFloors());
		}
		return me;
	}
	
	/* Methods used to get symbols and colors for drawing in the map */
	public String getSymbol(int y, int x, int z)
	{
		if (me.getPos().equals(new Point3D(x, y, z)))
		{
			return me.getSymbol();
		}
		else if (attackLocation != null && x == attackLocation.getX() && y == attackLocation.getY() &&
		         z == attackLocation.getZ())
		{
			return attackAnimation;
		}

		for (Actor a : otherActors.values())
		{
			if (a.getPos().equals(new Point3D(x, y, z)))
			{
				return a.getSymbol();
			}
		}

		return this.dungeon.getTile(new Point3D(x, y, z));
	}

	public int getColor(int x, int y, int z)
	{
		if (me.getPos().equals(new Point3D(x, y, z)) ||
		    (attackLocation != null && x == attackLocation.getX() && y == attackLocation.getY() &&
		     z == attackLocation.getZ()))
		{
			return me.getColor();
		}


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
		this.getMe().setId(id);
	}

	public void setDungeon(String[][][] dungeon)
	{
		this.dungeon = new Dungeon(dungeon);
	}

	public void setMe(Player me)
	{
		this.me = me;
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
				if (actorUpdate.contains("X"))
					otherActors.get(actorUpdate.getActorID()).setX(((Long) actorUpdate.getValue("X")).intValue());
				if (actorUpdate.contains("Y"))
					otherActors.get(actorUpdate.getActorID()).setY(((Long) actorUpdate.getValue("Y")).intValue());
				if (actorUpdate.contains("Z"))
					otherActors.get(actorUpdate.getActorID()).setZ(((Long) actorUpdate.getValue("Z")).intValue());
				if (actorUpdate.contains("health"))
					otherActors.get(actorUpdate.getActorID())
							.setHealth(((Long) actorUpdate.getValue("health")).intValue());
				if (actorUpdate.contains("symbol"))
					otherActors.get(actorUpdate.getActorID()).setSymbol((String) actorUpdate.getValue("symbol"));
			}
		}
	}

	public void addActor(Actor actor)
	{
		if (actor.getId() != me.getId() && !otherActors.containsKey(actor.getId()))
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
	
	/* Attempting attack and move methods */
	public boolean attemptMove(Point delta)
    {
            Point3D newPosition = me.getPos().clone();
            newPosition.translate((int) delta.getX(), (int) delta.getY());

            if (getMe().isAttacking())
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
                                    me.setPos(newPosition);
                                    return true;
                            }
                            else if (dungeon.isStairUp(x, y, z))
                            {
                                    me.setPos(newPosition);
                                    me.move(new Point3D(0, 0, 1));
                                    return true;
                            }
                            else if (dungeon.isStairDown(x, y, z))
                            {
                                    me.setPos(newPosition);
                                    me.move(new Point3D(0, 0, -1));
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
	
	public boolean attemptAttack(Point delta)
    {
            Point3D newPosition = (Point3D) me.getPos().clone();
            newPosition.translate(delta.x, delta.y);

            int x = newPosition.x;
            int y = newPosition.y;
            int z = newPosition.getZ();

            if(getMe().isAttacking());

            if (getDungeon().validPoint(newPosition) && dungeon.isEmptyFloor(new Point3D(x, y, z)))
            {
                    me.startAttacking(delta);
            }

            return false;
    }
}
