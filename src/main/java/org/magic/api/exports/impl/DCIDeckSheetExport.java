package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.lang3.NotImplementedException;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
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

	private static final String FILL_CONTINUED_LANDS = "FILL_CONTINUED_LANDS";
	private static final String FORCED_DATE = "FORCED_DATE";
	private static final String DATE_FORMAT = "DATE_FORMAT";
	private static final String LOCATION = "LOCATION";
	private static final String DCI_NUMBER = "DCI_NUMBER";
	private static final String FIRST_NAME = "FIRST_NAME";
	private static final String LAST_NAME = "LAST_NAME";
	private static final String EVENT_NAME = "EVENT_NAME";
	private static final String DECK_DESIGNER = "DECK_DESIGNER";
	private String space = "          ";

	@Override
	public MODS getMods() {
		return MODS.EXPORT;
	}


	@Override
	public MagicDeck importDeck(String f,String n) throws IOException {
		throw new NotImplementedException("Can't generate deck from DCI Sheet");
	}

	@Override
	public String getFileExtension() {
		return ".pdf";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		PdfReader reader = new PdfReader(this.getClass().getResource("/data/mtg_constructed_deck_registration_sheet_pdf1.pdf"));

		Document document = new Document(reader.getPageSize(1));
		PdfWriter writer;
		try {
			writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
		} catch (DocumentException e) {
			throw new IOException(e.getMessage());
		}
		document.open();
		PdfContentByte cb = writer.getDirectContent();

		// copy first page to new pdf file
		PdfImportedPage page = writer.getImportedPage(reader, 1);
		document.newPage();
		cb.addTemplate(page, 0, 0);

		Font helvetica = new Font(FontFamily.HELVETICA, 12);
		BaseFont bfHelv = helvetica.getCalculatedBaseFont(false);

		cb.beginText();
		cb.setFontAndSize(bfHelv, 11);

		// HEADER
		cb.setTextMatrix(page.getWidth() - 51f, page.getHeight() - 49);
		cb.showText(getString(LAST_NAME).substring(0, 1).toUpperCase());

		cb.setTextMatrix(page.getWidth() / 3.2f, page.getHeight() - 73);
		if (!getString(FORCED_DATE).equalsIgnoreCase(""))
			cb.showText(getString(FORCED_DATE));
		else
			cb.showText(new SimpleDateFormat(getString(DATE_FORMAT)).format(new Date()));

		cb.setTextMatrix(page.getWidth() / 1.48f, page.getHeight() - 73);
		cb.showText(getString(EVENT_NAME));

		cb.setTextMatrix(page.getWidth() / 3.2f, page.getHeight() - 96);
		cb.showText(getString(LOCATION));

		cb.setTextMatrix(page.getWidth() / 1.48f, page.getHeight() - 96);
		cb.showText(deck.getName());

		cb.setTextMatrix(page.getWidth() / 1.48f, page.getHeight() - 119);
		if (getString(DECK_DESIGNER).equals(""))
			cb.showText(getString(LAST_NAME) + " " + getString(FIRST_NAME));
		else
			cb.showText(getString(DECK_DESIGNER));

		// MAIN DECK
		int count = 0;
		for (MagicCard mc : deck.getMain().keySet()) {
			cb.setTextMatrix(page.getWidth() / 6.4f, page.getHeight() - 185 - count);
			cb.showText(deck.getMain().get(mc) + space + mc.getName());
			count += 18;
			notify(mc);
		}
		// CONTINUED and BASIC LAND
		if (getString(FILL_CONTINUED_LANDS).equalsIgnoreCase("true")) {
			count = 0;
			for (MagicCard mc : deck.getMain().keySet()) {
				if (mc.isLand()) {
					cb.setTextMatrix(page.getWidth() / 1.7f, page.getHeight() - 185 - count);
					cb.showText(deck.getMain().get(mc) + space + mc.getName());
					count += 18;
				}
				notify(mc);
			}
			
		}
		// SIDEBOARD
		count = 0;
		for (MagicCard mc : deck.getSideBoard().keySet()) {
			cb.setTextMatrix(page.getWidth() / 1.7f, page.getHeight() - 418 - count);
			cb.showText(deck.getSideBoard().get(mc) + space + mc.getName());
			notify(mc);
			count += 18;
		}

		// BOTTOM card count
		cb.setTextMatrix((page.getWidth() / 2f) - 30, 45);
		cb.showText(String.valueOf(deck.getAsList().size()));

		cb.setTextMatrix(page.getWidth() - 70, 100);
		cb.showText(String.valueOf(deck.getSideAsList().size()));

		// LEFT TEXT
		cb.showTextAligned(PdfContentByte.ALIGN_LEFT, getString(LAST_NAME), 52, 90, 90);
		cb.showTextAligned(PdfContentByte.ALIGN_LEFT, getString(FIRST_NAME), 52, 295, 90);

		String dci = getString(DCI_NUMBER);
		count = 0;
		for (int i = 0; i < dci.length(); i++) {
			char c = dci.charAt(i);
			cb.showTextAligned(PdfContentByte.ALIGN_LEFT, String.valueOf(c), 52, (428 + count), 90);
			count += 22;
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
		return new ImageIcon(PDFExport.class.getResource("/icons/plugins/sheet.png"));
	}

	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		throw new NotImplementedException("Can't import stock from DCI Sheet");
	}

	@Override
	public void initDefault() {
		setProperty(EVENT_NAME, "my Event");
		setProperty(DECK_DESIGNER, "MTGDesktopCompanion");
		setProperty(LAST_NAME, "My name");
		setProperty(FIRST_NAME, "My first name");
		setProperty(DCI_NUMBER, "0000000000");
		setProperty(LOCATION, "fill it");
		setProperty(DATE_FORMAT, "dd/MM/YYYY");
		setProperty(FORCED_DATE, "");
		setProperty(FILL_CONTINUED_LANDS, "true");

	}
	
	
}
