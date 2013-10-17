package com.tsp.client.model;

import java.awt.Point;

import com.googlecode.blacken.core.Random;

public class GameModel {

	//Character and dungeon
	Point3D playerLocation;
	String[][][] dungeon;

	//Dungeon properties
	private final int ROWS = 24;
	private final int COLS = 80;
	private final int FLOORS = 4;

	//Game tile types
	public static final String PLAYER = "@";
	public static final String EMPTY_FLOOR = " ";
	public static final String WALL = "#";
	public static final String STAIR_UP = "\u25B2";
	public static final String STAIR_DOWN = "\u25BC";

	public GameModel() {
		MapGenerator f = new MapGenerator(WALL, EMPTY_FLOOR, STAIR_UP, STAIR_DOWN);
		dungeon = f.getMap(FLOORS, ROWS, COLS);

		//Choose a random location for the player
		Random r = new Random();
		playerLocation = new Point3D(r.nextInt(0, COLS), r.nextInt(0, ROWS), r.nextInt(0, FLOORS));

		System.out.println(playerLocation);
		while (dungeon[(int)playerLocation.getZ()][(int)playerLocation.getX()][(int) playerLocation.getY()].equals(WALL)) {
			playerLocation.move(r.nextInt(0, COLS), r.nextInt(0, ROWS));
		}
	}

	public int dungeonRows() {
		return ROWS;
	}

	public int dungeonCols() {
		return COLS;
	}

	public String get(int i, int j, int z) {
		if (j == playerLocation.getX() && i == playerLocation.getY() && z == playerLocation.getZ()) {
			return PLAYER;
		}
		return this.dungeon[z][j][i];
	}

	public int getCurrentLevel() {
		return this.playerLocation.getZ();
	}

	public boolean attemptMove(Point delta) {
		Point3D newPosition = (Point3D) playerLocation.clone();
		newPosition.translate((int)delta.getX(), (int)delta.getY());

		// Verify the new Point3D is inside the map
		if (newPosition.getY() >= 0 && newPosition.getY() < 24 && newPosition.getX() >= 0 && newPosition.getX() < 80) {
			int x = (int) newPosition.getX();
			int y = (int) newPosition.getY();
			int z = newPosition.getZ();
			
			if (this.dungeon[z][x][y].equals(EMPTY_FLOOR)) {
				playerLocation = newPosition;
				return true;
			} else if (this.dungeon[z][x][y].equals(STAIR_UP)) {
				playerLocation = newPosition;
				playerLocation.moveZ(1);
				return true;
			} else if (this.dungeon[z][x][y].equals(STAIR_DOWN)) {
				playerLocation = newPosition;
				playerLocation.moveZ(-1);
				return true;
			} else {
				// Do nothing here
			}
		}
		return false;
	}
}
