package com.tsp.client.event;

import java.util.HashMap;
import java.util.Properties;


public class GameEvent {

	public final EventType type;
	public final HashMap<String, Object> payload;
	
	public GameEvent(EventType et, HashMap<String, Object> payload) {
		this.type = et;
		this.payload = payload;
	}
}
