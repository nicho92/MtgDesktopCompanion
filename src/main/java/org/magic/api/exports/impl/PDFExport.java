package org.magic.api.exports.impl;

import static org.magic.tools.MTG.getEnabledPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.Version;
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

	private PdfDocument  pdfDocDest;
	private float userPoint=72f;
	
	@Override
	public MODS getMods() {
		return MODS.EXPORT;
	}
	
	private Cell createCell(MagicCard card) throws IOException {

		ImageData imageData = null;

		try {
			imageData = ImageDataFactory.create(getEnabledPlugin(MTGPictureProvider.class).getPicture(card, null),	null);
		} catch (Exception e) {
			imageData = ImageDataFactory.create(getEnabledPlugin(MTGPictureProvider.class).getBackPicture(),null);
		}
		
		Image image = new Image(imageData);
			
	        image.scaleAbsolute(2.49f*userPoint,3.48f*userPoint);
            Cell cell = new Cell();
            
            if(getBoolean("PRINT_CUT_LINE"))
            {
            	cell.setBorder(new DottedBorder(0.5f));
            }
            else
            	cell.setBorder(Border.NO_BORDER);
            	
            cell.add(image);
	
		return cell;
	}

	@Override
	public String getFileExtension() {
		return ".pdf";
	}

	@Override
	public void exportDeck(MagicDeck deck, File f) throws IOException {
			Table table = new Table(3).useAllAvailableWidth();
			pdfDocDest = new PdfDocument(new PdfWriter(f));
			pdfDocDest.setDefaultPageSize(PageSize.A4);
				  
			PdfDocumentInfo info = pdfDocDest.getDocumentInfo();
		    info.setTitle(deck.getName());
		    info.setAuthor(getString("AUTHOR"));
		    info.setCreator(MTGConstants.MTG_APP_NAME);
		    info.setKeywords(deck.getTags().stream().collect(Collectors.joining(",")));
		    info.addCreationDate();
		   
		   try (
				PdfWriter writer = new PdfWriter(f); 
				Document doc = new Document(pdfDocDest)
			   )
		   {  
				for (MagicCard card : deck.getMainAsList()) {
					table.addCell(createCell(card));
					notify(card);
				}
				
				doc.add(table);

		} catch (Exception e) {
			logger.error("Error in pdf creation " + f, e);
		}
	}

	@Override
	public MagicDeck importDeck(String f,String name) throws IOException {
		throw new NotImplementedException("Can't generate deck from PDF");
	}

	@Override
	public String getName() {
		return "PDF";
	}

	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		throw new NotImplementedException("Can't import stock from PDF");
	}

	@Override
	public void initDefault() {
		setProperty("AUTHOR", System.getenv("user.name"));
		setProperty("PRINT_CUT_LINE","true");
	
	}

	@Override
	public String getVersion() {
		return Version.getInstance().getRelease();
	}
	

	@Override
	public boolean equals(Object obj) {
		
		if(obj ==null)
			return false;
		
		return hashCode()==obj.hashCode();
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}

}
