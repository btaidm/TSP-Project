package com.tsp.client.model;

import java.util.*;

public class Room {
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
