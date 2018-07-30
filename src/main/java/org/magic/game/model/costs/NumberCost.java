package org.magic.game.model.costs;

public abstract class NumberCost extends Cost {

	protected Integer value;
	protected String modifier;
	protected String x;
	
	public Integer getValue() {
		return value;
	}
	
	public NumberCost(int value) {
		super();
		setValue(value);
	}

	public NumberCost(String value) {
		super();
		modifier=value.substring(0, 1);
	}

	public void setX(Integer x)
	{
		setValue(x);
	}
	
	public void setValue(Integer value) {
		this.value = value;

		if(value>0)
			modifier="+";
		else if(value<0)
			modifier="-";
		else if(value==0)
			modifier="";
	}
	public String getModifier() {
		return modifier;
	}

	
	
}
