package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
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

	private PdfReader reader;
	
	
	@Override
	public MagicDeck importDeck(File f) throws Exception {
		throw new Exception("Can't generate deck from DCI Sheet");
	}

	
	public DCIDeckSheetExport() {
		
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("EVENT_NAME", "fill it");
			props.put("DECK_DESIGNER", System.getProperty("user.name"));
			props.put("LAST_NAME", "fill it");
			props.put("FIRST_NAME", "fill it");
			props.put("DCI_NUMBER", "fill it");
			props.put("PDF_URL", "https://wpn.wizards.com/sites/wpn/files/attachements/mtg_constructed_deck_registration_sheet_pdf11.pdf");
			save();
		}
	}
	
	@Override
	public void export(List<MagicCard> cards, File f) throws Exception {
		
		
	}

	@Override
	public String getFileExtension() {
		return ".pdf";
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		reader = new PdfReader(new URL(props.getProperty("PDF_URL")));
		
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
		
		
		// Add your new data / text here
		
		Font helvetica = new Font(FontFamily.HELVETICA, 12);
        BaseFont bf_helv = helvetica.getCalculatedBaseFont(false);
			cb.beginText();
			cb.setFontAndSize(bf_helv, 11);
			cb.setTextMatrix(0.1f, 0.5f); // placement du texte en x et y
			cb.showText("Coucou"); // écriture
			cb.endText();  // fin d'une séquence d'écriture
		

		document.close();

	}

	@Override
	public String getName() {
		return "DCI Deck Sheet";
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(PDFExport.class.getResource("/res/pdf.png"));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
