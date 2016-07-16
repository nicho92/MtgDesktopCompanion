package org.magic.game.tokens;

import org.magic.api.analyzer.TokenAnalyzer;
import org.magic.api.beans.MagicCard;

public class TokenFactory {

	
	public IToken createToken(IToken.TYPE_TOKEN type)
	{
		switch (type)
		{
		case LOYALITY_TOKEN : return new Loyalty();
		case COUNTER_TOKEN : return new Counter();
		}
		return null;
	}
	
	
	public MagicCard analyseText(MagicCard mc) throws Exception
	{
		return TokenAnalyzer.generateTokenFrom(mc);
	}
}
