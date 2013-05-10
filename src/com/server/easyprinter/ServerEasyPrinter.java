
package com.server.easyprinter;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerEasyPrinter {
	private static final int PORT = 5001;
	private static int maxConnections = 0;
	
	public static void main(String[] args) {
		int i = 0;
		
		try{
			ServerSocket listener = new ServerSocket();
			
		}catch (IOException ex){
			System.err.println(ex.getMessage());
		}
	}
}
