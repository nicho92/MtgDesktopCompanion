package org.magic.api.wallpaper.impl;

import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Wallpaper;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractWallpaperProvider;

public class WizardsOfTheCoastWallpaperProvider extends AbstractWallpaperProvider {

	@Override
	public List<Wallpaper> search(String search) {
		String url ="https://magic.wizards.com/en/see-more-wallpaper?page=1&filter_by=DESC&artist=-1&expansion=&title="+search+"&is_search=1";
		return new ArrayList<>();
	}

	@Override
	public List<Wallpaper> search(MagicEdition ed) {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

	@Override
	public List<Wallpaper> search(MagicCard card) {
		return search(card.getName());
	}

	@Override
	public String getName() {
		return "Wizard Of The Coast";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

}
