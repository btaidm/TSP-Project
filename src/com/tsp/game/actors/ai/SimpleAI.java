package com.tsp.game.actors.ai;

import com.tsp.game.actors.Actor;
import com.tsp.game.map.Dungeon;
import com.tsp.game.map.Point3D;
import com.tsp.packets.ActorUpdate;
import com.tsp.packets.Packet;

import java.util.ArrayList;

/**
 * @author Tim
 */
public class SimpleAI extends AI
{

	public SimpleAI()
	{
		super();
		this.symbol = "A";
		this.pos = new Point3D(0, 0, 0);
		this.name = "AI";
		this.color = 231;
		this.health = 10;
	}

	@Override
	public Packet turn(Dungeon dungeon, ArrayList<Actor> actors)
	{
		if (attack(dungeon, actors))
		{

		}
		else if (move(dungeon, actors))
		{
			ActorUpdate actorUpdate = new ActorUpdate(this.id);
			actorUpdate.insertValue("X", this.pos.getX());
			actorUpdate.insertValue("Y", this.pos.getY());
			actorUpdate.insertValue("Z", this.pos.getZ());
		}

		return null;
	}

	private boolean move(Dungeon dungeon, ArrayList<Actor> actors)
	{
		double random = Math.random();
		Point3D delta;

		if (random < .25)
			delta = Point3D.DOWN;
		else if (.25 <= random && random < .5)
			delta = Point3D.LEFT;
		else if (.5 <= random && random < .75)
			delta = Point3D.UP;
		else
			delta = Point3D.DOWN;

		return attemptMove(dungeon, delta, actors);
	}

	private boolean attack(Dungeon dungeon, ArrayList<Actor> actors)
	{
		if (checkUp(dungeon, actors))
		{
			return attemptAttack(dungeon, Point3D.UP);
		}
		else if (checkRight(dungeon, actors))
		{
			return attemptAttack(dungeon, Point3D.RIGHT);
		}
		else if (checkDown(dungeon, actors))
		{
			return attemptAttack(dungeon, Point3D.DOWN);
		}
		else if (checkLeft(dungeon, actors))
		{
			return attemptAttack(dungeon, Point3D.LEFT);
		}
		return false;
	}

	private boolean checkUp(Dungeon dungeon, ArrayList<Actor> actors)
	{
		Point3D attackPos = pos.clone();
		attackPos.add(Point3D.UP);
		return occupied(attackPos, actors);
	}

	private boolean checkDown(Dungeon dungeon, ArrayList<Actor> actors)
	{
		Point3D attackPos = pos.clone();
		attackPos.add(Point3D.DOWN);
		return occupied(attackPos, actors);
	}

	private boolean checkLeft(Dungeon dungeon, ArrayList<Actor> actors)
	{
		Point3D attackPos = pos.clone();
		attackPos.add(Point3D.LEFT);
		return occupied(attackPos, actors);
	}

	private boolean checkRight(Dungeon dungeon, ArrayList<Actor> actors)
	{
		Point3D attackPos = pos.clone();
		attackPos.add(Point3D.RIGHT);
		return occupied(attackPos, actors);
	}
}
