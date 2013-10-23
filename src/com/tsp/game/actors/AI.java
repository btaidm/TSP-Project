package com.tsp.game.actors;

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
	public abstract Packet turn(String[][][] dungeon, ArrayList<Actor> actors);
}
