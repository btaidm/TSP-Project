package com.tsp.game.characters;

import com.googlecode.blacken.core.Random;
import com.tsp.game.map.Point3D;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/18/13
 * Time: 10:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class Player extends com.tsp.game.characters.Character
{
	protected static int count = 0;

	public Player(int COLS, int ROWS, int LVLS)
	{
		newPosition(COLS,ROWS,LVLS);
		health = 10;
		id = count;
		count++;
		name = "Player " + count;
	}

	public Player(String _name, int COLS, int ROWS, int LVLS)
	{
		Random r = new Random();
		pos = new Point3D(r.nextInt(0, COLS), r.nextInt(0, ROWS), r.nextInt(0,LVLS));

		name = _name;
		health = 10;
		id = count;
		count++;
	}

}
