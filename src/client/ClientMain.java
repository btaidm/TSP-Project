package client;

import client.controller.GameController;
import client.model.GameModel;
import client.view.GameView;


public class ClientMain {
	public static void main(String[] arguments) {
		GameModel gm = new GameModel();
		GameView gv = new GameView(gm);
		GameController gc = new GameController();
		
		gv.addListener(gc);
		gv.play();
	}
}
