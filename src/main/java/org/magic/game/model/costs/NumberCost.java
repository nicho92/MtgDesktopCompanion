package org.magic.game.model.costs;

import org.magic.game.interfaces.Cost;
@Deprecated
public abstract class NumberCost implements Cost {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@Deprecated
	protected Integer value;
	private String modifier;

	@Deprecated
	public Integer getValue() {
		return value;
	}

	@Deprecated
	@Override
	public boolean isNumberCost() {
		return true;
	}

	@Deprecated
	protected NumberCost(int value) {
		super();
		setValue(value);
	}

	@Deprecated
	protected NumberCost(String value) {
		super();
		modifier = value.substring(0, 1);
	}

	@Deprecated
	public void setX(Integer x) {
		if (modifier.equals("-")) {
		    setValue(-x);
		} else {
		    setValue(x);
		}
	}

	@Deprecated
	public void setValue(Integer value) {
		this.value = value;

		if (value > 0) {
		    modifier = "+";
		} else if (value < 0) {
		    modifier = "-";
		} else if (value == 0) {
		    modifier = "";
		}
	}
	@Deprecated
	public String getModifier() {
		return modifier;
	}

}
