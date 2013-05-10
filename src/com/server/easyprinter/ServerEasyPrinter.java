
package com.server.easyprinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerEasyPrinter {
	private static final int PORT = 5001;
	private static int maxConnections = 0;
	
	public static void main(String[] args) {
		int i = 0;
		
		try{
			ServerSocket listener = new ServerSocket(PORT);
			Socket server;
			
			while ( (i++ < maxConnections) || (maxConnections == 0) ){
				doComms connection;
				
				server = listener.accept(); //wait
				doComms c = new doComms(server);
				Thread t = new Thread(c);
				t.start();
			}
			
		}catch (IOException ex){
			System.err.println(ex.getMessage());
		}
	}
}

class doComms implements Runnable{
	private Socket server;
	private String line, input, output;
	
	doComms(Socket server){
		this.server = server;
	}

	@Override
	public void run() {
		input = "";
		output = "";
		
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
			PrintWriter out = new PrintWriter(server.getOutputStream());
			
			while ( (line = in.readLine()) != null && !line.equals("FIN") ){
				input += line;
				out.println("CONNECTED"); //send to the client
			}
			
			server.close();
		}catch(IOException ex){
			System.err.println(ex.getMessage());
		}		
	}
}
