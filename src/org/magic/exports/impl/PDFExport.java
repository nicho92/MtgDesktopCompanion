package org.magic.exports.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.pictures.impl.GathererPicturesProvider;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFExport {

	 public static void export(List<MagicCard> cards,File f) {

			 Document document = new Document(PageSize.A4,5,5,10,5);

				  document.addAuthor("Nicolas Pihen");
				  document.addCreationDate();
				  document.addCreator("Magic Desktop Companion");
				  document.addTitle(f.getName());
				  
				  PdfPTable table = new PdfPTable(3); 
				  table.setHorizontalAlignment(Element.ALIGN_CENTER);
				  
	      try
	      {
	    	  
	         PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(f+".pdf"));
	         document.open();
	         for(MagicCard card : cards)
	         {
	        	 if(card.getEditions().get(0).getMultiverse_id()!=null)
	        	 {	 
	        		table.addCell(getCells(card));
	        	 	
	        	 }
	        	 else
	        	 {
	        		 table.addCell(new Phrase(card.getName()));
	        	 }
	        	 
	         }
	         document.add(table);
	         document.close();
	         writer.close();
	      } catch (Exception e)
	      {
	         e.printStackTrace();
	      } 
	    }
	
	 
	 private static PdfPCell getCells(MagicCard card) throws BadElementException, MalformedURLException, IOException
	 {
		 
		String id = card.getEditions().get(0).getMultiverse_id();
		Image image1=null;
		try {
			image1 = Image.getInstance(new GathererPicturesProvider().getPictureURL(id));
		} catch (Exception e) {
			e.printStackTrace();
		}
		   	   image1.scalePercent(60);
		   
		   PdfPCell cell = new PdfPCell(image1, false);
		   	cell.setBorder(0);
		   	cell.setPadding(5);
		   	
		return cell;
	 }
	 
	 
	
}
