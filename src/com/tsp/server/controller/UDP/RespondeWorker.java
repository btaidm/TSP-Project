package com.tsp.server.controller.UDP;

import com.tsp.packets.Packet;
import com.tsp.server.model.ServerModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.Charset;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/14/13
 * Time: 2:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class RespondeWorker implements Runnable
{

	DatagramSocket socket = null;
	DatagramPacket packet = null;
	ServerModel model = null;

	public RespondeWorker(DatagramSocket socket, DatagramPacket packet, ServerModel model)
	{
		this.socket = socket;
		this.packet = packet;
		this.model = model;
	}

	public void run()
	{
		process();
	}

	private byte[] process()
	{
		byte[] data = packet.getData();
		InputStreamReader input = new InputStreamReader(
				new ByteArrayInputStream(data), Charset.forName("UTF-8"));

		StringBuilder str = new StringBuilder();
		try
		{
			for (int value; (value = input.read()) != -1; )
				str.append((char) value);
		}
		catch (IOException e)
		{
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		Object parsedObject = null;
		try
		{
			parsedObject = JSONValue.parseWithException(str.toString().trim());
			for (Object o : (JSONArray) parsedObject)
			{
				if (o instanceof JSONObject)
				{
					System.out.println(Packet.parseJSONObject((JSONObject) o).toString());
				}
			}
		}
		catch (ParseException e)
		{
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		for (int i = 0; i < data.length; i++)
		{
			data[i] = 0;
		}
		//System.out.println(str.toString());
		return null;
	}
}
