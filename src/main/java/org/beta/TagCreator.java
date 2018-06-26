package org.beta;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.magic.services.extra.BoosterPicturesProvider;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class TagCreator {

	
	public static void main(String[] args) throws IOException, DocumentException {
		BoosterPicturesProvider prov = new BoosterPicturesProvider();
		Document document = new Document(PageSize.A4, 5, 5, 10, 5);
		PdfPTable table = new PdfPTable(1);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(new File("D:/test.pdf")));	
		document.open();
		PdfContentByte pdfCB = new PdfContentByte(writer);
		
		Image im= Image.getInstance(pdfCB,prov.getBannerFor("IMA"),1);
		im.scaleAbsoluteWidth(567);
		
		table.addCell(im);
		
		document.add(table);
		document.close();
		writer.close();
	}
	
}
