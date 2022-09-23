package org.magic.game.model.costs;

public class LifeCost extends NumberCost {


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;



	public LifeCost(int qty) {
		super(qty);
	}



	@Override
	public String toString() {

		if(value>1)
			return value + " lifes";


		return value + " life";
	}
}
