package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.MTGPromoType;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;
import org.magic.tools.MTG;

public class LigaMagicExport extends AbstractCardExport {

	
	private Map<String,EnumCondition> map;
	

	@Override
	public String getFileExtension() {
		return ".xls";
	}
	
	@Override
	public List<MagicCardStock> importStock(String content) throws IOException {
		throw new IOException(" Not implemented, please run by a file");
	}
	
	
	public LigaMagicExport() {
		map = new HashMap<>();
		
		map.put("NM",EnumCondition.NEAR_MINT);
		map.put("SP",EnumCondition.LIGHTLY_PLAYED);
		map.put("MP",EnumCondition.PLAYED);
		map.put("D",EnumCondition.DAMAGED);
		map.put("HP",EnumCondition.POOR);
		map.put("M",EnumCondition.MINT);
		
	}
	
	
	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {
		
		try(Workbook workbook = new XSSFWorkbook();	var out = new FileOutputStream(f))
		{
		
		
		var sheet = workbook.createSheet(MTGConstants.MTG_APP_NAME);
		
		var colNum=0;
		var rowNum=0;
		Row row = sheet.createRow(rowNum++);
		for(String s : new String[] {"Nome do Produto (PT)","Nome do Produto (EN)","Categoria","Quantidade","Preço",
									 "Idioma","Qualidade","Foil (0, 1)","Foil Etched(0, 1)", "Alterada (0, 1)",
									 "Assinada (0, 1)","Buy A Box (0, 1)","DCI (0, 1)","FNM (0, 1)","Oversize (0, 1)",
									 "Pre Release (0, 1)","Promo (0, 1)","Textless (0, 1)","Missprint (0, 1)"})
										{
										var cell= row.createCell(colNum++,CellType.STRING);
										cell.setCellValue(s);
										}
			
			
			for(MagicCardStock st : stock)
			{	
				colNum=0;
				row = sheet.createRow(rowNum++);
				row.createCell(colNum++,CellType.STRING).setCellValue(st.getProduct().getForeignNames().stream().filter(mcn->mcn.getLanguage().contains("Portuguese")).findFirst().orElse(st.getProduct().getForeignNames().get(0)).getName());
				row.createCell(colNum++,CellType.STRING).setCellValue(st.getProduct().getName());
				row.createCell(colNum++,CellType.STRING).setCellValue(st.getProduct().getEdition().getSet());
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(st.getPrice());
				Optional<Entry<String, EnumCondition>> opt = map.entrySet().stream().filter(e->e.getValue()==st.getCondition()).findFirst();
				row.createCell(colNum++,CellType.STRING).setCellValue(opt.isPresent()?opt.get().getValue().name():"");
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(st.isFoil()?1:0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(st.isEtched()?1:0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(st.isAltered()?1:0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(st.isSigned()?1:0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(st.getProduct().getPromotypes().contains(MTGPromoType.BUYABOX)?1:0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(st.getProduct().getPromotypes().contains(MTGPromoType.FNM)?1:0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(st.isOversize()?1:0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(st.getProduct().getCurrentSet().getSet().length()==4 && st.getProduct().getCurrentSet().getSet().startsWith("P")?1:0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(st.getProduct().getText().isBlank()?1:0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(0);
				
				notify(st.getProduct());
			}
		
		
		
		
		
		
		
		
            workbook.write(out);
        }
       
		
	}
	
	
	@Override
	public List<MagicCardStock> importStockFromFile(File f) throws IOException {
		
		var ret = new ArrayList<MagicCardStock>();
		
		try(Workbook workbook =  WorkbookFactory.create(f))
		{
		
			Iterator<Row> it = workbook.getSheetAt(0).iterator();
			it.next(); //skip title line
			
			while(it.hasNext())
			{
				Row row = it.next();
				
				var enName="";
				try {
					enName = row.getCell(1).getStringCellValue();
					var edName = row.getCell(2)!=null ? row.getCell(2).getStringCellValue():null;
					var qte = row.getCell(3)!=null?(int)row.getCell(3).getNumericCellValue():1;
					var priceItem = row.getCell(4)!=null?row.getCell(4).getNumericCellValue():0.0;
					var language = row.getCell(5)!=null? row.getCell(5).getStringCellValue():"";
					var quality = row.getCell(6)!=null?row.getCell(6).getStringCellValue():"";
					var foil = row.getCell(7)!=null && row.getCell(7).getNumericCellValue()==1;
					var etched = row.getCell(8)!=null && row.getCell(8).getNumericCellValue()==1;
					var altered = row.getCell(9)!=null && row.getCell(9).getNumericCellValue()==1;
					
					var mc = findCard(enName,edName);
						
					if(mc!=null)
					{
						var stockItem = new MagicCardStock();
							stockItem.setAltered(altered);
							stockItem.setFoil(foil);
							stockItem.setLanguage(language);
							stockItem.setEtched(etched);
							stockItem.setQte(qte);
							stockItem.setCondition( map.get(quality)!=null?map.get(quality):EnumCondition.MINT);
							stockItem.setPrice(priceItem);
							stockItem.setProduct(mc);
							ret.add(stockItem);
					}
					else
					{
						logger.error("can't find card " + enName );
					}
			
				}
				catch(Exception e)
				{
					logger.error("Error importing " +  enName);
				}
				
			}
		}
		
		return ret;
	}
	

	private MagicCard findCard(String enName, String edName) throws IOException {
		var set = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetByName(edName);
		
		if(set==null)
			logger.warn("Can't find set '" + edName +"' for card " + enName);
		
		
		return MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(enName, set, true).get(0);
	}



	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		exportStock(importFromDeck(deck), dest);
	}
	
	@Override
	public MagicDeck importDeckFromFile(File f) throws IOException {
		var d = new MagicDeck();
			 d.setName(FilenameUtils.getBaseName(f.getName()));
			 
			 importStockFromFile(f).forEach(mcs->d.getMain().put(mcs.getProduct(), mcs.getQte()));
			 
		return d;
	}
	

	@Override
	public String getVersion() {
		return "5.0.0";
	}

	

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
			return null;
	}

	@Override
	public String getName() {
		return "LigaMagic";
	}

}
