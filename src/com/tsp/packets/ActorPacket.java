package com.tsp.packets;

import com.tsp.game.actors.Actor;
import org.json.simple.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/20/13
 * Time: 4:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActorPacket extends Packet
{
	Actor actor;

	public Actor getActor()
	{
		return actor;
	}

	public ActorPacket(int _packetID, Actor character)
	{
		super( _packetID);
		this.actor = character;
		this.packetType = PacketType.ACTORPACKET;
	}

	public ActorPacket(Actor character)
	{
		super();
		this.actor = character;
		this.packetType = PacketType.ACTORPACKET;
	}

	@Override
	public String toJSONString()
	{
		JSONObject jb = new JSONObject();
		jb.put("packetID",packetID);
		jb.put("packetType",packetType.toString());
		jb.put("actor", actor);

		return jb.toString();
	}

	@Override
	public String toString()
	{
		return "ActorPacket{" +
		       "actor=" + actor +
		       '}';
	}
}
