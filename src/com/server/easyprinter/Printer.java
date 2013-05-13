
package com.server.easyprinter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.text.AttributedCharacterIterator;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

public class Printer implements Printable{
	
	public String readFromFile(String fileName){
		String data = "";
		long n = 0;
		
		if ( fileName.endsWith(".txt") ){
			try{
				BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
				String line = "";

				while ( (line = bufferedReader.readLine()) != null ){
					data += line + "\n";
				}

				return data;
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
	
	public void printToPrinter(){
		PrinterJob printerJob = PrinterJob.getPrinterJob();
		
		Book book = new Book();
		
		book.append(new Printer(), new PageFormat());
		printerJob.setPageable(book);
		
		boolean doPrint = printerJob.printDialog();
		if ( doPrint ){
			try{
				printerJob.print();
			}catch(Exception ex){
				System.err.println(ex.getMessage());
			}
		}
	}
	
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
