package com.tsp.game.actors.ai;

import com.tsp.game.actors.Actor;
import com.tsp.game.map.Dungeon;
import com.tsp.game.map.Point3D;
import com.tsp.packets.Packet;

import java.util.ArrayList;

/**
 * @author Tim
 */
public class SimpleAI extends AI
{

	public SimpleAI()
	{
		super();
		this.symbol = "A";
		this.pos = new Point3D(0, 0, 0);
		this.name = "AI";
		this.color = 231;
		this.health = 10;
	}

	@Override
	public Packet turn(Dungeon dungeon, ArrayList<Actor> actors)
	{

		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	private 
}
