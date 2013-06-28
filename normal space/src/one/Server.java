package one;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{
	public ServerSocket server;
	public World world;
	Socket sock;
//	PrintWriter System.out.;
//	BufferedReader hostin;
//	DataOutputStream hostout;
	Thread thread;
	public Server(World w) {
		world = w;
		thread = new Thread(this);
	}
	public void start() {
		thread.start();
	}
	@Override
	public void run() {
		try {
//			System.out. = new PrintWriter(new BufferedWriter(new FileWriter("System.out..txt")));
			System.out.println("Creating Server");
			server = new ServerSocket(34555);
			for(int a=0; true; a++) {
				System.out.println("Accepting socket");
				sock = server.accept();
				System.out.println("making reader");
				DataInputStream hostin = new DataInputStream(sock.getInputStream());
				System.out.println("Creating outputstream");
				DataOutputStream hostout = new DataOutputStream(sock.getOutputStream());
				world.addConnection(new Connection(world, hostin, hostout));
			}
		} catch (IOException e) {
			System.out.println("Error Creating Server");
			e.printStackTrace();
		}
	}
}
