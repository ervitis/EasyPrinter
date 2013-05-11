
package com.easyprinter;

import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class Comm{
	private static final int PORT = 5001;
	private static final int TIMEOUT = 4000;
	
	public String sendMessage(String input, String ip){
		try{
			char[] buffer = new char[1024];
			String result = "";
			Socket client = new Socket();
			InetAddress address = InetAddress.getByName(ip);
			PrintWriter outputStream;
			BufferedReader bufferedReader;
			
			SocketAddress socketAddress = new InetSocketAddress(address, PORT);
			
			client.connect(socketAddress, TIMEOUT);
			
			outputStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
			bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			try{
				Log.d("sendMessage->Sending", "Sending...");
				outputStream.println(input);
				outputStream.flush();
				Log.d("sendMessage->Preparing", "Preparing...");
				result = bufferedReader.readLine();
				Log.d("sendMessage->Receiving", result);
			}catch(Exception ex){
				Log.e("sendMessage", ex.getMessage());
			}
			
			outputStream.flush();
			outputStream.close();
			bufferedReader.close();
			client.close();
			
			return result;
		}catch(Exception ex){
			Log.e("Comm->sendMessage", ex.getMessage());
			return "no-data";
		}
	}	
}
