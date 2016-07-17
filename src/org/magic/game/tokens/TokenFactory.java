package org.magic.game.tokens;

import org.magic.api.analyzer.TokenAnalyzer;
import org.magic.api.beans.MagicCard;

public class TokenFactory {

	
	public MagicCard analyseText(MagicCard mc) throws Exception
	{
		return TokenAnalyzer.generateTokenFrom(mc);
	}
}
