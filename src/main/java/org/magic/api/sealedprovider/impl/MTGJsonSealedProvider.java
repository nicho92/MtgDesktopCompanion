package org.magic.api.sealedprovider.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MTGSealedProduct;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractSealedProvider;
import org.magic.api.providers.impl.Mtgjson5Provider;
import org.magic.api.sealedprovider.impl.MTGCompanionSealedProvider.LOGO;

public class MTGJsonSealedProvider extends AbstractSealedProvider {

	
	private List<MTGSealedProduct> products;
	
	private void init()
	{
		
		if(products==null)
		{
			var provider = new Mtgjson5Provider();
			provider.init();
			try {
				provider.listEditions();
			} catch (IOException e) {
				logger.error(e);
			}
			
			products = provider.getSealeds();
			
		}
		
		
	}
	
	@Override
	public List<MTGSealedProduct> getItemsFor(MagicEdition me) {
		init();
		return products.stream().filter(p->p.getEdition().getId().equals(me.getId())).toList();
	}
	
	
	public static void main(String[] args) {
		new MTGJsonSealedProvider().getItemsFor("ICE").forEach(p->{
			System.out.println(p);
		});
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

	

}
