package com.tsp.packets;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/22/13
 * Time: 8:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActorUpdate extends Packet
{

	HashMap<String,Object> data;
	int actorID;


	private void setUp()
	{
		packetType = PacketType.UPDATE_PACKET;
		data = new HashMap<String, Object>();
	}

	public ActorUpdate(int actorID)
	{
		this.actorID = actorID;
		setUp();
	}

	public ActorUpdate(Integer _packetID, int actorID)
	{
		super(_packetID);
		this.actorID = actorID;
		setUp();
	}

	public boolean contains(String key)
	{
		return data.containsKey(key);
	}

	public Object getValue(String key)
	{
		if(data.containsKey(key))
			return data.get(key);
		return null;
	}

	public void insertValue(String key, Object value)
	{
		data.put(key, value);
	}

	public void insertJSON(JSONObject jsonObject)
	{
		data.putAll(jsonObject);
	}


	@Override
	public String toJSONString()
	{
		JSONObject jsonObject = new JSONObject(data);
		jsonObject.put("packetID", packetID);
		jsonObject.put("packetType",packetType.toString());
		jsonObject.put("actorID", actorID);

		return jsonObject.toJSONString();
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder("ActorUpdate{data={");
		boolean first = true;
		for(Map.Entry<String,Object> entry : data.entrySet())
		{
			if(!first)
			{
				sb.append(",");
			}
			else
			{
				first = false;
			}
			sb.append(entry);
		}
		sb.append("}}");
		return sb.toString();
	}

	public Integer getActorID()
	{
		return actorID;
	}
}
