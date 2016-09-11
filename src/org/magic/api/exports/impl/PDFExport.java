package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MagicFactory;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFExport extends AbstractCardExport {

	Document document;
	
	public PDFExport() {
		super();
		
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("AUTHOR", "Nicolas PIHEN");
			save();
		}
		
		
	}
	
	public void export(List<MagicCard> cards,File f) {
		PdfPTable table = new PdfPTable(3); 
		table.setHorizontalAlignment(Element.ALIGN_CENTER);

		try
		{
			document = new Document(PageSize.A4,5,5,10,5);
			document.addAuthor(getProperty("AUTHOR").toString());
			document.addCreationDate();
			document.addCreator("Magic Desktop Companion");
			document.addTitle(f.getName());

			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(f));
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
			image1 = Image.getInstance(MagicFactory.getInstance().getEnabledPicturesProvider().getPicture(card,null),null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		image1.scalePercent(60);

		PdfPCell cell = new PdfPCell(image1, false);
		cell.setBorder(0);
		cell.setPadding(5);

		return cell;
	}


	@Override
	public String getFileExtension() {
		return ".pdf";
	}


	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		export(deck.getAsList(),dest);
	}


	@Override
	public MagicDeck importDeck(File f) throws Exception {
		throw new Exception("Can't generate deck from PDF");
	}


	@Override
	public String getName() {
		return "PDF";
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(PDFExport.class.getResource("/res/pdf.png"));
	}

}
