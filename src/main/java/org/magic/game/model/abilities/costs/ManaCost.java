package org.magic.game.model.abilities.costs;

public class ManaCost extends Cost {

	private StringBuilder mcost;
	
	
	public ManaCost() {
		mcost = new StringBuilder();
	}
	
	public void setManaCost(String mana)
	{
		mcost=new StringBuilder();
		mcost.append(mana);
	}
	
	public void add(String mana, int qty)
	{
		for(int i=0;i<qty;i++)
			mcost.append("{").append(mana.toUpperCase()).append("}");
	}
	
	@Override
	public String toString() {
		return mcost.toString();
	}
}
