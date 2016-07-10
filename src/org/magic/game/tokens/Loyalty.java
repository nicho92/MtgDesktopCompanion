package org.magic.game.tokens;

import org.magic.api.beans.MagicCard;

public class Loyalty implements IToken {

	@Override
	public TYPE_TOKEN getType() {
		return TYPE_TOKEN.LOYALITY_TOKEN;
	}

	
}
