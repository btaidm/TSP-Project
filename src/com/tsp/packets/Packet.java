package com.tsp.packets;

import com.tsp.game.actors.Actor;
import com.tsp.game.map.Point3D;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/16/13
 * Time: 8:48 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Packet implements JSONAware
{
	public enum PacketType
	{
		NOTAPACKET,
		MOVEMENTPACKET,
		ACTORPACKET,
		UPDATEPACKET,
		ATTACKPACKET,
		QUITPACKET
	}

	protected static Integer packetCount = 0;
	protected Integer packetID;

	public PacketType getPacketType()
	{
		return packetType;
	}

	protected PacketType packetType;

	public Packet(Integer _packetID)
	{
		packetID = _packetID;
	}

	public Packet()
	{
		packetID = incrementPacketCount();
	}

	protected Integer incrementPacketCount()
	{
		return packetCount++;
	}

	public Integer getPacketID()
	{
		return packetID;
	}

	public static Packet parseJSONObject(JSONObject obj) throws IllegalArgumentException
	{
		if (obj.containsKey("packetType") && obj.containsKey("packetID"))
		{
			PacketType type;
			try
			{
				type = PacketType.valueOf((String) obj.get("packetType"));
			}
			catch (Exception e)
			{
				type = PacketType.NOTAPACKET;
			}
			switch (type)
			{
				case MOVEMENTPACKET:
				{
					if (!(obj.containsKey("playerID") && obj.containsKey("X") && obj.containsKey("Y") && obj.containsKey("Z")))
						throw new IllegalArgumentException("Not a valid Movement packet");

					return new MovementPacket(((Long) obj.get("packetID")).intValue(),
					                          ((Long) obj.get("playerID")).intValue(),
					                          ((Long) obj.get("X")).intValue(),
					                          ((Long) obj.get("Y")).intValue(),
					                          ((Long) obj.get("Z")).intValue());
				}
				case ACTORPACKET:
				{
					if (!(obj.containsKey("player") && ((JSONObject) obj.get("player")).containsKey("id") &&
					      ((JSONObject) obj.get("player")).containsKey("name") &&
					      ((JSONObject) obj.get("player")).containsKey("X") &&
					      ((JSONObject) obj.get("player")).containsKey("Y") &&
					      ((JSONObject) obj.get("player")).containsKey("Z") &&
					      ((JSONObject) obj.get("player")).containsKey("health") &&
					      ((JSONObject) obj.get("player")).containsKey("type") &&
					      ((JSONObject) obj.get("player")).containsKey("symbol")))
						throw new IllegalArgumentException("Not a valid actor packet");


					int id = ((Long) ((JSONObject) obj.get("player")).get("id")).intValue();
					String name = (String) ((JSONObject) obj.get("player")).get("name");
					int x = ((Long) ((JSONObject) obj.get("player")).get("X")).intValue();
					int y = ((Long) ((JSONObject) obj.get("player")).get("Y")).intValue();
					int z = ((Long) ((JSONObject) obj.get("player")).get("Z")).intValue();
					int health = ((Long) ((JSONObject) obj.get("player")).get("health")).intValue();
					Actor.ActorType actorType = Actor.ActorType
							.valueOf((String) ((JSONObject) obj.get("player")).get("type"));
					String symbol = (String) ((JSONObject) obj.get("player")).get("symbol");

					Actor actor = new Actor(id, health, new Point3D(x, y, z), name, actorType, symbol);

					return new ActorPacket(((Long) obj.get("packetID")).intValue(), actor);
				}
				default:
					throw new IllegalArgumentException("Not a valid packet");
			}
		}
		else
			throw new IllegalArgumentException("JSON is not packet");

	}
}
