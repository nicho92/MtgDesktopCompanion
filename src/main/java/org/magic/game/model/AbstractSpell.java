package org.magic.game.model;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.game.model.abilities.costs.Cost;
import org.magic.services.MTGLogger;

public abstract class AbstractSpell  implements Spell  {

	protected Logger logger = MTGLogger.getLogger(this.getClass());
	protected MagicCard card;
	protected List<Cost> costs;

	public abstract boolean isStackable();

	
	public AbstractSpell()
	{
		costs=new ArrayList<>();
		
	}
	
	public void addCost(Cost e) {
		costs.add(e);
	}
	
	public void setCost(Cost c) {
		costs.clear();
		addCost(c);
		
	}
	
	public Cost getCost()
	{
		return costs.get(0);
	}
	
	public List<Cost> getCosts() {
		return costs;
	}
	
	public void setCosts(List<Cost> costs) {
		this.costs = costs;
	}
	
	
	public MagicCard getCard() {
		return card;
	}
	
	public void setCard(MagicCard card) {
		this.card = card;
	}

	public abstract void actionPerformed(ActionEvent e);
}
