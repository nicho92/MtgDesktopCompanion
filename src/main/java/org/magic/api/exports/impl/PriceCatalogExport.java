package org.magic.api.exports.impl;

import static org.magic.services.tools.MTG.getPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormatSymbols;
import java.util.Map;

import javax.swing.Icon;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGPrice;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.tools.FileTools;
import org.magic.services.tools.UITools;

public class PriceCatalogExport extends AbstractFormattedFileCardExport {

	private static final String PROPERTIES_CARD = "PROPERTIES_CARD";
	private static final String DECIMAL_SEPARATOR = "DECIMAL_SEPARATOR";
	private static final String PRICER = "PRICER";

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_EURO;
	}

	@Override
	public MODS getMods() {
		return MODS.EXPORT;
	}

	@Override
	public String getStockFileExtension() {
		return ".csv";
	}

	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
			String[] exportedPricesProperties = new String[] {"site","price","foilPrice"};
			String[] exportedCardsProperties = getArray(PROPERTIES_CARD);
			var bw = new StringBuilder();


			for (String k : exportedCardsProperties)
				bw.append(k).append(getSeparator());

			for (String k : exportedPricesProperties)
				bw.append(k).append(getSeparator());

			bw.append(System.lineSeparator());

			if(getString(PRICER).isEmpty())
				throw new IOException("PRICER parameter must be set");


			for(String pricer : getArray(PRICER))
			{
					MTGPricesProvider prov = getPlugin(pricer,MTGPricesProvider.class);

					for (MTGCard mc : deck.getMain().keySet())
					{
						for (String k : exportedCardsProperties) {
							String val;
							try {
								val = BeanUtils.getProperty(mc, k);
								if (val == null)
									val = "";
								bw.append(val.replaceAll(System.lineSeparator(), "")).append(getSeparator());
							} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
								throw new IOException(e);
							}
						}
						
						var pricesList = prov.getPrice(mc);
						
						if(pricesList.isEmpty())
						{
							bw.append(System.lineSeparator());
						}
						else
						{

							
							var mpNormal = pricesList.stream().filter(mp->!mp.isFoil()).findFirst();
							var mpFoil = pricesList.stream().filter(MTGPrice::isFoil).findFirst();
							
							
							bw.append(prov.getName()).append(getSeparator());
							bw.append(mpNormal.isPresent()?UITools.formatDouble(mpNormal.get().getValue(),getChar(DECIMAL_SEPARATOR)):"-").append(getSeparator());
							bw.append(mpFoil.isPresent()?UITools.formatDouble(mpFoil.get().getValue(),getChar(DECIMAL_SEPARATOR)):"-").append(getSeparator());
							bw.append(System.lineSeparator());
						}
						
						notify(mc);
					}
			}
			FileTools.saveFile(dest, bw.toString());


	}

	@Override
	public MTGDeck importDeck(String f,String name) throws IOException {
		throw new NotImplementedException("not implemented");
	}

	@Override
	public String getName() {
		return "Price Catalog";
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
		m.put(PRICER, new MTGProperty("mkm", "select pricer to pull price. See [Pricer plugins](Plugins#pricer)"));
		
		try {
			m.put(PROPERTIES_CARD, new MTGProperty("name,edition,number,types,border,frameEffects","choose cards attributs you want to export. Separated by comma", BeanUtils.describe(new MTGCard()).keySet().stream().toArray(value -> new String[value])));
		} catch (Exception _) {
			
			m.put(PROPERTIES_CARD, new MTGProperty("name,edition,number,types,border,frameEffects","choose cards attributs you want to export. Separated by comma"));
		}
		m.put(DECIMAL_SEPARATOR, new MTGProperty(""+DecimalFormatSymbols.getInstance().getDecimalSeparator(), "Decimal separator format"));
		
		
		
		return m;
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
		return ";";
	}


}
