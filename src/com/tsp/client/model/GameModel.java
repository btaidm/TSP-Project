package client.model;

import java.awt.Point;

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
	
	public GameModel() {
		playerLocation = new Point(0, 0);
		dungeon = new String[ROWS][COLS];
	
		for (int i = 0; i < dungeon.length; i++) {
			for (int j = 0; j < dungeon[i].length; j++) {
				dungeon[i][j] = EMPTY_FLOOR;
			}
		}
		
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
		if (newPosition.getX() >= 0 && newPosition.getX() < 24 && 
			newPosition.getY() >= 0 && newPosition.getY() < 80) {
			
			dungeon[(int) playerLocation.getX()][(int) playerLocation.getY()] = EMPTY_FLOOR;
			dungeon[(int) newPosition.getX()][(int) newPosition.getY()] = PLAYER;
			playerLocation = newPosition;
			
			return true;
		}
		
		return false;
	}
}
