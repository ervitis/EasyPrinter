
package com.server.easyprinter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.AttributedString;
import java.util.zip.CRC32;
import javax.print.*;
import javax.swing.*;

/**
 * Main class
 * @author victor
 */
public class ServerEasyPrinter {
	private static final int PORT = 5001;
	private static final int maxConnections = 0;
		
	/**
	 * Main for the program
	 * @param args	parameter
	 */
	public static void main(String[] args) {
		buildGUI();
	}
	
	/**
	 * Build the Graphic User Interface
	 */
	public static void buildGUI(){
		JFrame jFrame = new JFrame("EasyPrinter service");
		
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BorderLayout borderLayout = new BorderLayout();
		JPanel jPanelBackground = new JPanel(borderLayout);
		jPanelBackground.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JButton start = new JButton("Arranca");
		start.addActionListener(new StartService());
		
		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(start);
		
		JLabel impresora = new JLabel("Impresora");
		Global.txtImpresora = new JTextField(20);
		Global.txtImpresora.setEditable(false);
		JLabel documento = new JLabel("Documento");
		Global.txtDocumento = new JTextField(20);
		Global.txtDocumento.setEditable(false);
		
		Box box2 = new Box(BoxLayout.Y_AXIS);
		box2.add(impresora);
		box2.add(Global.txtImpresora);
		box2.add(documento);
		box2.add(Global.txtDocumento);
		
		jPanelBackground.add(BorderLayout.EAST, box);
		jPanelBackground.add(BorderLayout.WEST, box2);
		
		jFrame.getContentPane().add(jPanelBackground);
		jFrame.setBounds(50, 50, 120, 120);
		jFrame.pack();
		jFrame.setResizable(false);
		jFrame.setVisible(true);
	}

	/**
	 * Class for start the daemon implementing ActionListener interface
	 */
	private static class StartService implements ActionListener {

		/**
		 * Action for clicking the button
		 * @param e		mouse event
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(null, "Servicio arrancado");
			
			Daemon daemon = new Daemon(maxConnections, PORT);
			Thread thread = new Thread(daemon);
			thread.start();
		}
	}
}

/**
 * Daemon class for receiving the socket petitions extending Thread super and implementing Runnable interface
 * @author victor
 */
class Daemon extends Thread implements Runnable{
	private int max, port;

	/**
	 * Constructor for the daemon
	 * @param max			max connections, default 0
	 * @param port		port for listening the daemon
	 */
	Daemon(int max, int port){
		this.max = max;
		this.port = port;
	}

	/**
	 * Thread run method
	 * @throws IOException for socket error
	 */
	@Override
	synchronized
	public void run(){
		int i = 0;

		try{
			ServerSocket listener = new ServerSocket(port);
			Socket server;

			while ( (i++ < max) || (max == 0) ){				
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

/**
 * Class for communications extending the Thread super class and implementing the Runnable interface
 * @author victor
 */
class doComms extends Thread implements Runnable{
	private Socket server;
	private String line;

	/**
	 * Constructor for the class
	 * @param server	socket
	 */
	doComms(Socket server){
		this.server = server;
	}

	/**
	 * thread run method
	 */
	@Override
	public void run() {
		String[] tempcrc = null;
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
						out.println("CONNECTED"); //send to the client
						out.flush();
							
						line = in.readLine();
						if ( line.equals("GET-PRINT") ){
							//instead of this, send the printer
							PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
							String printerName = printService.getName();
							Global.impresora = printerName;
							out.println(printerName);
							out.flush();
							Global.txtImpresora.setText(Global.impresora);

							System.out.println(printerName);
						}
						else{
							out.println("error");
							out.flush();
						}
					}
					else if ( tempcrc[0].equals("FIN") ){
						//close connection
						System.out.println("Cerrando conexion");
						out.println("FIN");
						out.flush();
						in.close();
						server.close();
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
				
				Global.documento = fileName;
				
				if ( fileName.endsWith(".txt") ){
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
					t = t.substring(c, fileSize);
					buffer = t.getBytes();
					
					bos.write(buffer, 0, buffer.length);//write into the file
					bos.flush();

					System.out.println("File received");
					out.println("CORRECT");
					out.flush();

					Global.txtDocumento.setText(Global.documento);
							
					Printer printer = new Printer();
					System.out.println(printer.printFile(Global.documento));
				}
				else if ( fileName.endsWith(".pdf") ){
					out.println("CORRECT");
					out.flush();

					System.out.println("File name: " + fileName);
					
					long sizeTemp = 52428800;  //50MB
					InputStream inputStream = server.getInputStream();
					FileOutputStream fileOutputStream = new FileOutputStream(fileName);
					
					buffer = new byte[(int)sizeTemp];
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
					
					n = 0;
					while ( n < fileSize ){
						n += inputStream.read(buffer);
					}
					
					if ( n > 0 ){
						bufferedOutputStream.write(buffer, 0, fileSize);
						bufferedOutputStream.flush();

						System.out.println("File received");
						out.println("CORRECT");
						out.flush();

						Global.txtDocumento.setText(Global.documento);

						out.println("CORRECT");
						out.flush();
						File r = new File(fileName);
						System.out.println("Received " + r.length());
						bufferedOutputStream.close();

						Printer printer = new Printer();
						String result = printer.printFile(Global.documento);
						System.out.println(result);
						out.println(result);
						out.flush();
					}
					else{
						System.err.println("Fichero nulo");
					}
				}
				else{
					System.out.println("Archivo no soportado");
				}
			}

			System.out.println("Cerrando conexion");

			out.flush();
			out.close();
			in.close();
			bis.close();
			server.close();
		}catch(IOException ex){
			System.err.println(ex.getMessage());
		}		
	}
}
