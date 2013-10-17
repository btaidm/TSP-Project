package com.tsp.client;

import com.tsp.client.controller.ConnectionController;
import com.tsp.client.controller.GameController;
import com.tsp.client.model.GameModel;
import com.tsp.client.view.GameView;


public class ClientMain {
	public static void main(String[] arguments) {
		
		GameModel gm = new GameModel();
		GameView gv = new GameView(gm);
		GameController gc = new GameController();
		ConnectionController cc = new ConnectionController();
		
		gv.addListener(gc);
		gv.addListener(cc);
		gv.play();
	}
}
