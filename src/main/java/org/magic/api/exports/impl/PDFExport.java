package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.api.sorters.CardsDeckSorter;
import org.magic.services.MTGConstants;
import org.magic.services.tools.POMReader;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DottedBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;

public class PDFExport extends AbstractCardExport {

	private static final String SPACE = "SPACE";
	private float userPoint=72f;

	@Override
	public MODS getMods() {
		return MODS.EXPORT;
	}

	private Cell createCell(MTGCard card) throws IOException {

		ImageData imageData = null;

		try {
			imageData = ImageDataFactory.create(getEnabledPlugin(MTGPictureProvider.class).getFullSizePicture(card),null);
		} catch (Exception _) {
			imageData = ImageDataFactory.create(getEnabledPlugin(MTGPictureProvider.class).getBackPicture(card),null);
		}

		var image = new Image(imageData);

	        image.scaleAbsolute(2.49f*userPoint,3.48f*userPoint);
	        var cell = new Cell();
            if(getBoolean("PRINT_CUT_LINE"))
            {
            	cell.setBorder(new DottedBorder(0.5f));
            }
            else
            	cell.setBorder(Border.NO_BORDER);

            if(getInt(SPACE)!=null)
            	cell.setPadding(getInt(SPACE));

            cell.add(image);

		return cell;
	}

	@Override
	public String getFileExtension() {
		return ".pdf";
	}

	@Override
	public void exportDeck(MTGDeck deck, File f) throws IOException {
		var table = new Table(3).useAllAvailableWidth();

			try(var pdfDocDest = new PdfDocument(new PdfWriter(f));	Document doc = new Document(pdfDocDest) )
			{
				pdfDocDest.setDefaultPageSize(PageSize.A4);
				PdfDocumentInfo info = pdfDocDest.getDocumentInfo();
			    info.setTitle(deck.getName());
			    info.setAuthor(getString("AUTHOR"));
			    info.setCreator(MTGConstants.MTG_APP_NAME);
			    info.setKeywords(deck.getTags().stream().collect(Collectors.joining(",")));
			    info.addCreationDate();

			    var mainList = deck.getMainAsList();

			    Collections.sort(mainList, new CardsDeckSorter(deck) );



				for (MTGCard card : mainList) {
					table.addCell(createCell(card));
					notify(card);
				}

				doc.add(table);

			} catch (Exception e) {
				logger.error("Error in pdf creation {}", f, e);
			}
	}

	@Override
	public MTGDeck importDeck(String f,String name) throws IOException {
		throw new NotImplementedException("Can't generate deck from PDF");
	}

	@Override
	public String getName() {
		return "PDF";
	}

	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		throw new NotImplementedException("Can't import stock from PDF");
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("AUTHOR", new MTGProperty(System.getProperty("user.name")," the Author of the PDF File"),
							   "PRINT_CUT_LINE",MTGProperty.newBooleanProperty("false", "mark the cutline between cards"),
							   SPACE, MTGProperty.newIntegerProperty("0", "set padding between cards", 0, -1));

	}

	@Override
	public String getVersion() {
		return POMReader.readVersionFromPom(PdfDocument.class, "/META-INF/maven/com.itextpdf/kernel/pom.properties");
	}


}
