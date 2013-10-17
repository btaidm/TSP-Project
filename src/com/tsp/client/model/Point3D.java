package com.tsp.client.model;

import java.awt.Point;

public class Point3D extends Point {

	/**
	 * 
	 */
	private static final long serialVersionUID = 254380063536084963L;

	private int z;
	
	public Point3D(int x, int y, int z) {
		super(x, y);
		this.z = z;
	}
	
	public int getZ() {
		return this.z;
	}

	public void moveZ(int amount) {
		this.z += amount;
	}
	@Override
	public Point3D clone() {
		return new Point3D(this.x, this.y, this.z);
	}
	
	@Override
	public String toString() {
		return "Point3D[X=" + this.x + ",Y=" + this.y + ",Z=" + this.z + "]";
	}
}
