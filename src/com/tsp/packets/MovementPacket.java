package com.tsp.packets;

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

	public Integer getM_newX()
	{
		return m_newX;
	}

	public Integer getM_newY()
	{
		return m_newY;
	}

	public Integer getM_newZ()
	{
		return m_newZ;
	}

	Integer m_playerID;
	Integer m_newX;
	Integer m_newY;
	Integer m_newZ;

	public MovementPacket(Integer _packetID, Integer m_playerID, Integer m_newX, Integer m_newY, Integer m_moveZ )
	{
		super(_packetID);
		this.m_playerID = m_playerID;
		this.m_newX = m_newX;
		this.m_newY = m_newY;
		this.m_newZ = m_moveZ;
		this.packetType = PacketType.MOVEMENTPACKET;
	}

	public MovementPacket( Integer m_playerID, Integer m_newX, Integer m_newY, Integer m_newZ)
	{
		super();
		this.m_playerID = m_playerID;
		this.m_newX = m_newX;
		this.m_newY = m_newY;
		this.m_newZ = m_newZ;
		this.packetType = PacketType.MOVEMENTPACKET;
	}

	@Override
	public String toJSONString()
	{
		JSONObject jb = new JSONObject();
		jb.put("packetID",packetID);
		jb.put("packetType",packetType.toString());
		jb.put("playerID",m_playerID);
		jb.put("X", m_newX);
		jb.put("Y", m_newY);
		jb.put("Z", m_newZ);

		return jb.toString();
	}

	@Override
	public String toString()
	{
		return "MovementPacket{" +
		       "m_playerID=" + m_playerID +
		       ", m_newX=" + m_newX +
		       ", m_newY=" + m_newY +
		       ", m_newZ=" + m_newZ +
		       '}';
	}
}
