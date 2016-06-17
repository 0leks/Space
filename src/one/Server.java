package one;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
	public ServerSocket server;
	public World world;
	Socket sock;
	Thread thread;
	public Server() {
		thread = new Thread(this);
		world = new World(this);
	}
	public static void main(String[] args) {
		Server s = new Server();
		s.start();
	}
	public void start() {
		thread.start();
	}
	@Override
	public void run() {
		try {
			System.out.println("Creating Server");
			server = new ServerSocket(34555);
			while(true) {
				System.out.println("waiting for connection");
				sock = server.accept();
				System.out.println("making reader");
				DataInputStream hostin = new DataInputStream(sock.getInputStream());
				System.out.println("Creating outputstream");
				DataOutputStream hostout = new DataOutputStream(sock.getOutputStream());
				ObjectOutputStream output = new ObjectOutputStream(sock.getOutputStream());
				if(world.canJoin()) {
					world.addConnection(new Connection(world, hostin, hostout, output));
				} else {
//					hostout.writeInt(10012);
//					hostout.writeInt(1);
          output.close();
					hostin.close();
					hostout.close();
					
				}
			}
		} catch (IOException e) {
			System.out.println("Error Creating Server");
			e.printStackTrace();
		}
	}
}
