package com.tsp.viewer.event;

import java.util.HashMap;


public class GameEvent {

	public final EventType type;
	public final HashMap<String, Object> payload;
	
	public GameEvent(EventType et, HashMap<String, Object> payload) {
		this.type = et;
		this.payload = payload;
	}
}
