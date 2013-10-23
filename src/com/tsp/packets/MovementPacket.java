package com.tsp.packets;

import com.tsp.packets.Packet;
import org.json.simple.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/16/13
 * Time: 9:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class MovementPacket extends Packet
{
	public Integer getM_playerID()
	{
		return m_playerID;
	}

	public Integer getM_moveX()
	{
		return m_moveX;
	}

	public Integer getM_moveY()
	{
		return m_moveY;
	}

	public Integer getM_moveZ()
	{
		return m_moveZ;
	}

	Integer m_playerID;
	Integer m_moveX;
	Integer m_moveY;
	Integer m_moveZ;

	public MovementPacket(Integer _packetID, Integer m_playerID, Integer m_moveX, Integer m_moveY, Integer m_moveZ )
	{
		super(_packetID);
		this.m_playerID = m_playerID;
		this.m_moveX = m_moveX;
		this.m_moveY = m_moveY;
		this.m_moveZ = m_moveZ;
		this.packetType = PacketType.MOVEMENTPACKET;
	}

	public MovementPacket( Integer m_playerID, Integer m_moveX, Integer m_moveY, Integer m_moveZ)
	{
		super();
		this.m_playerID = m_playerID;
		this.m_moveX = m_moveX;
		this.m_moveY = m_moveY;
		this.m_moveZ = m_moveZ;
		this.packetType = PacketType.MOVEMENTPACKET;
	}

	@Override
	public String toJSONString()
	{
		JSONObject jb = new JSONObject();
		jb.put("packetID",packetID);
		jb.put("packetType",packetType.toString());
		jb.put("playerID",m_playerID);
		jb.put("moveX",m_moveX);
		jb.put("moveY",m_moveY);
		jb.put("moveZ",m_moveZ);

		return jb.toString();
	}

	@Override
	public String toString()
	{
		return "MovementPacket{" +
		       "m_playerID=" + m_playerID +
		       ", m_moveX=" + m_moveX +
		       ", m_moveY=" + m_moveY +
		       ", m_moveZ=" + m_moveZ +
		       '}';
	}
}
