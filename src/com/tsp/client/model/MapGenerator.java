package com.tsp.client.model;

import com.googlecode.blacken.core.Random;

public class MapGenerator {
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
				int x = r.nextInt(0, map[i].length);
				int y = r.nextInt(0, map[i][0].length);

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
