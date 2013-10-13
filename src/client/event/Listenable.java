package client.event;

import java.util.Properties;



public interface Listenable {
	public void addListener(GameListener gl);
	public void removeListener(GameListener gl);
	public void fireEvent(EventType e, Properties payload);
}
