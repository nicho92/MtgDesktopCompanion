package org.magic.game.model.costs;
@Deprecated
public class LoyaltyCost extends NumberCost {

	private static final long serialVersionUID = 1L;

	@Deprecated
	public LoyaltyCost(int value) {
		super(value);
	}

	@Deprecated
	public LoyaltyCost(String string) {
		super(string);
	}

	@Deprecated
	@Override
	public String toString() {
		if (getValue() == null) {
		    return getModifier() + "X";
		}
		return (getValue() > 0) ? "+" + getValue() : String.valueOf(getValue());
	}

	@Deprecated
	public boolean isX() {
		return getValue() == null;
	}

}
