package com.tsp.server.controller.TCP;

import com.tsp.server.model.ServerModel;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/19/13
 * Time: 1:37 PM
 * To change this template use File | Settings | File Templates.
 */
class clientThread extends Thread
{

	private String clientName = null;
	private DataInputStream is = null;
	private DataOutputStream os = null;
	private Socket clientSocket = null;
	private final clientThread[] threads;
	private int maxClientsCount;
	private ServerModel serverModel = null;

	private String BytesToString(byte[] bytes) throws IOException
	{
		InputStreamReader input = new InputStreamReader(
				new ByteArrayInputStream(bytes), Charset.forName("UTF-8"));

		StringBuilder str = new StringBuilder();

		for (int value; (value = input.read()) != -1; )
			str.append((char) value);


		return str.toString();
	}


	public clientThread(Socket clientSocket, clientThread[] threads, ServerModel sm)
	{
		this.clientSocket = clientSocket;
		this.threads = threads;
		maxClientsCount = threads.length;
		serverModel = sm;
	}

	public void run()
	{
		int maxClientsCount = this.maxClientsCount;
		clientThread[] threads = this.threads;

		try
		{
		   /*
		    * Create input and output streams for this client.
	        */
			is = new DataInputStream(clientSocket.getInputStream());
			os = new DataOutputStream(clientSocket.getOutputStream());

			int count = is.available();

			byte[] name = new byte[count];

			is.read(name);

			String PlayName = BytesToString(name);
			synchronized (this)
			{
				for (int i = 0; i < maxClientsCount; i++)
				{
					if (threads[i] != null && threads[i] == this)
					{
						clientName = "@" + PlayName;
						break;
					}
				}

				Integer playerID = new Integer(serverModel.addPlayer(PlayName));
				os.writeInt(playerID);
			}

		    /* Start the conversation. */
			while (true)
			{
				break;
			}

			quit();
		}
		catch (IOException e)
		{
		}
	}

	public void quit() throws IOException
	{
		/*
       * Clean up. Set the current thread variable to null so that a new client
       * could be accepted by the server.
       */
		synchronized (this)
		{
			for (int i = 0; i < maxClientsCount; i++)
			{
				if (threads[i] == this)
				{
					threads[i] = null;
				}
			}
		}
      /*
       * Close the output stream, close the input stream, close the socket.
       */
		is.close();
		os.close();
		clientSocket.close();
	}

	private void sendBytes(byte[] myByteArray) throws IOException
	{
		sendBytes(myByteArray, 0, myByteArray.length);
	}

	private void sendBytes(byte[] myByteArray, int start, int length) throws IOException
	{
		if(length < 0)
			throw new  IllegalArgumentException("Negative length not allowed");
		if(start < 0 || start >= myByteArray.length)
			throw new IndexOutOfBoundsException("Out of Bounds: " + start);

		os.writeInt(length);
		if(length > 0)
		{
			os.write(myByteArray,start,length);
		}
	}
}

