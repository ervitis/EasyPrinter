
package com.server.easyprinter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.CRC32;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class ServerEasyPrinter {
	private static final int PORT = 5001;
	private static int maxConnections = 0;
	
	public static void main(String[] args) {
		int i = 0;
		
		try{
			ServerSocket listener = new ServerSocket(PORT);
			Socket server;
			
			while ( (i++ < maxConnections) || (maxConnections == 0) ){				
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
		String[] tempcrc = null;
		CRC32 crc32 = new CRC32();
		long crc32value;
		long crc32sendedvalue;
		byte[] crc_b = new byte[1024];
		boolean isFile = false;
		String fileName = "";
		
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(server.getOutputStream())));
			
			BufferedInputStream bis = new BufferedInputStream(server.getInputStream());
			
			while ( (line = in.readLine()) != null && !line.equals("FIN")){
				if ( line.contains(";") ){
					tempcrc = line.split(";");
				
					if ( tempcrc[0].equals("SEARCHING") ){
						input += line;

						crc_b = tempcrc[0].getBytes("utf-8");
						crc32.update(crc_b);
						crc32value = crc32.getValue();

						crc32sendedvalue = Long.parseLong(tempcrc[1]);

						if ( crc32sendedvalue == crc32value ){
							//out.println("CONNECTED"); //send to the client
							//instead of this, send the printer
							PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
							String printerName = printService.getName();
							out.println(printerName);

							System.out.println("CRC fine");
						}
						else{
							System.err.println("CRC corrupted");
						}

						System.out.println(input);
						out.flush();
					}
				}
				else{
					//file
					isFile = true;
					break;
				}
			}
			
			if ( isFile ){
				byte[] buffer = new byte[8192];
				int n = 0;
				
				//get the file name in line
				String[] temp;
				temp = line.split("#");
				int nameSize = Integer.parseInt(temp[1]);
				int fileSize = Integer.parseInt(temp[2]);
				fileName = temp[0].substring(temp[0].length() - nameSize);
				if ( fileName.endsWith(".txt") || fileName.endsWith(".pdf") ){
					//sends an answer
					out.println("CORRECT");
					out.flush();
					
					System.out.println("File name: " + fileName);
					
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName));

					while ( n < fileSize ){
						n += bis.read(buffer);
					}
					
					//conversion
					String t = new String(buffer, "UTF-8");
					int c = Math.abs(n - fileSize);
					System.out.println("Filesize: " + fileSize + ", n: " + n + ", c: " + c);
					t = t.substring(c);
					buffer = t.getBytes("utf-8");
						
					bos.write(buffer, 0, n);
					bos.flush();
					
					out.println("CORRECT");
					out.flush();
					
					//here is where i should check the crc code

					File r = new File(fileName);
					System.out.println("Received " + r.length());

					bos.flush();
					bis.close();
					bos.close();
				}
				else{
					System.out.println("Archivo no soportado");
				}
			}
			
			System.out.println("Cerrando conexion");
			
			out.flush();
			out.close();
			in.close();
			server.close();
		}catch(IOException ex){
			System.err.println(ex.getMessage());
		}		
	}
}
