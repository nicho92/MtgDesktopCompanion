package org.magic.api.sealedprovider.impl;

import java.awt.image.BufferedImage;
import java.util.List;

import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractSealedProvider;
import org.magic.api.providers.impl.Mtgjson5Provider;
import org.magic.api.sealedprovider.impl.MTGCompanionSealedProvider.LOGO;
import org.magic.services.tools.MTG;

public class MTGJsonSealedProvider extends AbstractSealedProvider {

	
	List<MTGSealedProduct> products;
	
	private void init()
	{
		if(products==null)
		{
			
			Mtgjson5Provider prov = null;
			
			if(MTG.getEnabledPlugin(MTGCardsProvider.class).getName().equals("MTGJson5"))
			{
				prov=((Mtgjson5Provider)MTG.getEnabledPlugin(MTGCardsProvider.class));	
			}
			else
			{
				prov = new Mtgjson5Provider();
				prov.init();
			}
			
			products = prov.getSealeds().stream().toList();
		}
	}
	
	
	@Override
	public List<MTGSealedProduct> getItemsFor(MagicEdition me) {
		init();
		return products.stream().filter(mts->mts.getEdition().getId().equals(me.getId())).toList();
	}

	@Override
	public List<MTGSealedProduct> search(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BufferedImage getLogo(LOGO logo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "MTGJson";
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
