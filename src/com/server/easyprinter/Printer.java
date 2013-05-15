
package com.server.easyprinter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.print.*;
import java.io.*;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

public class Printer implements Printable{
	
	/**
	 * Read file and print it if it can't
	 * @param fileName	name of file
	 * @return					<code>string</code> which has the data or error data
	 * @throws					Exception if it can't send the file to print or return the correct data
	 */
	public String printFile(String fileName){
		String data = "";
		
		if ( fileName.endsWith(".txt") ){
			try{
				BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
				String line = "";

				while ( (line = bufferedReader.readLine()) != null ){
					data += line + "\r\n";
					System.out.print(data);
				}
				
				Global.myStyledText = new AttributedString(data);
				
				PrinterJob printerJob = PrinterJob.getPrinterJob();
				Book book = new Book();
				book.append(new Printer(), new PageFormat());
				printerJob.setPageable(book);
				printerJob.print();
				
				return "print";
			}catch(IOException ex){
				System.err.println(ex.getMessage());
				return "no-file";
			}catch(Exception ex){
				System.err.println(ex.getMessage());
				return "error";
			}
		}
		else{
		
			try{
				FileInputStream fileInputStream = new FileInputStream(fileName);
				
				DocFlavor docFlavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
				Doc doc = new SimpleDoc(fileInputStream, docFlavor, null);
				PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
				PrintService[] printService = PrintServiceLookup.lookupPrintServices(docFlavor, attributeSet);
				PrintService printer = null;
				
				for(int i=0;i<printService.length;i++){
					String svc = printService[i].getName();
					if ( svc.equals(Global.impresora) ){
						printer = printService[i];
						break;
					}
				}
				
				if ( printer != null ){
					DocPrintJob docPrintJob = printer.createPrintJob();
					try{
						docPrintJob.print(doc, attributeSet);
						return "print";
					}catch(Exception ex){
						System.err.println(ex.getMessage());
						return "error";
					}
				}
				else{
					return "no-printer";
				}
			}catch(Exception ex){
				System.err.println(ex.getMessage());
				return "error";
			}
		}
	}
	
	/**
	 * Set up the page attributes
	 * @param graphics		the graphics
	 * @param pageFormat	format page
	 * @param pageIndex		number page
	 * @return						Printable.PAGE_EXISTS
	 * @throws PrinterException 
	 */
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		Graphics2D graphics2d = (Graphics2D) graphics;
		graphics2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		graphics2d.setPaint(Color.black);
		
		Point2D.Float pen = new Point2D.Float();
		AttributedCharacterIterator charIterator = Global.myStyledText.getIterator();
		LineBreakMeasurer measurer = new LineBreakMeasurer(charIterator, graphics2d.getFontRenderContext());
		
		float wrappingWidth = (float) pageFormat.getImageableWidth();
		
		while ( measurer.getPosition() < charIterator.getEndIndex() ){
			TextLayout layout = measurer.nextLayout(wrappingWidth);
			pen.y += layout.getAscent();
			float dx = layout.isLeftToRight() ? 0 : (wrappingWidth - layout.getAdvance());
			layout.draw(graphics2d, pen.x + dx, pen.y);
			pen.y += layout.getDescent() + layout.getLeading();
		}
		
		return Printable.PAGE_EXISTS;
	}
}
