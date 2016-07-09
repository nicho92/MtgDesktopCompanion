package org.magic.game.tokens;

import org.magic.api.beans.MagicCard;

public class ModifierToken implements IToken {

	@Override
	public TYPE_TOKEN getType() {
		return TYPE_TOKEN.COUNTER_TOKEN;
	}

	@Override
	public void setModificator(int strength, int toughness) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLoyalty(int loyalty) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MagicCard creature(String name, String color, int s, int t, String text) {
		// TODO Auto-generated method stub
		return null;
	}

		
	
}
