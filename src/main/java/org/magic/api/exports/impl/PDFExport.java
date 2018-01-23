package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

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
	
	
	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}
	
	
	
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
			int i=0;
			for(MagicCard card : cards)
			{
				try
				{	 
					table.addCell(getCells(card));

				}
				catch(Exception e)
				{
					table.addCell(new Phrase(card.getName()));
				}
				
				setChanged();
				notifyObservers(i++);

			}
			document.add(table);
			document.close();
			writer.close();
		} catch (Exception e)
		{
			logger.error("Error in pdf creation " + f,e);
		} 
	}


	private PdfPCell getCells(MagicCard card) throws BadElementException, MalformedURLException, IOException
	{

		Image image1=null;
		try {
			image1 = Image.getInstance(MTGControler.getInstance().getEnabledPicturesProvider().getPicture(card,null),null);
		} catch (Exception e) {
			image1 = Image.getInstance(MTGControler.getInstance().getEnabledPicturesProvider().getBackPicture(),null);
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
		return new ImageIcon(PDFExport.class.getResource("/icons/pdf.png"));
	}

	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws Exception {
		MagicDeck d = new MagicDeck();
		d.setName(f.getName());
		
		for(MagicCardStock mcs : stock)
		{
			d.getMap().put(mcs.getMagicCard(), mcs.getQte());
		}
		
		export(d, f);
		
	}

	@Override
	public List<MagicCardStock> importStock(File f) throws Exception {
		throw new Exception("Can't import stock from PDF");
	}

}
