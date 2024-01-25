package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.tools.FileTools;

public class DeckedBuilder extends AbstractFormattedFileCardExport {


	private static final String COLUMNS ="Total Qty,Reg Qty,Foil Qty,Card,Set,Mana Cost,Card Type,Color,Rarity,Mvid,Single Price,Single Foil Price,Total Price,Price Source,Notes";

	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public String getName() {
		return "DeckedBuilder";
	}

	@Override
	public void exportStock(List<MTGCardStock> stock, File f) throws IOException {

		var temp = new StringBuilder(COLUMNS);
		temp.append(System.lineSeparator());
		stock.forEach(st->{
			temp.append(st.getQte()).append(getSeparator());

			if(st.isFoil())
				temp.append(0).append(getSeparator()).append(st.getQte()).append(getSeparator());
			else
				temp.append(st.getQte()).append(getSeparator()).append(0).append(getSeparator());

			temp.append(commated(st.getProduct().getName())).append(getSeparator());

			temp.append(st.getProduct().getCurrentSet().getSet()).append(getSeparator());
			temp.append(st.getProduct().getCost()).append(getSeparator());
			temp.append(st.getProduct().getFullType()).append(getSeparator());


			if(st.getProduct().getColors().size()>1)
				temp.append("Gold").append(getSeparator());
			else
				temp.append(st.getProduct().getColors()).append(getSeparator());

			temp.append(st.getProduct().getRarity()).append(getSeparator());
			temp.append(st.getProduct().getMultiverseid()).append(getSeparator());

			if(st.isFoil())
				temp.append(0.0).append(getSeparator()).append(st.getPrice()).append(getSeparator());
			else
				temp.append(st.getPrice()).append(getSeparator()).append(0.0).append(getSeparator());


			temp.append("MtgCompanion").append(getSeparator());
			temp.append("\"").append(st.getComment()).append("\"").append(getSeparator());


			temp.append(System.lineSeparator());
		});

		FileTools.saveFile(f, temp.toString());

	}


	@Override
	public List<MTGCardStock> importStock(String content) throws IOException {

		List<MTGCardStock> stocks = new ArrayList<>();

		matches(content,true).forEach(m->{
			var qtyRegular = Integer.parseInt(m.group(2));
			var qtyFoil = Integer.parseInt(m.group(3));
			var mc = parseMatcherWithGroup(m, 4, 5, true, FORMAT_SEARCH.NAME,FORMAT_SEARCH.NAME);
			if(mc!=null)
			{
				if(qtyFoil>0)
				{
					var stock = new MTGCardStock(mc);
								   stock.setPrice(Double.parseDouble(m.group(12)));
								   stock.setFoil(true);
								   stock.setQte(qtyFoil);
					stocks.add(stock);
				}

				if(qtyRegular>0)
				{
					var stock = new MTGCardStock(mc);
					   stock.setPrice(Double.parseDouble(m.group(11)));
					   stock.setFoil(false);
					   stock.setQte(qtyRegular);
					   stocks.add(stock);
				}
				notify(mc);
			}
		});
		return stocks;
	}


	@Override
	protected boolean skipFirstLine() {
		return true;
	}

	@Override
	protected String[] skipLinesStartWith() {
		return new String[0];
	}
	
	@Override
	protected String getSeparator() {
		return ",";
	}

}
