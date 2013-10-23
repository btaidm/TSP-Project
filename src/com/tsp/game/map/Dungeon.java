package com.tsp.game.map;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/22/13
 * Time: 1:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class Dungeon
{
	public static final String EMPTY_FLOOR = " ";
	public static final String WALL = "#";
	public static final String STAIR_UP = "\u25B2";
	public static final String STAIR_DOWN = "\u25BC";

	private String[][][] dungeon;
	int cols = 80;
	int rows = 24;
	int floors = 4;

	private MapGenerator mapGenerator;

	public Dungeon()
	{
		mapGenerator = new MapGenerator(WALL,EMPTY_FLOOR,STAIR_UP,STAIR_DOWN);
		generateDungeon();
	}

	public Dungeon(int cols, int rows, int floors)
	{
		mapGenerator = new MapGenerator(WALL,EMPTY_FLOOR,STAIR_UP,STAIR_DOWN);
		this.cols = cols;
		this.rows = rows;
		this.floors = floors;
		generateDungeon();
	}

	public Dungeon(MapGenerator mapGenerator)
	{
		this.mapGenerator = mapGenerator;
		generateDungeon();
	}

	public Dungeon(int cols, int rows, int floors, MapGenerator mapGenerator)
	{
		this.cols = cols;
		this.rows = rows;
		this.floors = floors;
		this.mapGenerator = mapGenerator;
		generateDungeon();
	}

	private void generateDungeon()
	{
		dungeon = mapGenerator.getMap(floors,cols,rows);
	}

	public String[][][] getDungeon()
	{
		return dungeon;
	}

	public boolean validPoint(Point3D point)
	{
		return getTile(point).equals(EMPTY_FLOOR) || getTile(point).equals(STAIR_DOWN) || getTile(point).equals(STAIR_UP);
	}

	public String getTile(Point3D point)
	{
		return dungeon[point.getZ()][((int) point.getX())][((int) point.getY())];
	}

}
