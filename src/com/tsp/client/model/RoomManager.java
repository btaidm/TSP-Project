package com.tsp.client.model;

import java.util.ArrayList;
import java.util.Arrays;

public class RoomManager {
	
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
