package org.magic.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class MagicPDFGenerator {

		static String language;
	
	 public static void generatePDF(List<MagicCard> cards,File f,String lang) {

		 language=lang;
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
		 for(MagicCardNames mcn : card.getForeignNames())
			 if(mcn.getLanguage()!=null)
				 if(mcn.getLanguage().startsWith(language))
				 {
					 id = String.valueOf(mcn.getGathererId());
					 break;
				 }
		 
		 
		 
		 Image image1 = Image.getInstance(new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+id+"&type=card"));
		   	   image1.scalePercent(60);
		   
		   PdfPCell cell = new PdfPCell(image1, false);
		   	cell.setBorder(0);
		   	cell.setPadding(5);
		   	
		return cell;
	 }
	 
	 
	
}
