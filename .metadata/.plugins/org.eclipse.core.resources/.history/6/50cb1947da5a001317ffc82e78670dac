package com.tsp.packets;

import org.json.simple.JSONObject;

import com.tsp.util.KDTuple;

public class ScorePacket extends Packet {

	private String playerID;
	private KDTuple score;
	public ScorePacket(String id, KDTuple score) {
		this.packetType = PacketType.SCORE_PACKET;
		this.playerID = id;
		this.score = score;
	}
	
	public String getPlayerID() {
		return this.playerID;
	}
	
	public KDTuple getScore() {
		return this.score;
	}
	
	@Override
	public String toJSONString() {
		JSONObject jb = new JSONObject();
		jb.put("packetID",  packetID);
		jb.put("packetType",  this.packetType.toString());
		jb.put("playerID", playerID);
		jb.put("kills", score.kills());
		jb.put("deaths", score.deaths());
		return jb.toString();
	}

}
