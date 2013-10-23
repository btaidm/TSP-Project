package com.tsp.client.model;

import java.awt.Point;

import com.googlecode.blacken.core.Random;
import com.tsp.game.map.MapGenerator;
import com.tsp.game.map.Point3D;

public class GameModel {

	//Actor and dungeon
	Point3D playerLocation;
	Point3D attackLocation;
	String attackAnimation = "-";
	int attackCounter = 0;
	private final int ATTACK_COUNTER_MAX = 3;
	
	String[][][] dungeon;

	//Dungeon properties
	private final int ROWS = 24;
	private final int COLS = 80;
	private final int FLOORS = 4;

	//Points corresponding to directions
	public static final Point LEFT = new Point(-1, 0);
	public static final Point RIGHT = new Point(1, 0);
	public static final Point UP = new Point(0, -1);
	public static final Point DOWN = new Point(0, 1);
	
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

	public GameModel(String[][][] _dungeon) {
		dungeon = _dungeon;

		//Choose a random location for the player
		Random r = new Random();
		playerLocation = new Point3D(r.nextInt(0, COLS), r.nextInt(0, ROWS), r.nextInt(0, FLOORS));

		System.out.println(playerLocation);
		while (dungeon[(int)playerLocation.getZ()][(int)playerLocation.getX()][(int) playerLocation.getY()].equals(WALL)) {
			playerLocation.move(r.nextInt(0, COLS), r.nextInt(0, ROWS));
		}
	}

	public GameModel(String[][][] _dungeon, Point3D _playerLocation) {
		dungeon = _dungeon;

		playerLocation = _playerLocation;

		System.out.println(playerLocation);
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
		} else if (attackLocation != null && j == attackLocation.getX() && i == attackLocation.getY() && z == attackLocation.getZ()) {
			return attackAnimation;
		}
		return this.dungeon[z][j][i];
	}

	public int getCurrentLevel() {
		return this.playerLocation.getZ();
	}

	public boolean resetAttack()
	{
		if (attackCounter > 0) {
			attackCounter--;
			return false;
		}

		// On every attempted move we want to be able to clear the attack
		clearAttack();
		return true;
	}

	public boolean attemptMove(Point delta) {
		Point3D newPosition = (Point3D) playerLocation.clone();
		newPosition.translate((int)delta.getX(), (int)delta.getY());

		if (attackCounter > 0) {
			attackCounter--;
			return false;
		}
		
		// On every attempted move we want to be able to clear the attack
		clearAttack();
		
		// Verify the new Point3D is inside the map
		if (inBounds(newPosition)) {
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

	public Point3D getPlayerLocation()
	{
		return playerLocation;
	}

	public boolean attemptAttack(Point delta) {
		Point3D newPosition = (Point3D) playerLocation.clone();
		newPosition.translate(delta.x, delta.y);
		
		int x = newPosition.x;
		int y = newPosition.y;
		int z = newPosition.getZ();
		
		if (attackCounter > 0) {
			attackCounter--;
			return false;
		}
		
		if (inBounds(newPosition) && this.dungeon[z][x][y].equals(EMPTY_FLOOR)) {
			attackLocation = newPosition;
			attackCounter = ATTACK_COUNTER_MAX;
			
			//Calculate the attack animation
			if (delta.equals(UP) || delta.equals(DOWN))
				attackAnimation = "|";
			else
				attackAnimation = "-";
			return true;
		}
		
		return false;
	}
	
	public void clearAttack() {
		attackLocation = null;
	}
	
	private boolean inBounds(Point p) {
		return p.y >= 0 && p.y < ROWS && p.x >= 0 && p.x < COLS;
	}
}
