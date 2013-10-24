package com.tsp.packets;

import org.json.simple.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/22/13
 * Time: 9:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuitPacket extends Packet
{
	public QuitPacket()
	{
		packetType = PacketType.QUITPACKET;
	}

	@Override
	public String toJSONString()
	{
		JSONObject jb = new JSONObject();
		jb.put("packetID", packetID);
		jb.put("packetType", packetType.toString());
		return jb.toJSONString();
	}
}
