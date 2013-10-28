package com.tsp.game.actors;

import com.googlecode.blacken.core.Random;
import com.sun.scenario.effect.light.PointLight;
import com.tsp.game.map.Point3D;
import org.json.simple.JSONObject;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/18/13
 * Time: 10:05 AM
 * To change this template use File | Settings | File Templates.
 */
public final class Player extends Actor
{
	static int playerCount = 0;
	final int ATTACK_COUNTER_MAX = 3;
	String attackSymbol = "-";
	boolean attacking = false;
	Point3D attackDelta = new Point3D(0,0,0);
	int attackCounter = 0;

	/**
	 * Creates a new player with name of Player count
	 * @param COLS the upper bound of the x pos
	 * @param ROWS the upper bound of the y pos
	 * @param LVLS the upper bound of the z pos
	 */
	public Player(int COLS, int ROWS, int LVLS)
	{
		this();
		newPosition(COLS,ROWS,LVLS);
		health = 10;
		playerCount++;
		name = "Player " + playerCount;
		color = (int)(Math.random()*254) + 1;
	}

	private Player()
	{
		type = ActorType.ACTOR_PLAYER;
		symbol = "@";

	}

	/**
	 * Creates a new player
	 * @param _name
	 * @param COLS
	 * @param ROWS
	 * @param LVLS
	 */
	public Player(String _name, int COLS, int ROWS, int LVLS)
	{
		this();
		Random r = new Random();
		pos = new Point3D(r.nextInt(0, COLS), r.nextInt(0, ROWS), r.nextInt(0,LVLS));
		name = _name;
		health = 10;
		playerCount++;
		color = (int)(Math.random()*254) + 1;
	}

	public Player(Actor actor)
	{
		this();
		this.pos = actor.getPos();
		this.name = actor.getName();
		this.color = actor.getColor();
		this.health = actor.getHealth();
		this.symbol = actor.getSymbol();
		this.id = actor.getId();
	}

	public boolean isAttacking()
	{
		return attacking;
	}

	public void setAttacking(boolean attacking, Point3D attackDelta)
	{
		if((this.attacking = attacking))
		{
			attackCounter = ATTACK_COUNTER_MAX;
			this.attackDelta = (Point3D) attackDelta;
			if(this.attackDelta.equals(Point3D.UP) || this.attackDelta.equals(Point3D.DOWN))
			{
				attackSymbol = "|";
			}
			else if(this.attackDelta.equals(Point3D.LEFT) || this.attackDelta.equals(Point3D.RIGHT))
			{
				attackSymbol = "-";
			}
		}
	}

	public boolean attemptAttackReset()
	{
		if(attacking && attackCounter > 0)
		{
			attackCounter--;
			return false;
		}
		attacking = false;
		return true;
	}

	public String getAttackSymbol()
	{
		return attackSymbol;
	}

	public Point3D getAttackPos()
	{
		Point3D attackPos = this.pos.clone();
		attackPos.add(attackDelta);
		return attackPos;
	}

	public void setXAttack(int x)
	{
		this.attackDelta.x = x;
	}

	public void setYAttack(int y)
	{
		this.attackDelta.y = y;
	}

	public Point3D getDelta()
	{
		return attackDelta;
	}
}
