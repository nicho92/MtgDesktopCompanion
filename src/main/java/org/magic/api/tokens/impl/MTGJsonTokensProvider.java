package org.magic.api.tokens.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGPictureProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGJsonProvider;
import org.magic.api.interfaces.abstracts.AbstractTokensProvider;
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
			prov = (AbstractMTGJsonProvider)MTG.getPlugin("MTGSQLive", MTGCardsProvider.class);
		}
	}
	
	
	@Override
	public MagicCard generateTokenFor(MagicCard mc) {
		try {
			return prov.listToken(mc.getCurrentSet()).stream().filter(tok->tok.getRotatedCard().getName().equals(mc.getName()) && tok.getLayout()==MTGLayout.TOKEN).findFirst().orElse(null);
		} catch (IOException e) {
			logger.error(e);
			return null;
		}
	}

	@Override
	public MagicCard generateEmblemFor(MagicCard mc) throws IOException {
		try {
			return prov.listToken(mc.getCurrentSet()).stream().filter(tok->tok.getRotatedCard().getName().equals(mc.getName()) && tok.getLayout()==MTGLayout.EMBLEM).findFirst().orElse(null);
		} catch (IOException e) {
			logger.error(e);
			return null;
		}
	}

	@Override
	public BufferedImage getPictures(MagicCard tok) throws IOException {
		return MTG.getEnabledPlugin(MTGPictureProvider.class).getPicture(tok);
	}

	@Override
	public String getName() {
		return "MTGJson";
	}

}
