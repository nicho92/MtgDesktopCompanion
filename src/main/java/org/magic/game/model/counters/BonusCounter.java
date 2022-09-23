package org.magic.game.model.counters;

import org.magic.game.gui.components.DisplayableCard;

public class BonusCounter extends AbstractCounter {


	private static final long serialVersionUID = 1L;
	int powerModifier;
	int toughnessModifier;

	public BonusCounter(int powerModifier, int toughnessModifier) {
		this.powerModifier = powerModifier;
		this.toughnessModifier = toughnessModifier;
	}

	@Override
	public void apply(DisplayableCard displayableCard) {
		var power = 0;
		var toughness = 0;

		try {
			power = Integer.parseInt(displayableCard.getMagicCard().getPower());
		} catch (Exception e) {
			power = 0;
		}

		try {
			toughness = Integer.parseInt(displayableCard.getMagicCard().getToughness());
		} catch (Exception e) {
			toughness = 0;
		}

		power = power + powerModifier;
		toughness = toughness + toughnessModifier;

		displayableCard.getMagicCard().setPower(String.valueOf(power));
		displayableCard.getMagicCard().setToughness(String.valueOf(toughness));
	}

	@Override
	public void remove(DisplayableCard displayableCard) {
		var power = 0;
		var toughness = 0;

		try {
			power = Integer.parseInt(displayableCard.getMagicCard().getPower());
		} catch (Exception e) {
			logger.error(e);
		}

		try {
			toughness = Integer.parseInt(displayableCard.getMagicCard().getToughness());
		} catch (Exception e) {
			logger.error(e);
		}

		power = power - powerModifier;
		toughness = toughness - toughnessModifier;

		displayableCard.getMagicCard().setPower(String.valueOf(power));
		displayableCard.getMagicCard().setToughness(String.valueOf(toughness));

	}

	@Override
	public String describe() {

		var build = new StringBuilder();

		if (powerModifier >= 0)
			build.append("+").append(powerModifier);
		else
			build.append(powerModifier);

		build.append("/");

		if (toughnessModifier >= 0)
			build.append("+").append(toughnessModifier);
		else
			build.append(toughnessModifier);

		build.append(" counter");
		return build.toString();

	}


	@Override
	public int hashCode() {
		return "bonus".hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if(obj ==null)
			return false;

		return hashCode()==obj.hashCode();
	}


}
