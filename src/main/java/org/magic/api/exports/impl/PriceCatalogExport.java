package org.magic.api.exports.impl;

import static org.magic.tools.MTG.getPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.swing.Icon;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractFormattedFileCardExport;
import org.magic.services.MTGConstants;
import org.magic.tools.FileTools;

public class PriceCatalogExport extends AbstractFormattedFileCardExport {

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
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
			String[] exportedPricesProperties = getArray("PROPERTIES_PRICE");
			String[] exportedCardsProperties = getArray("PROPERTIES_CARD");
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

					for (MagicCard mc : deck.getMain().keySet())
					{
						for (MagicPrice prices : prov.getPrice(mc)) {
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

							for (String p : exportedPricesProperties) {
								String val;
								try {
									val = BeanUtils.getProperty(prices, p);
									if (val == null)
										val = "";
									bw.append(val.replaceAll(System.lineSeparator(), "")).append(getSeparator());
								} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
									throw new IOException(e);
								}

							}
							bw.append(System.lineSeparator());
						}
						notify(mc);
					}
			}
			FileTools.saveFile(dest, bw.toString());


	}

	@Override
	public MagicDeck importDeck(String f,String name) throws IOException {
		throw new NotImplementedException("not implemented");
	}

	@Override
	public String getName() {
		return "Price Catalog";
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		var m = super.getDefaultAttributes();
		m.put(PRICER, "");
		m.put("PROPERTIES_CARD", "number,name,cost,supertypes,types,subtypes,editions");
		m.put("PROPERTIES_PRICE", "site,seller,value,currency,language,quality,foil");

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
	protected String getStringPattern() {
		return null;
	}

	@Override
	protected String getSeparator() {
		return ";";
	}


}
