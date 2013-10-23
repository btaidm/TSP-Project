package com.tsp.game.actors;

import com.googlecode.blacken.core.Random;
import com.tsp.game.map.Point3D;
import org.json.simple.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/18/13
 * Time: 10:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class Player extends Actor
{
	int playerCount = 0;
	public Player(int COLS, int ROWS, int LVLS)
	{
		newPosition(COLS,ROWS,LVLS);
		type = ActorType.ACTOR_PLAYER;
		health = 10;
		playerCount++;
		name = "Player " + playerCount;
		symbol = "@";
	}

	public Player(String _name, int COLS, int ROWS, int LVLS)
	{
		Random r = new Random();
		pos = new Point3D(r.nextInt(0, COLS), r.nextInt(0, ROWS), r.nextInt(0,LVLS));
		type = ActorType.ACTOR_PLAYER;
		name = _name;
		health = 10;
		playerCount++;
	}


}
