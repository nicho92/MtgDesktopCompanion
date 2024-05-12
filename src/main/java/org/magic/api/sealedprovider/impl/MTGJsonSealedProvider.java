package org.magic.api.sealedprovider.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	
	private List<MTGSealedProduct> products;
	
	
	private void init() throws IOException
	{
		if(products == null)
		{
			products = new ArrayList<>();
			var obj = URLTools.extractAsJson(AbstractMTGJsonProvider.MTG_JSON_PRODUCTS).getAsJsonObject();
			
			
			obj.entrySet().forEach(e->{
								MTGEdition ed;
								try {
									ed = MTG.getEnabledPlugin(MTGCardsProvider.class).getSetById(e.getKey());
								} catch (IOException e1) {
									logger.error("error getting id {}",e);
									return;
								}
								var i=0;
								 
								for(var sube : e.getValue().getAsJsonObject().entrySet()) {	
									var prod = new MTGSealedProduct();
										 prod.setEdition(ed);
										 prod.setLang("en");
										 prod.setName(sube.getKey());
										 prod.setTypeProduct(EnumItems.SEALED);
										 prod.setNum(i++);
										 try {
											 prod.setUrl("https://product-images.tcgplayer.com/fit-in/437x437/"+sube.getValue().getAsJsonObject().get("identifiers").getAsJsonObject().get("tcgplayerProductId").getAsString()+".jpg");
										 }
										 catch(Exception ex)
										 {
											 //do nothing
										 }
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
										 
										 
										 setExtra(prod,subType);
										 products.add(prod);
									
							}
								
				
			});
			
			
			
				for(MTGEdition ed : products.stream().map(s->s.getEdition()).distinct().toList())
				{
					var p = new MTGSealedProduct();
						p.setTypeProduct(EnumItems.SET);
						p.setEdition(ed);
						p.setLang("en");
						p.setNum(1);
						p.setUrl("http://mtgen.net/"+ed.getId().toLowerCase()+"/images/logo-word.webp");
						p.setName(ed.getSet() + " full set");
						
					products.add(p);
				}
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
		 else if(subType.equals("GIFT"))
			 prod.setExtra(EnumExtra.GIFT);
	}


	@Override
	public List<MTGSealedProduct> getItemsFor(MTGEdition me) {
		try {
			init();
		} catch (IOException e) {
			logger.error("Error Loading list {}",me,e);
			return new ArrayList<>();
		}
		return products.stream().filter(mts->mts.getEdition().getId().equals(me.getId())).toList();
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
