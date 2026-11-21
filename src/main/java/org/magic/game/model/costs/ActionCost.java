package org.magic.game.model.costs;

import org.magic.game.interfaces.Cost;

@Deprecated
public class ActionCost implements Cost {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String action;

	@Deprecated
	public void setAction(String c) {
		this.action = c;
	}

	@Deprecated
	public ActionCost() {
	}

	@Deprecated
	public ActionCost(String action) {
		setAction(action);
	}

	@Deprecated
	@Override
	public String toString() {
		return action;
	}

}
