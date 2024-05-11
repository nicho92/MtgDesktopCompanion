package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.Version;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.beans.enums.EnumPromoType;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.tools.MTG;

public class LigaMagicExport extends AbstractCardExport {


	@Override
	public String getFileExtension() {
		return ".xls";
	}

	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {
		throw new IOException(" Not implemented, please run by a file");
	}
	
	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.EXTERNAL_FILE_FORMAT;
	}
	


	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {

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


			for(MTGCardStock st : stock)
			{
				colNum=0;
				row = sheet.createRow(rowNum++);
				row.createCell(colNum++,CellType.STRING).setCellValue(st.getProduct().getForeignNames().stream().filter(mcn->mcn.getLanguage().contains("Portuguese")).findFirst().orElse(st.getProduct().getForeignNames().get(0)).getName());
				row.createCell(colNum++,CellType.STRING).setCellValue(st.getProduct().getName());
				row.createCell(colNum++,CellType.STRING).setCellValue(st.getProduct().getEdition().getSet());
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(st.getPrice());
				row.createCell(colNum++,CellType.STRING).setCellValue(aliases.getConditionFor(this, st.getCondition()));
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(st.isFoil()?1:0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(st.isEtched()?1:0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(st.isAltered()?1:0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(st.isSigned()?1:0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(st.getProduct().getPromotypes().contains(EnumPromoType.BUYABOX)?1:0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(st.getProduct().getPromotypes().contains(EnumPromoType.FNM)?1:0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(st.getCondition()==EnumCondition.OVERSIZED?1:0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(st.getProduct().getEdition().getSet().length()==4 && st.getProduct().getEdition().getSet().startsWith("P")?1:0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(st.getProduct().getText().isBlank()?1:0);
				row.createCell(colNum++,CellType.NUMERIC).setCellValue(0);

				notify(st.getProduct());
			}
            workbook.write(out);
        }


	}


	@Override
	public List<MTGCardStock> importStockFromFile(File f) throws IOException {

		var ret = new ArrayList<MTGCardStock>();

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
						var stockItem = new MTGCardStock(mc);
							stockItem.setAltered(altered);
							stockItem.setFoil(foil);
							stockItem.setLanguage(language);
							stockItem.setEtched(etched);
							stockItem.setQte(qte);
							stockItem.setCondition( aliases.getReversedConditionFor(this, quality, null));
							stockItem.setPrice(priceItem);
							ret.add(stockItem);
					}
					else
					{
						logger.error("can't find card {}",enName );
					}

				}
				catch(Exception e)
				{
					logger.error("Error importing {}",enName);
				}

			}
		}

		return ret;
	}


	private MTGCard findCard(String enName, String edName) throws IOException {
		var set = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetByName(edName);

		if(set==null)
			logger.warn("Can't find set '{}' for card {}",edName,enName);


		return MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(enName, set, true).get(0);
	}

	@Override
	public MTGDeck importDeckFromFile(File f) throws IOException {
		var d = new MTGDeck();
			 d.setName(FilenameUtils.getBaseName(f.getName()));

			 importStockFromFile(f).forEach(mcs->d.getMain().put(mcs.getProduct(), mcs.getQte()));

		return d;
	}


	@Override
	public String getVersion() {
		return Version.getVersion();
	}



	@Override
	public MTGDeck importDeck(String f, String name) throws IOException {
			return null;
	}

	@Override
	public String getName() {
		return "LigaMagic";
	}

}
