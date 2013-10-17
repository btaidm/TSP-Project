package com.tsp.client.model;

import java.util.*;

public class FloorGenerator {
	String[][][] rooms;
	double oneToZero = 0;
	boolean ascending = true;
	boolean decending = false;
	String wall;
	String floor;
	
	public FloorGenerator(String wall, String floor){
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
