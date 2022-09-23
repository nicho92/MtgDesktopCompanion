package org.magic.game.model.counters;

import javax.swing.JOptionPane;

import org.magic.game.gui.components.DisplayableCard;
import org.magic.game.model.abilities.LoyaltyAbilities;
import org.magic.game.model.costs.LoyaltyCost;

public class LoyaltyCounter extends AbstractCounter {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private LoyaltyCost value;
	private String label;

	public LoyaltyCounter(int value, String label) {
		this.value = new LoyaltyCost(value);
		this.label = label;
	}

	public LoyaltyCounter(LoyaltyAbilities la) {

		this.label=la.getEffects().get(0).getEffectDescription();
		this.value=(LoyaltyCost)la.getCost();
	}

	@Override
	public void apply(DisplayableCard displayableCard) {

		var loy = 0;
		try {
			loy = displayableCard.getMagicCard().getLoyalty();
		} catch (Exception e) {
			logger.error(e);
		}

		if(value.isX())
		{
			String x = JOptionPane.showInputDialog("X ?");
			value.setX(Integer.parseInt(x));
		}
		displayableCard.getMagicCard().setLoyalty(loy + value.getValue());

	}

	@Override
	public void remove(DisplayableCard displayableCard) {
		var loy = 0;
		try {
			loy = displayableCard.getMagicCard().getLoyalty();
		} catch (Exception e) {
			logger.error(e);
		}

		displayableCard.getMagicCard().setLoyalty(loy - value.getValue());

	}

	@Override
	public String describe() {
		return value + ": " + label;
	}


	@Override
	public int hashCode() {
		return "loyalty".hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if(obj ==null)
			return false;

		return hashCode()==obj.hashCode();
	}

}
