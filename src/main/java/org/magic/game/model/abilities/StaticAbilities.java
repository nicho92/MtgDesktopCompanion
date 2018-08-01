package org.magic.game.model.abilities;

import org.magic.api.beans.MTGKeyWord;

public class StaticAbilities extends AbstractAbilities {

	private MTGKeyWord key;
	
	
	@Override
	public boolean isStatic() {
		return true;
	}

	public void init(MTGKeyWord key) {
		this.key=key;
	}
	
	@Override
	public String toString() {
		return "\nSTATIC: " + key.getKeyword();
	}


	
}
