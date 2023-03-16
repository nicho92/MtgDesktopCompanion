package org.magic.api.sealedprovider.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractSealedProvider;
import org.magic.api.providers.impl.Mtgjson5Provider;
import org.magic.api.sealedprovider.impl.MTGCompanionSealedProvider.LOGO;
import org.magic.services.tools.MTG;

public class MTGJsonSealedProvider extends AbstractSealedProvider {
	
	private List<MTGSealedProduct> products;
	
	private void init() throws IOException
	{
		if(products==null)
		{
			Mtgjson5Provider prov = null;
			
			if(MTG.getEnabledPlugin(MTGCardsProvider.class).getName().equals("MTGJson5"))
			{
				prov=((Mtgjson5Provider)MTG.getEnabledPlugin(MTGCardsProvider.class));
				prov.listEditions();
			}
			else
			{
				logger.debug("Loading MTGJson5 provider");
				prov = (Mtgjson5Provider)MTG.getPlugin("MTGJson5", MTGCardsProvider.class);
				prov.init();
				prov.listEditions();
			}
			
			products = new ArrayList<>(prov.getSealeds());
			
			
				for(MagicEdition ed : products.stream().map(s->s.getEdition()).distinct().toList())
				{
					var p = new MTGSealedProduct();
						p.setTypeProduct(EnumItems.SET);
						p.setEdition(ed);
						p.setLang("en");
						p.setNum(1);
						p.setName(ed.getSet() + " full set");
						
					products.add(p);
				}
		}
	}
	
	
	@Override
	public List<MTGSealedProduct> getItemsFor(MagicEdition me) {
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
