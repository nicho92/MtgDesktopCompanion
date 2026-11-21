package org.magic.game.model.costs;
@Deprecated
public class LifeCost extends NumberCost {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Deprecated
	public LifeCost(int qty) {
		super(qty);
	}

	@Deprecated
	@Override
	public String toString() {

		if (value > 1) {
		    return value + " lifes";
		}

		return value + " life";
	}
}
