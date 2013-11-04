package com.tsp.packets;

import org.json.simple.JSONObject;

public class MessagePacket extends Packet {

	String message;
	
	public MessagePacket(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	@Override
	public String toJSONString() {
		JSONObject jb = new JSONObject();
		jb.put("message", message);
		return jb.toString();
	}

}
