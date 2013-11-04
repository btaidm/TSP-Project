package com.tsp.packets;

import org.json.simple.JSONObject;

public class MessagePacket extends Packet {

	String message;
	
	public MessagePacket(String message) {
		super();
		this.message = message;
		this.packetType = PacketType.MESSAGE_PACKET;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	@Override
	public String toJSONString() {
		JSONObject jb = new JSONObject();
		jb.put("packetID",  packetID);
		jb.put("packetType",  this.packetType.toString());
		jb.put("message", message);
		return jb.toString();
	}

}
