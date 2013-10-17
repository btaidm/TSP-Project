package packets;

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
	protected enum PacketType
	{
		NOTAPACKET,
		MOVEMENTPACKET
	}

	protected static Integer packetCount = 0;
	protected Integer packetID;
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
					if (!(obj.containsKey("playerID") && obj.containsKey("moveX") && obj.containsKey("moveY") &&
					      obj.containsKey("attack") && obj.containsKey("attackX") && obj.containsKey("attackY")))
						throw new IllegalArgumentException("Not a valid Movement packet");

					return new MovementPacket(((Long)obj.get("packetID")).intValue(),
					                          ((Long) obj.get("playerID")).intValue(),
					                          ((Long) obj.get("moveX")).intValue(),
					                          ((Long) obj.get("moveY")).intValue(),
					                          (Boolean) obj.get("attack"),
					                          ((Long) obj.get("attackX")).intValue(),
					                          ((Long) obj.get("attackY")).intValue());
				}
				default:
					throw new IllegalArgumentException("Not a valid packet");
			}
		}
		else
			throw new IllegalArgumentException("JSON is not packet");

	}
}
