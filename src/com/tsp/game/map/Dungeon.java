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

	/**
	 *
	 */
	public Dungeon()
	{
		mapGenerator = new MapGenerator(WALL, EMPTY_FLOOR, STAIR_UP, STAIR_DOWN);
		generateDungeon();
	}

	/**
	 *
	 */
	private void generateDungeon()
	{
		dungeon = mapGenerator.getMap(floors, rows, cols);
	}

	/**
	 *
	 * @param cols
	 * @param rows
	 * @param floors
	 */
	public Dungeon(int cols, int rows, int floors)
	{
		mapGenerator = new MapGenerator(WALL, EMPTY_FLOOR, STAIR_UP, STAIR_DOWN);
		this.cols = cols;
		this.rows = rows;
		this.floors = floors;
		generateDungeon();
	}

	/**
	 *
	 * @param mapGenerator
	 */
	public Dungeon(MapGenerator mapGenerator)
	{
		this.mapGenerator = mapGenerator;
		generateDungeon();
	}

	/**
	 *
	 * @param cols
	 * @param rows
	 * @param floors
	 * @param mapGenerator
	 */
	public Dungeon(int cols, int rows, int floors, MapGenerator mapGenerator)
	{

		this.cols = cols;
		this.rows = rows;
		this.floors = floors;
		this.mapGenerator = mapGenerator;
		generateDungeon();
	}

	/**
	 *
	 * @param dungeon
	 */
	public Dungeon(String[][][] dungeon)
	{
		mapGenerator = new MapGenerator(WALL, EMPTY_FLOOR, STAIR_UP, STAIR_DOWN);
		this.dungeon = dungeon;
		this.floors = dungeon.length;
		this.cols = dungeon[0].length;
		this.rows = dungeon[0][0].length;
	}

	/**
	 *
	 * @return
	 */
	public String[][][] getDungeon()
	{
		return dungeon;
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public String getTile(int x, int y, int z)
	{
		return dungeon[z][x][y];
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean validPoint(int x, int y, int z)
	{
		Point3D point = new Point3D(x, y, z);
		return validPoint(point);
	}

	/**
	 *
	 * @param point
	 * @return
	 */
	public boolean validPoint(Point3D point)
	{
		int x = (int) point.getX();
		int y = (int) point.getY();
		int z = point.getZ();

		return (x >= 0 && x < cols) && (y >= 0 && y < rows) && (z >= 0 && z < floors);
	}

	/**
	 *
	 * @return
	 */
	public int getColumns()
	{
		return cols;
	}

	/**
	 *
	 * @return
	 */
	public int getFloors()
	{
		return floors;
	}

	/**
	 *
	 * @return
	 */
	public int getRows()
	{
		return rows;
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean isEmptyFloor(int x, int y, int z)
	{
		return isEmptyFloor(new Point3D(x, y, z));
	}

	/**
	 *
	 * @param point
	 * @return
	 */
	public boolean isEmptyFloor(Point3D point)
	{
		return validPoint(point) && (getTile(point).equals(EMPTY_FLOOR));
	}

	/**
	 *
	 * @param point
	 * @return
	 */
	public String getTile(Point3D point)
	{
		return dungeon[point.getZ()][((int) point.getX())][((int) point.getY())];
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean isStairUp(int x, int y, int z)
	{
		return isStairUp(new Point3D(x, y, z));
	}

	/**
	 *
	 * @param point
	 * @return
	 */
	public boolean isStairUp(Point3D point)
	{
		return validPoint(point) && (getTile(point).equals(STAIR_UP));
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean isWall(int x, int y, int z)
	{
		return isWall(new Point3D(x, y, z));
	}

	/**
	 *
	 * @param point
	 * @return
	 */
	public boolean isWall(Point3D point)
	{
		return validPoint(point) && getTile(point).equals(WALL);
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean isStairDown(int x, int y, int z)
	{
		return isStairDown(new Point3D(x, y, z));
	}

	/**
	 *
	 * @param point
	 * @return
	 */
	public boolean isStairDown(Point3D point)
	{
		return validPoint(point) && (getTile(point).equals(STAIR_DOWN));
	}

	/**
	 *
	 * @param actors
	 * @return
	 * @throws Exception
	 */
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

	/**
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param actors
	 * @return
	 */
	private boolean occupied(int x, int y, int z, Collection<Actor> actors)
	{
		for (Actor actor : actors)
		{
			if (actor.getPos().equals(new Point3D(x, y, z)))
				return true;
		}
		return false;
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean walkableTile(int x, int y, int z)
	{
		Point3D point = new Point3D(x, y, z);
		return walkableTile(point);
	}

	/**
	 *
	 * @param point
	 * @return
	 */
	public boolean walkableTile(Point3D point)
	{
		return validPoint(point) && (getTile(point).equals(EMPTY_FLOOR) || getTile(point).equals(STAIR_DOWN) ||
		                             getTile(point).equals(STAIR_UP));
	}
}
