package client.model;

import java.awt.Point;

import com.googlecode.blacken.core.Random;

public class GameModel {

	//Character and dungeon
	Point playerLocation;
	String[][] dungeon;
	
	//Dungeon properties
	private final int ROWS = 24;
	private final int COLS = 80;
	
	//Game tile types
	public static final String PLAYER = "@";
	public static final String EMPTY_FLOOR = " ";
	public static final String WALL = "#";
	
	public GameModel() {
		FloorGenerator f = new FloorGenerator(WALL, EMPTY_FLOOR);
		dungeon = f.GetFullMap(ROWS, COLS);
		/*
		for (int i = 0; i < dungeon.length; i++) {
			for (int j = 0; j < dungeon[i].length; j++) {
				dungeon[i][j] = EMPTY_FLOOR;
			}
		}
		*/
		
		//Choose a random location for the player
		Random r = new Random();
		playerLocation = new Point(r.nextInt(0, COLS), r.nextInt(0, ROWS));
		while (dungeon[(int)playerLocation.getY()][(int)playerLocation.getX()].equals(WALL)) {
			playerLocation.move(r.nextInt(0, COLS), r.nextInt(0, ROWS));
		}
		
		System.out.println(playerLocation);
		dungeon[(int) playerLocation.getY()][(int) playerLocation.getX()] = PLAYER;
	}
	
	public int dungeonRows() {
		return ROWS;
	}
	
	public int dungeonCols() {
		return COLS;
	}
	
	public String get(int i, int j) {
		return this.dungeon[i][j];
	}
	
	public boolean attemptMove(Point delta) {
		Point newPosition = (Point) playerLocation.clone();
		newPosition.translate((int)delta.getX(), (int)delta.getY());
		
		// Verify the new point is inside the map
		if (newPosition.getY() >= 0 && newPosition.getY() < 24 && 
			newPosition.getX() >= 0 && newPosition.getX() < 80 &&
			this.dungeon[(int)newPosition.getY()][(int)newPosition.getX()].equals(EMPTY_FLOOR)) {
			
			dungeon[(int) playerLocation.getY()][(int) playerLocation.getX()] = EMPTY_FLOOR;
			dungeon[(int) newPosition.getY()][(int) newPosition.getX()] = PLAYER;
			playerLocation = newPosition;
			
			return true;
		}
		
		return false;
	}
}
