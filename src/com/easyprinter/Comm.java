
package com.easyprinter;

import android.util.Log;
import java.io.*;
import java.net.*;

public class Comm{
	private static final int PORT = 5001;
	private static final int TIMEOUT = 4000;
	
	synchronized
	public String sendMessage(String input, String ip){
		String result = "";
		Socket client = new Socket();
		PrintWriter outputStream;
		BufferedReader bufferedReader;
			
		try{
			InetAddress address = InetAddress.getByName(ip);
			SocketAddress socketAddress = new InetSocketAddress(address, PORT);
			client.connect(socketAddress, TIMEOUT);
			
			outputStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
			bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
		
			Log.d("sendMessage->Sending", "Sending...");
				
			outputStream.println(input + ";");
			outputStream.flush();
				
			Log.d("sendMessage->Preparing", "Preparing...");
			result = bufferedReader.readLine();
			Log.d("sendMessage->Response", result);
			
			if ( result.equals("CONNECTED") ){
				outputStream.println("GET-PRINT");
				outputStream.flush();

				result = bufferedReader.readLine();
				Log.d("sendMessage->Receiving", result);
				
				return result + ";" + ip;
			}
			else{
				outputStream.flush();
				outputStream.close();
				bufferedReader.close();
				client.close();
				return "closed";
			}			
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
		String result;
		int n;
		boolean getFile = false;
		
		try{
			Socket client = new Socket();
			InetAddress address = InetAddress.getByName(ip);
			SocketAddress socketAddress = new InetSocketAddress(address, PORT);
			client.connect(socketAddress, TIMEOUT);
			
			PrintWriter outputStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
						
			//send name, length name and length file
			String temp = file.getName().toString();
			temp = temp + "#" + temp.length() + "#" + file.length();
			outputStream.println(temp);
			outputStream.flush();
			objectOutputStream.flush();
			result = "";
			result = bufferedReader.readLine();
			
			if ( file.getName().endsWith(".txt") ){
				byte[] buffer = new byte[8192];

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
						
						bufferedReader.close();
						client.close();
						return "sended";
					}			
				}
			}
			else if ( file.getName().endsWith(".pdf") ){
				byte[] buffer = new byte[(int)file.length()];
				
				Log.d("SendFile->fileName", result);
				if ( result.equals("CORRECT") ){
					//read the file and send it, a solution
					n = 0;
					while ( n < file.length() ){
						n += bufferedInputStream.read(buffer);
					}
					
					Log.d("SendFile pdf->", "Leidos " + n);

					//a different outputstream
					//read it
					if ( n > 0 ){
						OutputStream os = client.getOutputStream();
						os.write(buffer);
						os.flush();
					
						//wait for server
						result = bufferedReader.readLine();
						
						bufferedReader.close();
						client.close();
						return "sended";
					}
					else{
						bufferedReader.close();
						client.close();
						return "error-minus-one";
					}
				}
				else{
					bufferedReader.close();
					client.close();
					return "no-correct";
				}
			}
			else{
				client.close();
				return "no-file";
			}			
		}catch(IllegalArgumentException ex){
			Log.e("sendMessage", ex.getMessage());
			return "socket-invalid-timeout";
		}catch(IOException ex){
			Log.e("sendMessage", ex.getMessage());
			return "error-connection";
		}catch (Exception ex) {
			Log.e("sendMessage", ex.getMessage());
			return "error";
		}
		return "error";
	}
}
