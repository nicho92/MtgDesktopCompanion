package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.lang3.NotImplementedException;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGControler;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFExport extends AbstractCardExport {

	Document document;

	@Override
	public MODS getMods() {
		return MODS.EXPORT;
	}


	private PdfPCell getCells(MagicCard card) throws BadElementException, IOException {

		Image image1 = null;
		try {
			image1 = Image.getInstance(MTGControler.getInstance().getEnabledPicturesProvider().getPicture(card, null),
					null);
		} catch (Exception e) {
			image1 = Image.getInstance(MTGControler.getInstance().getEnabledPicturesProvider().getBackPicture(), null);
		}

		int h = getInt("CARD_HEIGHT");
		int w = getInt("CARD_WIDTH");

		image1.scaleAbsolute(w, h);

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
	public void export(MagicDeck deck, File f) throws IOException {
		PdfPTable table = new PdfPTable(3);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);

		try {
			document = new Document(PageSize.A4, 5, 5, 10, 5);
			document.addAuthor(getString("AUTHOR"));
			document.addCreationDate();
			document.addCreator("Magic Desktop Companion");
			document.addTitle(deck.getName());

			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(f));
			document.open();
			int i = 0;
			for (MagicCard card : deck.getAsList()) {
				table.addCell(getCells(card));
				setChanged();
				notifyObservers(i++);

			}
			document.add(table);
			document.close();
			writer.close();
		} catch (Exception e) {
			logger.error("Error in pdf creation " + f, e);
		}
	}

	@Override
	public MagicDeck importDeck(File f) throws IOException {
		throw new NotImplementedException("Can't generate deck from PDF");
	}

	@Override
	public String getName() {
		return "PDF";
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(PDFExport.class.getResource("/icons/plugins/pdf.png"));
	}

	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		MagicDeck d = new MagicDeck();
		d.setName(f.getName());

		for (MagicCardStock mcs : stock) {
			d.getMap().put(mcs.getMagicCard(), mcs.getQte());
		}

		export(d, f);

	}

	@Override
	public List<MagicCardStock> importStock(File f) throws IOException {
		throw new NotImplementedException("Can't import stock from PDF");
	}

	@Override
	public void initDefault() {
		setProperty("AUTHOR", "Nicolas PIHEN");
		setProperty("CARD_HEIGHT", "163");
		setProperty("CARD_WIDTH", "117");
	}

	@Override
	public String getVersion() {
		return "1.1";
	}

}
