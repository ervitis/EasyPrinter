
package com.easyprinter;

import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.zip.CRC32;

public class Comm{
	private static final int PORT = 5001;
	private static final int TIMEOUT = 4000;
	
	synchronized
	public String sendMessage(String input, String ip){
		try{
			String result = "";
			Socket client = new Socket();
			PrintWriter outputStream;
			BufferedReader bufferedReader;
			byte[] buffer = new byte[1024];
			long crc32value;
			
			try{
				InetAddress address = InetAddress.getByName(ip);
				SocketAddress socketAddress = new InetSocketAddress(address, PORT);
				client.connect(socketAddress, TIMEOUT);
			} catch(Exception ex){
				Log.e("sendMessage", ex.getMessage());
				return "no-socket";
			}
			
			try{
				outputStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
				bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
		
				Log.d("sendMessage->Sending", "Sending...");
				
				CRC32 crc32 = new CRC32();
				buffer = input.getBytes("utf-8");
				crc32.update(buffer);
				crc32value = crc32.getValue();
				
				outputStream.println(input + ";" + crc32value);
				outputStream.flush();
				
				Log.d("sendMessage->Preparing", "Preparing...");
				result = bufferedReader.readLine();
				Log.d("sendMessage->Receiving", result);
				
				outputStream.close();
				bufferedReader.close();
				client.close();
				
				return result;
			}catch(Exception ex){
				Log.e("sendMessage", ex.getMessage());
				return "no-data";
			}
		}catch(Exception ex){
			Log.e("Comm->sendMessage", ex.getMessage());
			return "no-data";
		}
	}	
	
	synchronized
	public int sendFile(File file, String ip){
		Socket client = new Socket();
		BufferedReader bufferedReader;
		BufferedInputStream bufferedInputStream;
		String result;
		int n;
		
		try{
			InetAddress address = InetAddress.getByName(ip);
			SocketAddress socketAddress = new InetSocketAddress(address, PORT);
			client.connect(socketAddress, TIMEOUT);
		}catch(Exception ex){
			Log.e("sendFile", ex.getMessage());
			return 1;
		}
		
		try{
			bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
			
			bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
			byte[] buffer = new byte[8192];
			
			do{
				objectOutputStream.writeObject(file);
				//objectOutputStream.flush();
			} while ( (n = bufferedInputStream.read(buffer)) != -1 );
			
			objectOutputStream.flush();
			
			//read the server response it will be bytes
			result = bufferedReader.readLine();
			Log.d("sendFile", result);
			
			objectOutputStream.close();
			bufferedReader.close();
			client.close();
			
			return 0;
			
		}catch(Exception ex){
			Log.e("sendFile", ex.getMessage());
			return 2;
		}
	}
}
