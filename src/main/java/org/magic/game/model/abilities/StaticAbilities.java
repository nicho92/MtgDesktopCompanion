package org.magic.game.model.abilities;

import org.magic.api.beans.MTGKeyWord;
import org.magic.game.interfaces.AbstractAbilities;

@Deprecated
public class StaticAbilities extends AbstractAbilities {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private MTGKeyWord key;

	@Deprecated
	@Override
	public boolean isStatic() {
		return true;
	}

	@Deprecated
	public void init(MTGKeyWord key) {
		this.key = key;
	}

	@Deprecated
	@Override
	public String toString() {
		return "\nSTATIC: " + key.getKeyword();
	}

	@Deprecated
	public StaticAbilities(MTGKeyWord k) {
		init(k);
	}

}
