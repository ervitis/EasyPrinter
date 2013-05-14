
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

public class ServerEasyPrinter {
	private static final int PORT = 5001;
	private static final int maxConnections = 0;
		
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
		
		JButton print = new JButton("Imprimir");
		print.addActionListener(new PrintJob());
		
		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(start);
		box.add(print);
		
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
	
	/**
	 * Class for the print button event implementing an actionlistener interface
	 */
	private static class PrintJob implements ActionListener{

		/**
		 * Button click event
		 * @param e			the mouse event
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			Printer printer = new Printer();
			
			if ( Global.documento.endsWith(".txt") ){
				String data = printer.readFromFile(Global.documento);

				if ( !data.contentEquals("error") ){
					Global.myStyledText = new AttributedString(data);

					printer.printToPrinter();
				}
				else
				{
					System.err.println("Error al imprimir");
				}
			}
			else{
				String data = printer.readFromFile(Global.documento);
				System.out.println(data);
			}
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
	private String line, input, output;

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
							Global.impresora = printerName;
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
				
				Global.documento = fileName;
				
				if ( fileName.endsWith(".txt") || fileName.endsWith(".pdf") ){
					//sends an answer
					out.println("CORRECT");
					out.flush();

					System.out.println("File name: " + fileName);

					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName));

					while ( n < fileSize ){
						n += bis.read(buffer);
					}
					//bis.close();

					bos.write(buffer, 0, fileSize);//write into the file
					bos.flush();

					//conversion
					String t = new String(buffer, "UTF-8");
					int c = Math.abs(n - fileSize);
					System.out.println("Filesize: " + fileSize + ", n: " + n + ", c: " + c);
					t = t.substring(c);
					buffer = t.getBytes("utf-8");

					System.out.println("File received");
					out.println("CORRECT");
					out.flush();

					//wait for crc code
					String rc = in.readLine();

					//here is where i should check the crc code
					try{
						File f = new File(fileName);
						System.out.println(f.getName());
						String re = CheckSum.getMD5CheckSum(f);
						System.out.println("rc - " + rc);
						System.out.println("re - " + re);

						if ( rc.equals(re) ){
							out.println("CORRECT");
							out.flush();
							File r = new File(fileName);
							System.out.println("Received " + r.length());
							bos.close();
							
							//set the names
							Global.txtDocumento.setText(Global.documento);
							Global.txtImpresora.setText(Global.impresora);
						}		
						else{
							out.println("CRC CORRUPTED");
							out.flush();
							System.out.println("CRC corrupted");
							f.delete();
						}
					}catch(Exception ex){
						System.err.println(ex.getMessage());
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
