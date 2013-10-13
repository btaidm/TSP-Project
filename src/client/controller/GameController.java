package client.controller;

import client.event.GameEvent;
import client.event.GameListener;

public class GameController implements GameListener {

	@Override
	public void receiveEvent(GameEvent e) {
		switch(e.type) {
		case TURN_END:
			processTick();
		default:
			break;
		}
	}

	private void processTick() {
		//TODO: need to decide what to do with a tick, how long to make it, etc.
	}

}
