package com.tsp.client.model;

public class MapGenerator {
	private FloorGenerator floorGenerator;

	public MapGenerator(String wall, String floor){
		floorGenerator = new FloorGenerator(wall, floor);
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
}
