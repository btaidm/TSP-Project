package com.tsp.game.characters;

import com.googlecode.blacken.core.Random;
import com.tsp.game.map.Point3D;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/18/13
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Character
{

	Point3D pos;
	String name;
	int id;
	int health;

	boolean checkHit(int x, int y, int z)
	{

		return checkHit(new Point3D(x, y, z));
	}

	boolean checkHit(Point3D check)
	{
		return pos.equals(check);
	}

	int getDamage()
	{
		return 1;
	}

	public Point3D getPos()
	{
		return pos;
	}

	public void move(int x, int y)
	{
		move(new Point3D(x, y, 0));
	}

	public void move(int x, int y, int z)
	{
		move(new Point3D(x, y, z));
	}

	public void move(Point3D movement)
	{
		pos.add(movement);
	}

	public void setPos(Point3D pos)
	{
		this.pos = pos;
	}

	public String getName()
	{
		return name;
	}

	public int getHealth()
	{
		return health;
	}


	public int getId()
	{
		return id;
	}

	public void hit(Character attacking)
	{
		this.health -= attacking.getDamage();
	}

	public void newPosition(int COLS, int ROWS, int LVLS)
	{
		Random r = new Random();
		pos = new Point3D(r.nextInt(0, COLS), r.nextInt(0, ROWS), r.nextInt(0,LVLS));
	}
}
