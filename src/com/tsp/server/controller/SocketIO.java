package com.tsp.server.controller;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Tim
 */
public class SocketIO
{
	SocketChannel socketChannel = null;

	public SocketIO(SocketChannel socketChannel)
	{
		this.socketChannel = socketChannel;
	}

	public void WriteShort(short value) throws IOException
	{
		ByteBuffer buf = ByteBuffer.allocate(2);
		buf.putShort(value);
		socketChannel.write(buf);
	}

	public void WriteInt(int value) throws IOException
	{
		ByteBuffer buf = ByteBuffer.allocate(4);
		buf.putInt(value);
		socketChannel.write(buf);
	}

	public void WriteLong(long value) throws IOException
	{
		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.putLong(value);
		socketChannel.write(buf);
	}

	public void WriteChar(char value) throws IOException
	{
		ByteBuffer buf = ByteBuffer.allocate(1);
		buf.putChar(value);
		socketChannel.write(buf);
	}

	public short ReadShort() throws IOException
	{
		ByteBuffer buf = ByteBuffer.allocate(2);
		int read = socketChannel.read(buf);
		if(read != 2)
			throw new IOException("Short not read");
		return buf.getShort();
	}

	public int ReadInt() throws IOException
	{
		ByteBuffer buf = ByteBuffer.allocate(4);
		int read = socketChannel.read(buf);
		if(read != 4)
			throw new IOException("Short not read");
		return buf.getInt();
	}

	public long ReadLong() throws IOException
	{
		ByteBuffer buf = ByteBuffer.allocate(8);
		int read = socketChannel.read(buf);
		if(read != 8)
			throw new IOException("Short not read");
		return buf.getLong();
	}

	public char ReadChar() throws IOException
	{
		ByteBuffer buf = ByteBuffer.allocate(1);
		int read = socketChannel.read(buf);
		if(read != 1)
			throw new IOException("Short not read");
		return buf.getChar();
	}

}
