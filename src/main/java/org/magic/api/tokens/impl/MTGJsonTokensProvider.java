package org.magic.api.tokens.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumLayout;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractTokensProvider;
import org.magic.api.interfaces.abstracts.extra.AbstractMTGJsonProvider;
import org.magic.api.pictures.impl.ScryFallPicturesProvider;
import org.magic.services.network.URLTools;
import org.magic.services.tools.MTG;

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
	public List<MTGCard> listTokensFor(MTGEdition ed) throws IOException {
		return prov.listToken(ed);
	}

	@Override
	public MTGCard generateTokenFor(MTGCard mc) throws IOException {
		return prov.getTokenFor(mc,EnumLayout.TOKEN);
	}

	@Override
	public MTGCard generateEmblemFor(MTGCard mc) throws IOException {
		return prov.getTokenFor(mc,EnumLayout.EMBLEM);
	}

	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("PROVIDER",new MTGProperty("MTGSQLive","The MTGJson provider name.","MTGSQLive","MTGJson5"));
	}

	@Override
	public BufferedImage getPictures(MTGCard tok) throws IOException {
		String url = new ScryFallPicturesProvider().generateUrl(tok);
		return URLTools.extractAsImage(url);
	}

	@Override
	public String getName() {
		return "MTGJson";
	}

}
