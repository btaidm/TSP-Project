package com.tsp.game.map;

import com.tsp.game.actors.Actor;
import com.tsp.game.actors.Player;

import java.util.*;

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
	public static final String STAIR_UP = "U";//"\u25B2";
	public static final String STAIR_DOWN = "P";//"\u25BC";
	public static final String UNREVEALED = "&";
	int fogOfWarHeight = 2;
	int fogOfWarWidth = 4;
	int cols = 80;
	int rows = 24;
	int floors = 4;
	private String[][][] dungeon;
	private MapGenerator mapGenerator;
	boolean visible[][][];
	FogOfWar fogOfWar;
	private boolean reveal = false;

	/**
	 *
	 */
	public Dungeon()
	{
		mapGenerator = new MapGenerator(WALL, EMPTY_FLOOR, STAIR_UP, STAIR_DOWN);
		generateDungeon();
		fogOfWar = new FogOfWar(dungeon , fogOfWarHeight, fogOfWarWidth);
	}

	/**
	 *
	 */
	private void generateDungeon()
	{
		dungeon = mapGenerator.getMap(floors, rows, cols);
		visible = new boolean[dungeon.length][dungeon[0].length][dungeon[0][0].length];
		for(int i = 0; i < visible.length; i++){
			for(int j = 0; j < visible[0].length; j++){
				for(int k = 0; k < visible[0][0].length; k++){
					visible[i][j][k] = false;
				}
			}
		}
		
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
		fogOfWar = new FogOfWar(dungeon , fogOfWarHeight, fogOfWarWidth);
	}

	/**
	 *
	 * @param mapGenerator
	 */
	public Dungeon(MapGenerator mapGenerator)
	{
		this.mapGenerator = mapGenerator;
		generateDungeon();
		fogOfWar = new FogOfWar(dungeon , fogOfWarHeight, fogOfWarWidth);
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
		fogOfWar = new FogOfWar(dungeon , fogOfWarHeight, fogOfWarWidth);
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
		visible = new boolean[this.floors][this.cols][this.rows];
		for(int i = 0; i < dungeon.length; i++){
			for(int j = 0; j < dungeon[i].length; j++){
				for(int k = 0; k < dungeon[i][j].length; k++){
					visible[i][j][k] = false;
				}
			}
		}
		fogOfWar = new FogOfWar(dungeon , fogOfWarHeight, fogOfWarWidth);
	}

	/**
	 *
	 * @return
	 */
	public String[][][] getDungeon()
	{
		return dungeon;
	}
	
	public void updateVisibleDungeon(Player player){
		boolean[][][] currentRevealed = fogOfWar.GetFogOfWar(player.getPos());
		visible = mergeVisible(currentRevealed, visible);
	}
	
	private boolean[][][] mergeVisible(boolean[][][] currentRevealed, boolean[][][] visible){
		for(int i = 0; i < currentRevealed.length; i++){
			for(int j = 0; j < currentRevealed[0].length; j++){
				for(int k = 0; k < currentRevealed[0][0].length; k++){
					if(currentRevealed[i][j][k] || visible[i][j][k]){
						visible[i][j][k] = true;
					}
				}
			}
		}
		return visible;
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
	
	public boolean isUnrevealed(Point3D point)
	{
		return validPoint(point) && (getTile(point).equals(UNREVEALED));
	}
	
	public boolean isUnrevealed(int x, int y, int z)
	{
		return isUnrevealed(new Point3D(x, y, z));
	}

	/**
	 *
	 * @param point
	 * @return
	 */
	public String getTile(Point3D point)
	{
		String retPoint;
		if(reveal || pointIsVisible(point)){
			retPoint = dungeon[point.getZ()][((int) point.getX())][((int) point.getY())];
		}
		else{
			retPoint = UNREVEALED;
		}
		return retPoint;
	}
	
	public void revealAll()
	{
		this.reveal = true;
	}
	
	private boolean pointIsVisible(Point3D point){
		return visible[point.getZ()][point.x][point.y];
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


	private class FloorGenerator {
		String[][][] rooms;
		double oneToZero = 0;
		boolean ascending = true;
		boolean decending = false;
		String wall;
		String floor;
		
		public FloorGenerator(String wall, String floor) {
			this.wall = wall;
			this.floor = floor;
		}

		public String[][] GetFullMap(int length, int width){

			String[][] map = GenMap(length, width);
			while(oneToZero < .38 || oneToZero > .45){
				map = GenMap(length, width);
				oneToZero = getOneToZero(map, floor, wall);
			}
			String[][] temp;
			int k = 0;
			do{
				temp = getMapCopy(map);
				double wallToFloor = Math.random() * 2 + Math.random();
				double floorToWall = Math.random() * 3 + Math.random();
				temp = changeXToY(temp, floor, wall, wallToFloor, decending);
				temp = changeXToY(temp, wall, floor, floorToWall, ascending);
				if(oneToZero > getOneToZero(temp, floor, wall) && oneToZero > .38){
					map = temp;
				}
				oneToZero = getOneToZero(temp, floor, wall);
				k++;
			}while(((oneToZero < .42 || oneToZero > .47))&& k < 100);
			map = temp;

			return map;
		}
		
		public String[][] getMidMap(int length, int width){
			String[][] map = GenMap(length, width);
			while(oneToZero < .38 || oneToZero > .45){
				map = GenMap(length, width);
				oneToZero = getOneToZero(map, floor, wall);
			}
			String[][] temp;

			int k = 0;
			do{
				temp = getMapCopy(map);
				double wallToFloor = Math.random() * 2 + Math.random();
				double floorToWall = Math.random() * 3 + Math.random();
				temp = changeXToY(temp, floor, wall, wallToFloor, decending);
				temp = changeXToY(temp, wall, floor, floorToWall, ascending);
				if(oneToZero > getOneToZero(temp, floor, wall) && oneToZero > .38){
					map = temp;
				}
				oneToZero = getOneToZero(temp, floor, wall);
				k++;
			}while(((oneToZero < .35 || oneToZero > .40)) && k < 100);
			map = temp;
			return map;
		}

		public String[][] getSparseMap(int length, int width){
			String[][] map = GenMap(length, width);
			while(oneToZero < .34 || oneToZero > .40){
				map = GenMap(length, width);
				oneToZero = getOneToZero(map, floor, wall);
				
			}

			String[][] temp = new String[0][0];
			int k = 0; 
			do{
				temp = getMapCopy(map);
				double wallToFloor = Math.random();
				double floorToWall = Math.random() * 2 + Math.random();
				temp = changeXToY(temp, floor, wall, wallToFloor, decending);
				temp = changeXToY(temp, wall, floor, floorToWall, ascending);
				if(oneToZero > getOneToZero(temp, floor, wall) && getOneToZero(temp, floor, wall) > .3){
					map = temp;
				}
				oneToZero = getOneToZero(temp, floor, wall);
				k++;
			}while((oneToZero < .27 || oneToZero > .33) && k < 100);
			map = temp;

			return map;
		}
		
		public void mapPointsOnFloor(String[][] floorMap){
			RoomManager manager = new RoomManager();
			for(int i = 0; i < floorMap.length; i++){
				for(int j = 0; j < floorMap[i].length; j++){
					if(floorMap[i][j] == floor){
						manager.addPoint(i, j);
					}
				}
			}
			int[][] randomCoords = manager.getRandomRoomCoordinates();
			for(int i = 0; i < randomCoords.length; i++){
				int[] temp = randomCoords[i];
				floorMap = mapBetweenPoints(floorMap, temp[0], temp[1], temp[2], temp[3]);
			}
		}
		
		private String[][] mapBetweenPoints(String[][] map, int x1, int y1, int x2, int y2){
			while(x1 != x2 || y1 != y2){
				map[x1][y1] = floor;
				if(x1 != x2){
					if(x1 > x2){
						x1 = x1-1;
					}
					else{
						x1 = x1+1;
					}
				}
				else{
					if(y1 > y2){
						y1 = y1-1;
					}
					else{
						y1 = y1+1;
					}
				}
			}
			return map;
		}

		private String[][] GenMap(int length, int width){
			String[][] map = new String[length][width];
			for(int i = 0; i < map.length; i++){
				for(int j = 0; j < map[i].length; j++){
					map[i][j] = wall;
				}
			}
			map = changeXToY(map, wall, floor, 2, ascending);
			return map;

		}

		private String[][] changeXToY(String[][] map, String x, String y, double roomNumberDivisor, boolean direction){
			int length = map.length;
			int width = map[0].length;
			int maxRoomLength = length / 3;
			int maxRoomWidth = width / 3;

			double diff = length / width;
			if(diff > 1){diff = width / length;}
			if(diff > .5){diff = 1 - diff;}
			for(int i = 0; i < ((length + width)/roomNumberDivisor) && i < ((width + length)/2); i++){
				int roomLength = (int)(Math.floor(Math.random() * (maxRoomLength - 2))) + 2;
				int roomWidth = (int)(Math.floor(Math.random() * (maxRoomWidth - 2))) + 2;

				int roomStartX = (int)(Math.floor(Math.random() * length));
				int roomStartY = (int)(Math.floor(Math.random() * width));
				if(Math.random() > .5){
					if(Math.random() > .5){
						for(int j = roomStartX; j < roomStartX + roomLength && roomStartX + j < map.length ; j++){
							for(int k = roomStartY; k < roomStartY + roomWidth && k < map[j].length; k++){

								map[j][k] = y;


							}

						}
					}
					else{
						for(int j = roomStartX; j < roomStartX + roomLength && roomStartX + j < map.length ; j++){
							for(int k = roomStartY; k > roomStartY - roomWidth && k >= 0; k--){

								map[j][k] = y;


							}

						}
					}
				}
				else{
					if(Math.random() > .5){
						for(int j = roomStartX; j > roomStartX - roomLength && j >= 0; j--){
							for(int k = roomStartY; k > roomStartY - roomWidth && k >= 0; k--){

								map[j][k] = y;

							}

						}
					}
					else{

						for(int j = roomStartX; j < roomStartX + roomLength && roomStartX + j < map.length ; j++){
							for(int k = roomStartY; k > roomStartY - roomWidth && k >= 0; k--){

								map[j][k] = y;

							}
						}
					}
				}
			}
			return map;
		}

		public void printFloor(String[][] map){
			for(int i = 0; i < map.length; i++){
				for(int j = 0; j < map[i].length; j++){
					System.out.print(map[i][j]);
				}
				System.out.print("\n");
			}
		}

		private double getOneToZero(String[][] map, String one, String zero){
			double result = 0;
			double oneCount = 0;
			double zeroCount = 0;

			for(int i = 0; i < map.length; i++){
				for(int j = 0; j < map[i].length; j++){
					if(map[i][j] == one){
						oneCount++;
					}
					else{
						zeroCount++;
					}
				}
			}
			result = oneCount / (oneCount + zeroCount);
			return result;
		}
		
		private String[][] getMapCopy(String[][] map){
			String[][] copy = new String[map.length][];
			for(int i = 0; i < map.length; i++){
				copy[i] = Arrays.copyOf(map[i], map[i].length);
			}
			return copy;
		}
	
	}

	private class MapGenerator {
		private FloorGenerator floorGenerator;

		private String stairUp;
		private String stairDown;
		private String wall;

		public MapGenerator(String wall, String floor, String stairUp, String stairDown){
			floorGenerator = new FloorGenerator(wall, floor);
			this.stairUp = stairUp;
			this.stairDown = stairDown;
			this.wall = wall;
		}

		/**
		 * creates a map with given height, length, and width.
		 * 
		 * @param height the height of the map. z levels
		 * @param width the width of the map. x
		 * @param length the length of the map. y
		 * @return returns a map as a String[][][]
		 */
		public String[][][] getMap(int height, int width, int length){
			String[][][] map = new String[height][][];

			for(int i = 0; i < height; i++){
				/*
				 * 50% chance of a mid-map, 25% of the other 2
				 */
				if(Math.random() > .5){
					map[i] = floorGenerator.getMidMap(length, width);
				}
				else if(Math.random() > .5){
					map[i] = floorGenerator.GetFullMap(length, width);
				}
				else{
					map[i] = floorGenerator.getSparseMap(length, width);
				}
			}
			mapPaths(map);
			mapStairs(map);

			return map;
		}

		/**
		 * prints out the full map
		 * @param map
		 */

		public void printFullMap(String[][][] map){
			for(int i = 0; i < map.length; i++){
				System.out.println("Floor " + i);
				for(int j = 0; j < map[i].length; j++){
					for(int k = 0; k < map[i][j].length; k++){
						System.out.print(map[i][j][k]);
					}
					System.out.println();
				}
				System.out.println("\n");
			}
		}

		/**
		 * maps paths between points on a floor for a map.
		 * @param map
		 */

		private void mapPaths(String[][][] map){

			for(String[][] floor: map){
				floorGenerator.mapPointsOnFloor(floor);
			}
		}

		/**
		 * Map stairs between floors
		 */
		private void mapStairs(String[][][] map) {
			Random r = new Random();

			// For each floor in the map
			for(int i = 0; i < map.length - 1; i++) {
				//Choose a random location on the floor for a stair and then choose
				//the same location on the floor above.  If stairs can go in both places,
				//put them there, otherwise don't.

				boolean placedStairs = false;
				while (!placedStairs) {
					int x = r.nextInt(map[i].length);
					int y = r.nextInt(map[i][0].length);

					if (!map[i][x][y].equals(this.wall) && !map[i][x][y].equals(this.stairUp) &&
						!map[i+1][x][y].equals(this.wall) && !map[i][x][y].equals(this.stairDown)) {
						placedStairs = true;
						
						map[i][x][y] = stairUp;
						map[i+1][x][y] = stairDown;
					}
				}
			}
		}
	}

	private class Room {
		private int[][] room;
		
		public Room(){
			room = new int[0][];
		}
		
		public boolean roomContainsPoint(int x, int y){
			for(int i = 0; i < room.length; i++){
				if(room[i].length != 2){
					return false;
				}
				if(room[i][0] == x && room[i][1] == y){
					return true;
				}
			}
			return false;
		}
		
		public boolean roomAdjacentToPoint(int x, int y){
			return(roomContainsPoint(x + 1, y) || roomContainsPoint(x - 1, y) ||
					roomContainsPoint(x, y + 1) || roomContainsPoint(x, y - 1));
		}
		
		public void addPointToRoom(int x, int y){
			room = Arrays.copyOf(room, room.length + 1);
			room[room.length - 1] = new int[2];
			room[room.length - 1][0] = x;
			room[room.length - 1][1] = y;
		}
		
		public void mergeRooms(Room roomToMerge){
			int mergeRoomLength = roomToMerge.getRoom().length;
			int[][] newRoom = new int[mergeRoomLength + room.length][];
			System.arraycopy(room, 0, newRoom, 0, room.length);
			System.arraycopy(roomToMerge.getRoom(), 0, newRoom, room.length, mergeRoomLength);
			room = newRoom;
		}
		
		public int[][] getRoom(){
			return room;
		}
		
		public int[] getRandomCoord(){
			int loc = (int)(Math.random() * room.length);
			return room[loc];
		}
	}

	private class RoomManager {
		
		private ArrayList<Room> rooms;
		
		public RoomManager(){
			rooms = new ArrayList<Room>();
		}
		
		public boolean anyRoomsContainPoint(int x, int y){
			for(int i = 0; i < rooms.size(); i++){
				if(rooms.get(i).roomContainsPoint(x, y)){
					return true;
				}
			}
			return false;
		}
		
		public boolean anyRoomsAdjacentToPoint(int x, int y){
			for(int i = 0; i < rooms.size(); i++){
				if(rooms.get(i).roomAdjacentToPoint(x, y)){
					return true;
				}
			}
			return false;
		}
		
		public void addPoint(int x, int y){
			boolean putIntoRoom = false;
			for(int i = 0; i < rooms.size(); i++){
				if(rooms.get(i).roomAdjacentToPoint(x, y)){
					rooms.get(i).addPointToRoom(x, y);
					putIntoRoom = true;
				}
			}
			if(!putIntoRoom){
				rooms.add(new Room());
				rooms.get(rooms.size() - 1).addPointToRoom(x, y);
			}
			mergeNewlyAdjacentRooms(x, y);
		}
		
		public void mergeNewlyAdjacentRooms(int x, int y){
			ArrayList<Room> roomsNeedingMerged = new ArrayList<Room>();
			for(Room room: rooms){
				if(room.roomContainsPoint(x, y)){
					roomsNeedingMerged.add(room);
				}
			}
			
			while(roomsNeedingMerged.size() > 1){
				roomsNeedingMerged.get(0).mergeRooms(getLastElement(roomsNeedingMerged));
				rooms.remove(getLastElement(roomsNeedingMerged));
				roomsNeedingMerged.remove(getLastElement(roomsNeedingMerged));
			}
			
		}
		
		private Room getLastElement(ArrayList<Room> arrayList){
			return arrayList.get(arrayList.size() - 1);
		}
		
		public int howManyRoomsContainPoint(int x, int y){
			int count = 0;
			for(int i = 0; i < rooms.size(); i++){
				if(rooms.get(i).roomContainsPoint(x, y)){
					count++;
				}
			}
			return count;
		}
		
		public int[][] getRandomRoomCoordinates(){
			int[][] roomCoords = new int[0][];
			
			for(int i = 1; i < rooms.size(); i++){
				Room room = rooms.get(i);
				roomCoords = Arrays.copyOf(roomCoords, roomCoords.length + 1);
				
				roomCoords[roomCoords.length - 1] = room.getRandomCoord();
				
				roomCoords[roomCoords.length - 1] = Arrays.copyOf(roomCoords[roomCoords.length - 1],
						roomCoords[roomCoords.length - 1].length + 2);
				
				System.arraycopy(rooms.get(i-1).getRandomCoord(), 0,
						roomCoords[roomCoords.length - 1], 2, 2);
				
			}
			
			return roomCoords;
		}
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
	
	private class FogOfWar {
		
		String[][][] map;
		double height;
		double width;
		
		
		public FogOfWar(String[][][] dungeon, double height, double width){
			this.map = dungeon;
			this.height = height;
			this.width = width;
		}
		/**
		 * finds all points visible in a circle around the given point
		 * 
		 * @param x
		 * @param y
		 * @param z
		 * @return
		 */
		public boolean [][][] GetFogOfWar(Point3D loc){
			boolean [][][] visible = new boolean[map.length][map[0].length][map[0][0].length];
			int count = 0;
			for(int z = 0; z < visible.length; z++){
				for(int x = 0; x < visible[z].length; x++){
					for(int y = 0; y < visible[z][x].length; y++){
						Point3D temp = new Point3D(x,y,z);
						visible[z][x][y] = IsVisible(loc, temp);
						count++;
					}
				}
			}
			System.out.println(count);
			return visible;
		}
		
		/**
		 * if the floors are the same, then checks distance around point;
		 * 
		 * @param loc
		 * @param temp
		 * @return
		 */
		public boolean IsVisible(Point3D loc, Point3D temp){
			boolean isVisible = false;
			if(loc.getZ() == temp.getZ()){
				if(Math.abs(loc.x - temp.x) < width || Math.abs(loc.y - temp.y)< height){
					if(Math.abs(loc.x - temp.x) < width && Math.abs(loc.y - temp.y)< height){
						isVisible = true;
					}
					else if(Distance(temp, loc) < (height + width)/2){
						isVisible = true;
					}
				}
			}
			return isVisible;
		}
		/**
		 * uses pythagorian theorum to find distance
		 * 
		 * @param a
		 * @param b6
		 * @return
		 */
		public double Distance(Point3D a, Point3D b){
			double horDist = a.x - b.x;
			double vertDist = a.y - b.y;
			double totalDist = (vertDist * vertDist) + (horDist * horDist);
			totalDist = Math.sqrt(totalDist);
			return totalDist;
		}
	}

	
}
