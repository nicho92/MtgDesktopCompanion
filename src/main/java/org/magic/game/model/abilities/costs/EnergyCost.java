package org.magic.game.model.abilities.costs;

public class EnergyCost extends NumberCost {

	private String text="{E}"; 
	
	public EnergyCost(int qty)
	{
		super(qty);
	}
	
	
	@Override
	public String toString() {
		StringBuilder append = new StringBuilder();
		
		for(int i=0;i<value;i++)
			append.append(text);

		return append.toString();
	}
	
}
