package org.magic.api.sealedprovider.impl;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.enums.EnumExtra;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractSealedProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractMTGJsonProvider;
import org.magic.api.sealedprovider.impl.MTGCompanionSealedProvider.LOGO;
import org.magic.services.network.URLTools;
import org.magic.services.tools.MTG;

public class MTGJsonSealedProvider extends AbstractSealedProvider {
	
	private Map<MTGEdition, List<MTGSealedProduct>> products;
	
	
	@Override
	public List<MTGEdition> listSets() {
		init();
		return products.keySet().stream().toList();
	}
	
	private void init() 
	{
		
		if(products == null)
		{
			logger.debug("init data from {}",getName());
			
			products = new HashMap<>();
			
			
			var obj = URLTools.extractAsJson(AbstractMTGJsonProvider.MTG_JSON_PRODUCTS).getAsJsonObject();
			
			
			obj.entrySet().forEach(e->{
								MTGEdition ed;
								try {
									ed = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(e.getKey());
								} catch (Exception e1) {
									logger.error("error getting id {}",e);
									return;
								}
								var i=0;
								var list = new ArrayList<MTGSealedProduct>();
								for(var sube : e.getValue().getAsJsonObject().entrySet()) {	
									var prod = new MTGSealedProduct();
										 prod.setEdition(ed);
										 prod.setLang("en");
										 prod.setName(sube.getKey());

										 prod.setNum(i++);
										 try {
											 prod.setUrl("https://product-images.tcgplayer.com/fit-in/437x437/"+sube.getValue().getAsJsonObject().get("identifiers").getAsJsonObject().get("tcgplayerProductId").getAsString()+".jpg");
										 }
										 catch(Exception ex)
										 {
											//do nothing
										 }
										 
										try { 
										 var category = sube.getValue().getAsJsonObject().get("category").getAsString();
										 var subType = sube.getValue().getAsJsonObject().get("subtype").getAsString();
										
										 
										 if(category.equals("BOOSTER_BOX"))
											 prod.setTypeProduct(EnumItems.BOX);
										 else if(category.equals("BOOSTER_PACK"))
											 prod.setTypeProduct(EnumItems.BOOSTER);
										 else if(category.equals("DECK"))
											 prod.setTypeProduct(EnumItems.CONSTRUCTPACK);
										 else if(category.endsWith("_CASE"))
												 prod.setTypeProduct(EnumItems.CASE);
										 else if(category.equals("BUNDLE"))
											 prod.setTypeProduct(EnumItems.BUNDLE);
										 else if(subType.equals("FAT_PACK"))
											 prod.setTypeProduct(EnumItems.FATPACK);
										 else if(subType.equals("STARTER"))
											 prod.setTypeProduct(EnumItems.STARTER);
										 else if(subType.equals("PRERELEASE"))
											 prod.setTypeProduct(EnumItems.PRERELEASEPACK);
										 else if(subType.equals("PLANESWALKER"))
											 prod.setTypeProduct(EnumItems.CONSTRUCTPACK);
										 else if(subType.equals("COMMANDER"))
											 prod.setTypeProduct(EnumItems.COMMANDER_DECK);
										 else if(category.equals("BOX_SET"))
											 prod.setTypeProduct(EnumItems.CONSTRUCTPACK);
										 else if(category.equals("MULTI_DECK") || category.equals("BOX_SET"))
											 prod.setTypeProduct(EnumItems.BUNDLE);
											 
											 
											 setExtra(prod,subType);
										}
										catch(NullPointerException ex)
										{
											logger.error(ex);
										}
										 
										 
										 list.add(prod);
							}
							
								var p = new MTGSealedProduct();
								p.setTypeProduct(EnumItems.SET);
								p.setEdition(ed);
								p.setLang("en");
								p.setNum(1);
								p.setUrl("http://mtgen.net/"+ed.getId().toLowerCase()+"/images/logo-word.webp");
								p.setName(ed.getSet() + " Full set");
								
							list.add(p);
								
								
							products.put(ed, list);	
							
			});
		}
	}
	
	
	private void setExtra(MTGSealedProduct prod, String subType) {
		 if(subType.equals("COLLECTOR"))
			 prod.setExtra(EnumExtra.COLLECTOR);
		 else if(subType.equals("SET"))
			 prod.setExtra(EnumExtra.SET);
		 else if(subType.equals("DEFAULT"))
			 prod.setExtra(EnumExtra.DRAFT);
		 else if(subType.equals("THEME"))
			 prod.setExtra(EnumExtra.THEME);
		 else if(subType.equals("INTRO"))
			 prod.setExtra(EnumExtra.INTRO);
		 else if(subType.equals("PLANESWALKER"))
			 prod.setExtra(EnumExtra.PLANESWALKER);
		 else if(subType.equals("PREMIUM"))
			 prod.setExtra(EnumExtra.VIP);
		 else if(subType.startsWith("GIFT"))
			 prod.setExtra(EnumExtra.GIFT);
	}


	@Override
	public List<MTGSealedProduct> getItemsFor(MTGEdition me) {
		init();
		return products.get(me)!=null?products.get(me):new ArrayList<>();
	}

	@Override
	public List<MTGSealedProduct> search(String name) {
		return new ArrayList<>();
	}

	@Override
	public BufferedImage getLogo(LOGO logo) {
		return null;
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	@Override
	public String getName() {
		return "MTGJson";
	}

}
