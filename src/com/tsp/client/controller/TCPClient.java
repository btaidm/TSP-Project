package com.tsp.client.controller;

import com.tsp.client.model.GameModel;
import com.tsp.game.actors.Actor;
import com.tsp.packets.ActorPacket;
import com.tsp.packets.Packet;
import com.tsp.packets.QuitPacket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/20/13
 * Time: 6:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class TCPClient extends Thread
{
	GameModel model;
	InetAddress addr;
	int port = 12000;
	Socket clientSocket;
	DataInputStream is;
	DataOutputStream os;

	public TCPClient(GameModel model) throws UnknownHostException
	{
		this.model = model;
		addr = InetAddress.getLocalHost();
	}


	public TCPClient(GameModel model, InetAddress address, int port)
	{
		this.model = model;
		addr = address;
		this.port = port;
	}

	public TCPClient(GameModel model, String host, int port) throws UnknownHostException
	{
		this.model = model;
		addr = InetAddress.getByName(host);
		this.port = port;
	}

	public void connect() throws IOException
	{
		clientSocket = new Socket(addr, port);
		is = new DataInputStream(clientSocket.getInputStream());
		os = new DataOutputStream(clientSocket.getOutputStream());
	}

	public void sendName(String name) throws IOException
	{
		os.writeUTF(name);
	}

	public int getID() throws IOException
	{
		return is.readInt();
	}

	public String[][][] getDungeon() throws IOException
	{
		int Columns = is.readInt();
		int Rows = is.readInt();
		int Floors = is.readInt();
		String[][][] dungeon = new String[Columns][Rows][Floors];
		for (int z = 0; z < Floors; z++)
			for (int x = 0; x < Columns; x++)
				for (int y = 0; y < Rows; y++)
					dungeon[x][y][z] = is.readUTF();
		return dungeon;
	}

	public

	public void sendQuit() throws IOException
	{
		os.writeUTF(new QuitPacket().toJSONString());
		os.close();
		is.close();
		clientSocket.close();
	}


}
