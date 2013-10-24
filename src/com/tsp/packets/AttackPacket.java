package com.tsp.packets;

import org.json.simple.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/22/13
 * Time: 8:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class AttackPacket extends Packet
{

	int attacker;
	int deltaX;
	int deltaY;

	public AttackPacket(Integer _packetID, int attacker, int deltaX, int deltaY)
	{
		super(_packetID);
		this.attacker = attacker;
		this.deltaX = deltaX;
		this.deltaY = deltaY;
		this.packetType = PacketType.ATTACKPACKET;
	}

	public AttackPacket(int attacker, int deltaX, int deltaY)
	{
		this.attacker = attacker;
		this.deltaX = deltaX;
		this.deltaY = deltaY;
		this.packetType = PacketType.ATTACKPACKET;
	}

	@Override
	public String toString()
	{
		return "AttackPacket{" +
		       "attacker=" + attacker +
		       ", deltaX=" + deltaX +
		       ", deltaY=" + deltaY +
		       '}';
	}

	@Override
	public String toJSONString()
	{
		JSONObject jb = new JSONObject();
		jb.put("packetID",packetID);
		jb.put("packetType",packetType.toString());
		jb.put("playerID",attacker);
		jb.put("X", deltaX);
		jb.put("Y", deltaY);

		return jb.toString();
	}
}
