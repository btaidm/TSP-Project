package com.tsp.game.actors.ai;

import com.tsp.game.actors.Actor;
import com.tsp.game.map.Dungeon;
import com.tsp.game.map.Point3D;
import com.tsp.packets.Packet;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/21/13
 * Time: 4:09 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AI extends Actor
{

	final int ATTACK_COUNTER_MAX = 3;
	String attackSymbol = "-";
	boolean attacking = false;
	Point3D attackDelta = new Point3D(0, 0, 0);
	int attackCounter = 0;

	public AI()
	{
		super();
		type = ActorType.ACTOR_AI;
	}

	/**
	 * Calculates the AIs turn
	 *
	 * @param dungeon the dungeon
	 * @param actors  the actors
	 * @return a packet
	 */
	public abstract Packet turn(Dungeon dungeon, ArrayList<Actor> actors);

	final protected boolean occupied(Point3D point, ArrayList<Actor> actors)
	{
		for (Actor actor : actors)
		{
			if (actor.getPos().equals(point))
				return true;
		}
		return false;
	}

	/* Attempting attack and move methods */
	final protected boolean attemptMove(Dungeon dungeon, Point delta, ArrayList<Actor> actors)
	{
		Point3D newPosition = this.getPos().clone();
		newPosition.translate((int) delta.getX(), (int) delta.getY());

		if (this.isAttacking() && !this.attemptAttackReset())
			return false;

		// Verify the new Point3D is inside the map
		if (dungeon.validPoint(newPosition))
		{
			int x = (int) newPosition.getX();
			int y = (int) newPosition.getY();
			int z = newPosition.getZ();
			if (!occupied(newPosition, actors))
			{
				if (dungeon.isEmptyFloor(x, y, z))
				{
					this.setPos(newPosition);
					return true;
				}
				else if (dungeon.isStairUp(x, y, z))
				{
					this.setPos(newPosition);
					this.move(new Point3D(0, 0, 1));
					return true;
				}
				else if (dungeon.isStairDown(x, y, z))
				{
					this.setPos(newPosition);
					this.move(new Point3D(0, 0, -1));
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

	final protected boolean attemptAttack(Dungeon dungeon, Point3D delta)
	{
		Point3D newPosition = this.getPos().clone();
		newPosition.translate(delta.x, delta.y);

		int x = newPosition.x;
		int y = newPosition.y;
		int z = newPosition.getZ();

		if (this.isAttacking() && this.attemptAttackReset())
			return false;

		if (dungeon.validPoint(newPosition)
		    && dungeon.isEmptyFloor(new Point3D(x, y, z)))
		{
			this.setAttacking(true, delta);
			return true;
		}

		return false;
	}

	protected final boolean isAttacking()
	{
		return attacking;
	}

	protected final void setAttacking(boolean attacking, Point3D attackDelta)
	{
		if ((this.attacking = attacking))
		{
			attackCounter = ATTACK_COUNTER_MAX;
			this.attackDelta = (Point3D) attackDelta;
			if (this.attackDelta.equals(Point3D.UP) || this.attackDelta.equals(Point3D.DOWN))
			{
				attackSymbol = "|";
			}
			else if (this.attackDelta.equals(Point3D.LEFT) || this.attackDelta.equals(Point3D.RIGHT))
			{
				attackSymbol = "-";
			}
		}
	}

	protected final boolean attemptAttackReset()
	{
		if (attacking && attackCounter > 0)
		{
			attackCounter--;
			return false;
		}
		attacking = false;
		return true;
	}

	protected final String getAttackSymbol()
	{
		return attackSymbol;
	}

	protected final Point3D getAttackPos()
	{
		Point3D attackPos = this.pos.clone();
		attackPos.add(attackDelta);
		return attackPos;
	}

	protected final void setXAttack(int x)
	{
		this.attackDelta.x = x;
	}

	protected final void setYAttack(int y)
	{
		this.attackDelta.y = y;
	}

	protected final Point3D getDelta()
	{
		return attackDelta;
	}

}
