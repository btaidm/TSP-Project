package server;

import java.net.DatagramPacket;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 10/14/13
 * Time: 2:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class RespondeWorker implements Runnable
{

	Socket socket = null;
	DatagramPacket packet = null;

	public RespondeWorker(Socket socket, DatagramPacket packet) {
		this.socket = socket;
		this.packet = packet;
	}

	public void run() {
		packet.getData()
	}
}
