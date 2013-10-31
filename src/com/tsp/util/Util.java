package com.tsp.util;

import com.tsp.packets.Packet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @author Tim
 */
public class Util
{
	public static String BytesToString(byte[] bytes) throws IOException
	{
		return BytesToString(bytes, 0, bytes.length);
	}

	public static String BytesToString(byte[] bytes, int offset, int length) throws IOException
	{
		InputStreamReader input = new InputStreamReader(
				new ByteArrayInputStream(bytes, offset, length), Charset.forName("UTF-8"));

		StringBuilder str = new StringBuilder();

		for (int value; (value = input.read()) != -1; )
			str.append((char) value);


		return str.toString();
	}
}
