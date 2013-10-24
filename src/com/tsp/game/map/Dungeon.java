package com.tsp.game.map;

import com.tsp.game.actors.Actor;

import java.util.Collection;

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
	int cols = 80;
	int rows = 24;
	int floors = 4;
	private String[][][] dungeon;
	private MapGenerator mapGenerator;

	public Dungeon()
	{
		mapGenerator = new MapGenerator(WALL, EMPTY_FLOOR, STAIR_UP, STAIR_DOWN);
		generateDungeon();
	}

	public Dungeon(int cols, int rows, int floors)
	{
		mapGenerator = new MapGenerator(WALL, EMPTY_FLOOR, STAIR_UP, STAIR_DOWN);
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

	public Dungeon(String[][][] dungeon)
	{
		mapGenerator = new MapGenerator(WALL, EMPTY_FLOOR, STAIR_UP, STAIR_DOWN);
		this.dungeon = dungeon;
		this.floors = dungeon.length;
		this.cols = dungeon[0].length;
		this.rows = dungeon[0][0].length;
	}

	private void generateDungeon()
	{
		dungeon = mapGenerator.getMap(floors, rows, cols);
	}

	public String[][][] getDungeon()
	{
		return dungeon;
	}

	public boolean walkableTile(Point3D point)
	{
		return validPoint(point) && (getTile(point).equals(EMPTY_FLOOR) || getTile(point).equals(STAIR_DOWN) ||
		                             getTile(point).equals(STAIR_UP));
	}

	public boolean validPoint(Point3D point)
	{
		int x = (int) point.getX();
		int y = (int) point.getY();
		int z = point.getZ();

		return (x >= 0 && x < cols) && (y >= 0 && y < rows) && (z >= 0 && z < floors);
	}

	public boolean isStairUp(Point3D point)
	{
		return validPoint(point) && (getTile(point).equals(STAIR_UP));
	}

	public boolean isStairDown(Point3D point)
	{
		return validPoint(point) && (getTile(point).equals(STAIR_DOWN));
	}

	public boolean isEmptyFloor(Point3D point)
	{
		return validPoint(point) && (getTile(point).equals(EMPTY_FLOOR));
	}

	public boolean isWall(Point3D point)
	{
		return validPoint(point) && getTile(point).equals(WALL);
	}

	public String getTile(Point3D point)
	{
		return dungeon[point.getZ()][((int) point.getX())][((int) point.getY())];
	}

	public String getTile(int x, int y, int z)
	{
		return dungeon[z][x][y];
	}

	public boolean validPoint(int x, int y, int z)
	{
		Point3D point = new Point3D(x, y, z);
		return validPoint(point);
	}

	public boolean walkableTile(int x, int y, int z)
	{
		Point3D point = new Point3D(x, y, z);
		return walkableTile(point);
	}

	public int getColumns()
	{
		return cols;
	}

	public int getFloors()
	{
		return floors;
	}

	public int getRows()
	{
		return rows;
	}

	public boolean isEmptyFloor(int x, int y, int z)
	{
		return isEmptyFloor(new Point3D(x, y, z));
	}

	public boolean isStairUp(int x, int y, int z)
	{
		return isStairUp(new Point3D(x, y, z));
	}

	public boolean isWall(int x, int y, int z)
	{
		return isWall(new Point3D(x, y, z));
	}

	public boolean isStairDown(int x, int y, int z)
	{
		return isStairDown(new Point3D(x, y, z));
	}

	public Point3D findFirstWalkablePoint(Collection<Actor> actors) throws Exception
	{
		for (int z = 0; z < this.floors; z++)
		{
			for (int x = 0; x < this.cols; x++)
			{
				for (int y = 0; y < this.rows; y++)
				{
					if (walkableTile(x, y, z) && !occupied(x,y,z,actors))
					{
						return new Point3D(x,y,z);
					}
				}
			}
		}
		throw new Exception("No point available");
	}

	private boolean occupied(int x, int y, int z, Collection<Actor> actors)
	{
		for (Actor actor : actors)
		{
			if (actor.getPos().equals(new Point3D(x, y, z)))
				return true;
		}
		return false;
	}
}
