package org.magic.game.model.costs;

public class LoyaltyCost extends NumberCost {

	private static final long serialVersionUID = 1L;

	public LoyaltyCost(int value) {
		super(value);
	}

	public LoyaltyCost(String string) {
		super(string);
	}

	@Override
	public String toString()
	{
		if(getValue()==null)
			return getModifier()+"X";
		return (getValue()>0) ? "+"+getValue(): String.valueOf(getValue());
	}

	public boolean isX() {
		return getValue()==null;
	}


}
