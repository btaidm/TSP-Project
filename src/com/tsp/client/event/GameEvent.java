package client.event;

import java.util.Properties;


public class GameEvent {

	public final EventType type;
	public final Properties payload;
	
	public GameEvent(EventType et, Properties payload) {
		this.type = et;
		this.payload = payload;
	}
}
