package com.tsp.game.map;

import java.awt.Point;

public class Point3D extends Point
{

	//Points corresponding to directions
	public static final Point3D LEFT = new Point3D(-1, 0);
	public static final Point3D RIGHT = new Point3D(1, 0);
	public static final Point3D UP = new Point3D(0, -1);
	public static final Point3D DOWN = new Point3D(0, 1);
	/**
	 *
	 */
	private static final long serialVersionUID = 254380063536084963L;

	private int z;

	public Point3D(int x, int y, int z)
	{
		super(x, y);
		this.z = z;
	}

	public Point3D(int x, int y)
	{
		super(x, y);
		this.z = 0;
	}

	public int getZ()
	{
		return this.z;
	}

	public void setZ(int z)
	{
		this.z = z;
	}

	public void moveZ(int amount)
	{
		this.z += amount;
	}

	@Override
	public Point3D clone()
	{
		return new Point3D(this.x, this.y, this.z);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if(!(super.equals(o))) return false;
		Point3D point3D = (Point3D) o;

		return this.z == point3D.getZ();
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + z;
		return result;
	}

	@Override
	public String toString()
	{
		return "Point3D[X=" + this.x + ",Y=" + this.y + ",Z=" + this.z + "]";
	}

	public void add(Point3D movement)
	{
		this.x += movement.x;
		this.y += movement.y;
		this.z += movement.z;
	}

	public void add(Point movement)
	{
		this.x += movement.x;
		this.y += movement.y;
	}
}
