package org.magic.game.model.costs;

import org.magic.game.interfaces.Cost;
@Deprecated
public class TapCost implements Cost {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public enum DIR {
		TAP, UNTAP
	}

	private final DIR t;

	@Deprecated
	public TapCost(DIR t) {
		this.t = t;
	}

	@Deprecated
	public TapCost() {
		this.t = DIR.TAP;
	}

	@Deprecated
	@Override
	public String toString() {
		return t.name();
	}

}
