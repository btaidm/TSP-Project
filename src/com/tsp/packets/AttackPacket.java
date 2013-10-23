package com.tsp.packets;

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

	@Override
	public String toJSONString()
	{
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
