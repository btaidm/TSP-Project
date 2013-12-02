package com.tsp.viewer.controller;

import com.tsp.viewer.event.GameEvent;
import com.tsp.viewer.event.GameListener;

public class GameController implements GameListener {

	@Override
	public void receiveEvent(GameEvent e) {
		switch (e.type)
		{
			case TURN_END:
				processTick();
			case TURN_MOVE:
				break;
			default:
				break;
			case TURN_ATTACK:
				break;
		}
	}

	private void processTick() {
		//TODO: need to decide what to do with a tick, how long to make it, etc.
	}

}
