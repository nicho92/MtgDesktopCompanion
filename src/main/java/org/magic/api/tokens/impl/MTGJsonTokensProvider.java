package org.magic.api.tokens.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractTokensProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractMTGJsonProvider;
import org.magic.api.pictures.impl.ScryFallPicturesProvider;
import org.magic.services.network.URLTools;
import org.magic.tools.MTG;

public class MTGJsonTokensProvider extends AbstractTokensProvider {

	
	private AbstractMTGJsonProvider prov;
	
	
	public MTGJsonTokensProvider() {
		
		if(MTG.getEnabledPlugin(MTGCardsProvider.class) instanceof AbstractMTGJsonProvider p)
		{
			this.prov = p;
		}
		else
		{
			prov = (AbstractMTGJsonProvider)MTG.getPlugin(getString("PROVIDER"), MTGCardsProvider.class);
		}
	}
	
	@Override
	public List<MagicCard> listTokensFor(MagicEdition ed) throws IOException {
		return prov.listToken(ed);
	}
	
	@Override
	public MagicCard generateTokenFor(MagicCard mc) throws IOException {
		return prov.getTokenFor(mc,MTGLayout.TOKEN);
	}

	@Override
	public MagicCard generateEmblemFor(MagicCard mc) throws IOException {
		return prov.getTokenFor(mc,MTGLayout.EMBLEM);
	}
	
	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("PROVIDER","MTGSQLive");
	}

	@Override
	public BufferedImage getPictures(MagicCard tok) throws IOException {
		String url = new ScryFallPicturesProvider().generateUrl(tok);
		return URLTools.extractAsImage(url);
	}

	@Override
	public String getName() {
		return "MTGJson";
	}

}
