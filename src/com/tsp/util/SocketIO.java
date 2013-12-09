package com.tsp.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author Tim
 */
public class SocketIO {
    SocketChannel socketChannel = null;

    public SocketIO(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void WriteShort(short value) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.putShort(value);
        buf.flip();
        while (buf.hasRemaining())
            socketChannel.write(buf);
    }

    public void WriteInt(int value) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(value);
        buf.flip();
        while (buf.hasRemaining())
            socketChannel.write(buf);
    }

    public void WriteLong(long value) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putLong(value);
        buf.flip();
        while (buf.hasRemaining())
            socketChannel.write(buf);
    }

    public void WriteChar(char value) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(1);
        buf.putChar(value);
        buf.flip();
        while (buf.hasRemaining())
            socketChannel.write(buf);
    }

    public short ReadShort() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(2);
        int read = socketChannel.read(buf);
        if (read != 2)
            throw new IOException("Short not read");
        buf.flip();
        return buf.getShort();
    }

    public int ReadInt() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(4);
        int read = socketChannel.read(buf);
        if (read != 4)
            throw new IOException("Short not read");
        buf.flip();
        return buf.getInt();
    }

    public long ReadLong() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(8);
        int read = socketChannel.read(buf);
        if (read != 8)
            throw new IOException("Short not read");
        buf.flip();
        return buf.getLong();
    }

    public char ReadChar() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(1);
        int read = socketChannel.read(buf);
        if (read != 1)
            throw new IOException("Short not read");
        buf.flip();
        return buf.getChar();
    }

    public void WriteString(String string) throws IOException {
        byte[] str = string.getBytes();
        int size = str.length;
        ByteBuffer buf = ByteBuffer.allocate(size + 4);
        buf.putInt(size).put(str);
        buf.flip();
        while (buf.hasRemaining())
            socketChannel.write(buf);
    }

    public String ReadString() throws IOException{
        int size = this.ReadInt();
        ByteBuffer buf = ByteBuffer.allocate(size);
        int read = 0;
        while (read < size) {
            int r = socketChannel.read(buf);
            if (r < 0)
                throw new IOException("Short not read");
            read += r;
        }
        buf.flip();
        CharBuffer charBuffer = Charset.defaultCharset().decode(buf);

        return charBuffer.toString();
    }

}
