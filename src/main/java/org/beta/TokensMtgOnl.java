package org.beta;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.abstracts.AbstractTokensProvider;
import org.magic.tools.URLTools;

public class TokensMtgOnl extends AbstractTokensProvider {

	
	static String jsonStandard="http://tokens.mtg.onl/data/SetsWithTokens.json?v-142";
	static String jsonAllToken="http://mtg.onl/token-list/data/ProxyTokens.json?v=105";
	static String jsonAlternative="http://alternative.mtg.onl/data/AlternativeTokens.json?v=0.8";
	
	
	
	@Override
	public MagicCard generateTokenFor(MagicCard mc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MagicCard generateEmblemFor(MagicCard mc) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BufferedImage getPictures(MagicCard tok) throws IOException {
		
		StringBuilder uri = new StringBuilder("http://tokens.mtg.onl/tokens/");
					  uri.append(tok.getCurrentSet().getId())
					  	 .append("_")
					     .append(tok.getNumber().replaceAll(" ", "-"))
					     .append("-")
					     .append(tok.getName().replaceAll(" ", "-"))
					     .append(".jpg");
		
		
		return URLTools.extractImage(uri.toString());
	}

	@Override
	public String getName() {
		return "Tokens MTG Onl";
	}


}
