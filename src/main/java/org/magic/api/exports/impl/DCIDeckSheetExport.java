package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.lang3.NotImplementedException;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractCardExport;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

public class DCIDeckSheetExport extends AbstractCardExport {

	private String space ="          ";

	
	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}

	
	@Override
	public MagicDeck importDeck(File f) throws IOException {
		throw new NotImplementedException("Can't generate deck from DCI Sheet");
	}

	
	public DCIDeckSheetExport() {
		
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("EVENT_NAME", "my Event");
			props.put("DECK_DESIGNER", "MTGDesktopCompanion");
			props.put("LAST_NAME", "My name");
			props.put("FIRST_NAME", "My first name");
			props.put("DCI_NUMBER", "0000000000");
			props.put("LOCATION", "fill it");
			props.put("DATE_FORMAT", "dd/MM/YYYY");
			props.put("FORCED_DATE", "");
			props.put("FILL_CONTINUED_LANDS", "true");
			props.put("PDF_URL", "https://wpn.wizards.com/sites/wpn/files/attachements/mtg_constructed_deck_registration_sheet_pdf11.pdf");
			save();
		}
	}
	
	@Override
	public void export(List<MagicCard> cards, File f) throws IOException {
		MagicDeck d = new MagicDeck();
				  d.setName("Search Result");

		for(MagicCard mc : cards)
		{
			d.getMap().put(mc, 1);
		}
		
		export(d,f);
		
	}

	@Override
	public String getFileExtension() {
		return ".pdf";
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		PdfReader reader = new PdfReader(new URL(getProperty("PDF_URL")));
		
		Document document = new Document(reader.getPageSize(1));
		PdfWriter writer;
		try {
			writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
		} catch (DocumentException e) {
			throw new IOException(e.getMessage());
		}
		document.open();
		PdfContentByte cb = writer.getDirectContent();
		
		//copy first page to new pdf file
		PdfImportedPage page = writer.getImportedPage(reader, 1); 
		document.newPage();
		cb.addTemplate(page, 0, 0);
		
		Font helvetica = new Font(FontFamily.HELVETICA, 12);
        BaseFont bfHelv = helvetica.getCalculatedBaseFont(false);
		
        
        cb.beginText();
		cb.setFontAndSize(bfHelv, 11);
		
		//HEADER
			cb.setTextMatrix(page.getWidth()-51f, page.getHeight()-49); 
			cb.showText(getProperty("LAST_NAME").substring(0, 1).toUpperCase()); 
		
			cb.setTextMatrix(page.getWidth()/3.2f, page.getHeight()-73); 
			if(!getProperty("FORCED_DATE").equalsIgnoreCase(""))
				cb.showText(getProperty("FORCED_DATE"));
			else
				cb.showText(new SimpleDateFormat(getProperty("DATE_FORMAT")).format(new Date())); 
			
			cb.setTextMatrix(page.getWidth()/1.48f, page.getHeight()-73); 	
			cb.showText(getProperty("EVENT_NAME"));
			
			cb.setTextMatrix(page.getWidth()/3.2f, page.getHeight()-96); 
			cb.showText(getProperty("LOCATION")); 
			
			cb.setTextMatrix(page.getWidth()/1.48f, page.getHeight()-96); 	
			cb.showText(deck.getName()); 
	
			cb.setTextMatrix(page.getWidth()/1.48f, page.getHeight()-119); 	
			if(getProperty("DECK_DESIGNER").equals(""))
				cb.showText(getProperty("LAST_NAME") +" " + getProperty("FIRST_NAME")); 
			else
				cb.showText(getProperty("DECK_DESIGNER"));
	
			
		//MAIN DECK
			int count=0;
			for(MagicCard mc : deck.getMap().keySet())
			{
				cb.setTextMatrix(page.getWidth()/6.4f, page.getHeight()-185-count); 	
				cb.showText(deck.getMap().get(mc) + space + mc.getName());
				count+=18;
			}
		//CONTINUED and BASIC LAND
			if(getProperty("FILL_CONTINUED_LANDS").equalsIgnoreCase("true"))
			{
				count=0;
				for(MagicCard mc : deck.getMap().keySet())
				{
					if(mc.getTypes().contains("Land"))
					{ 
					  cb.setTextMatrix(page.getWidth()/1.7f, page.getHeight()-185-count); 	
					  cb.showText(deck.getMap().get(mc) + space + mc.getName());
					  count+=18;
					}
				}
			}
		//SIDEBOARD
			count=0;
			for(MagicCard mc : deck.getMapSideBoard().keySet())
			{
				cb.setTextMatrix(page.getWidth()/1.7f, page.getHeight()-418-count); 	
				cb.showText(deck.getMapSideBoard().get(mc) + space + mc.getName());
				count+=18;
			}	
			
			
		
	
		//BOTTOM card count
			cb.setTextMatrix((page.getWidth()/2f)-30, 45); 	
			cb.showText(String.valueOf(deck.getAsList().size()));
			
			cb.setTextMatrix(page.getWidth()-70, 100); 	
			cb.showText(String.valueOf(deck.getSideAsList().size()));
			
			
		//LEFT TEXT 
			cb.showTextAligned(PdfContentByte.ALIGN_LEFT, getProperty("LAST_NAME"), 52, 90, 90);
			cb.showTextAligned(PdfContentByte.ALIGN_LEFT, getProperty("FIRST_NAME"), 52, 295, 90);
			
			String dci = getProperty("DCI_NUMBER");
			count=0;
			for(int i=0;i<dci.length();i++)
			{
				char c = dci.charAt(i);
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, String.valueOf(c), 52, (428+count), 90);
				count+=22;
			}
			
			
			cb.endText(); 
			
		
		document.close();

	}

	@Override
	public String getName() {
		return "DCI Deck Sheet";
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(PDFExport.class.getResource("/icons/sheet.png"));
	}


	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		MagicDeck d = new MagicDeck();
		d.setName(f.getName());
		
		for(MagicCardStock mcs : stock)
		{
			d.getMap().put(mcs.getMagicCard(), mcs.getQte());
		}
		
		export(d, f);
		
	}


	@Override
	public List<MagicCardStock> importStock(File f) throws IOException {
		throw new NotImplementedException("Can't import stock from DCI Sheet");
	}

}
