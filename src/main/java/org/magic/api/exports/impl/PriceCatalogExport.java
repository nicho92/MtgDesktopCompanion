package org.magic.api.exports.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import org.apache.commons.beanutils.BeanUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MTGPricesProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class PriceCatalogExport extends AbstractCardExport {

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
	public void export(MagicDeck deck, File dest) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(dest))) 
		{
			String[] exportedPricesProperties = getString("PROPERTIES_PRICE").split(",");
			String[] exportedCardsProperties = getString("PROPERTIES_CARD").split(",");
			
			for (String k : exportedCardsProperties)
				bw.write(k + ";");
			
			for (String k : exportedPricesProperties)
				bw.write(k + ";");

			bw.write("\n");
			int i = 0;
			
			for(String pricer : getString("PRICER").split(","))
			{	
				MTGPricesProvider prov = MTGControler.getInstance().getPlugin(pricer,MTGPricesProvider.class);
			
					for (MagicCard mc : deck.getMap().keySet()) {
						for (MagicPrice prices : prov.getPrice(mc.getCurrentSet(), mc)) {
							for (String k : exportedCardsProperties) {
								String val;
								try {
									val = BeanUtils.getProperty(mc, k);
									if (val == null)
										val = "";
									bw.write(val.replaceAll("\n", "") + ";");
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
									bw.write(val.replaceAll("\n", "") + ";");
								} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
									throw new IOException(e);
								}
		
							}
							bw.write("\n");
						}
						setChanged();
						notifyObservers(i++);
					}
			}
		}

	}

	@Override
	public MagicDeck importDeck(File f) throws IOException {
		return null;
	}

	@Override
	public List<MagicCardStock> importStock(File f) throws IOException {
		return new ArrayList<>();
	}

	@Override
	public String getName() {
		return "Price Catalog";
	}

	@Override
	public void initDefault() {
		setProperty("PRICER", "");
		setProperty("PROPERTIES_CARD", "number,name,cost,supertypes,types,subtypes,editions");
		setProperty("PROPERTIES_PRICE", "site,seller,value,currency,language,quality,foil");
		
	}


}
