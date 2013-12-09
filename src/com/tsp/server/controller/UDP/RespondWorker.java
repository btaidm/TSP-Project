package com.tsp.server.controller.UDP;

import com.tsp.packets.Packet;
import com.tsp.server.model.ServerModel;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/14/13
 * Time: 2:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class RespondWorker implements Runnable
{

	DatagramChannel socket = null;
	//DatagramPacket packet = null;
	InetSocketAddress address = null;
	ByteBuffer data = null;
	ServerModel model = null;

	private ByteBuffer clone(ByteBuffer org)
	{
		ByteBuffer clone = ByteBuffer.allocate(org.capacity());
		org.rewind();
		clone.put(org);
		org.rewind();
		clone.flip();
		return clone;
	}

	public RespondWorker(DatagramChannel socket, ByteBuffer packet, InetSocketAddress address, ServerModel model)
	{
		this.socket = socket;
		this.data = clone(packet);
		this.model = model;
		this.address = address;
	}

	public void run()
	{
		if (data.capacity() > 1)
			process();
	}

	private byte[] process()
	{

		CharBuffer charBuffer = Charset.defaultCharset().decode(data);


		Object parsedObject = null;
		try
		{
			parsedObject = JSONValue.parseWithException(charBuffer.toString().trim());
			if (parsedObject instanceof JSONObject)
			{
				Packet packet1 = Packet.parseJSONObject((JSONObject) parsedObject);
				model.processPacket(packet1);
			}
		}
		catch (ParseException e)
		{
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}


		//System.out.println(str.toString());
		return null;
	}
}
