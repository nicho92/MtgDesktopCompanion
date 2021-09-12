package org.beta;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.gui.components.shops.StockItemPanel;
import org.magic.tools.MTG;

public class LigaMagicExport extends AbstractCardExport {

	public static void main(String[] args) throws EncryptedDocumentException, IOException {
		
		
		new LigaMagicExport().importStockFromFile(new File("D:\\Téléchargements\\estoquemtg.xls"));
		
	}

	@Override
	public String getFileExtension() {
		return "xls";
	}
	
	
	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		return null;
		
	}
	
	
	@Override
	public List<MagicCardStock> importStockFromFile(File f) throws IOException {
		
		var ret = new ArrayList<MagicCardStock>();
		
		try(Workbook workbook =  WorkbookFactory.create(f))
		{
		
			Iterator<Row> it = workbook.getSheetAt(0).iterator();
			it.next(); //skip title line
			
			var enName="";
			var edName="";
			while(it.hasNext())
			{
				Row row = it.next();
				
				try {
					
				
					
				enName = row.getCell(1).getStringCellValue();
				edName = row.getCell(2).getStringCellValue();
				
				var qte = (int)row.getCell(3).getNumericCellValue();
				var priceItem = row.getCell(4)!=null?row.getCell(4).getNumericCellValue():0.0;
				var language = row.getCell(5)!=null? row.getCell(5).getStringCellValue():"";
				var quality = row.getCell(6).getStringCellValue();
				var foil = row.getCell(7)!=null?row.getCell(7).getNumericCellValue()==1:false;
				var etched = row.getCell(8)!=null?row.getCell(8).getNumericCellValue()==1:false;
				var altered = row.getCell(9)!=null?row.getCell(9).getNumericCellValue()==1:false;
				
				
				
				var mc = findCard(enName,edName);
				
				var stockItem = new MagicCardStock();
					stockItem.setAltered(altered);
					stockItem.setFoil(foil);
					stockItem.setLanguage(language);
					stockItem.setEtched(etched);
					stockItem.setQte(qte);
					stockItem.setCondition(eval(quality));
					stockItem.setPrice(priceItem);
					stockItem.setProduct(mc);
					ret.add(stockItem);
				
				
				
				}
				catch(NullPointerException ex)
				{
					logger.error("empty cell for " + enName + " " + edName);
				}
				
			}
		}
		
		return ret;
	}
	

	private MagicCard findCard(String enName, String edName) throws IOException {
		
		var set = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetByName(edName);
		
		return MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(enName, set, true).get(0);
		
		
	}

	private EnumCondition eval(String quality) {
		switch (quality) {

		case "NM": return EnumCondition.NEAR_MINT;
		case "SP": return EnumCondition.LIGHTLY_PLAYED;
		case "MP": return EnumCondition.PLAYED;
		case "D": return EnumCondition.DAMAGED;
		case "HP": return EnumCondition.POOR;
		case "M": return EnumCondition.MINT;
		default : logger.warn("Missing "+ quality); return EnumCondition.GOOD;
			
			
		}
		 
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		
		
	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "LigaMagic";
	}

}
