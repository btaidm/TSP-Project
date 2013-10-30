package com.tsp.game.actors;

import com.tsp.game.map.Dungeon;
import com.tsp.packets.Packet;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/21/13
 * Time: 4:09 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AI extends Actor
{
	public AI()
	{
		super();
		type = ActorType.ACTOR_AI;
	}

	/**
	 * Calculates the AIs turn
	 * @param dungeon the dungeon
	 * @param actors the actors
	 * @return a packet
	 */
	public abstract Packet turn(Dungeon dungeon, ArrayList<Actor> actors);
}
