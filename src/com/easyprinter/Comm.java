
package com.easyprinter;

import android.util.Log;
import java.io.*;
import java.net.*;
import java.util.zip.CRC32;

public class Comm{
	private static final int PORT = 5001;
	private static final int TIMEOUT = 4000;
	
	synchronized
	public String sendMessage(String input, String ip){
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
			
			outputStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
			bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
		
			//Log.d("sendMessage->Sending", "Sending...");
			
			CRC32 crc32 = new CRC32();
			buffer = input.getBytes("utf-8");
			crc32.update(buffer);
			crc32value = crc32.getValue();
				
			outputStream.println(input + ";" + crc32value);
			outputStream.flush();
				
			//Log.d("sendMessage->Preparing", "Preparing...");
			result = bufferedReader.readLine();
			//Log.d("sendMessage->Receiving", result);
				
			outputStream.close();
			bufferedReader.close();
			client.close();
				
			//returns the printer and the ip
			return result + ";" + ip;
			
		} catch(IllegalArgumentException ex){
			Log.e("sendMessage", ex.getMessage());
			return "socket-invalid-timeout";
		} catch(IOException ex){
			Log.e("sendMessage", ex.getMessage());
			return "error-connection";
		}
	}	
	
	synchronized
	public String sendFile(File file, String ip){
		Socket client = new Socket();
		BufferedReader bufferedReader;
		BufferedInputStream bufferedInputStream;
		PrintWriter outputStream;
		String result;
		int n;
		boolean getFile = false;
		
		try{
			InetAddress address = InetAddress.getByName(ip);
			SocketAddress socketAddress = new InetSocketAddress(address, PORT);
			client.connect(socketAddress, TIMEOUT);
			
			outputStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
			bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
			
			bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
			byte[] buffer = new byte[8192];
			
			//send the file name and wait for confirmation
			outputStream.flush();
			String temp = file.getName().toString();
			temp = temp + "#" + temp.length() + "#" + file.length();
			outputStream.println(temp);
			outputStream.flush();
			objectOutputStream.flush();
			result = "";
			result = bufferedReader.readLine();
			
			Log.d("SendFile->fileName", result);
			if ( result.equals("CORRECT") ){	
				//sends the file
				n = 0;
				while ( n < file.length() ){
					n += bufferedInputStream.read(buffer);

					objectOutputStream.write(buffer, 0, n);
					getFile = true;
					objectOutputStream.flush();
				}

				if ( n == -1 && !getFile ){
					throw new Exception("File with no size");
				}
				else{
					objectOutputStream.flush();

					//read the server response
					result = bufferedReader.readLine();
					//Log.d("sendFile->", result);
					
					//calculates the CRC
					result = CheckSum.getMD5CheckSum(file);
					outputStream.println(result);
					outputStream.flush();
					
					//wait for crc correct
					result = bufferedReader.readLine();
					objectOutputStream.close();
					
					if ( result.equals("CORRECT") ){
						//Log.d("sendFile", result);
						bufferedReader.close();
						client.close();
						return "sended";
					}
					else{
						bufferedReader.close();
						client.close();
						return "crc-corrupted";
					}
				}			
			}
			else{
				bufferedReader.close();
				client.close();
				return "no-response";
			}
			
		} catch(IllegalArgumentException ex){
			Log.e("sendMessage", ex.getMessage());
			return "socket-invalid-timeout";
		} catch(IOException ex){
			Log.e("sendMessage", ex.getMessage());
			return "error-connection";
		} catch (Exception ex) {
			Log.e("sendMessage", ex.getMessage());
			return "error-MD5";
		}
	}
}
