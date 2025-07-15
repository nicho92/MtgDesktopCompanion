package org.magic.game.model.factories;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.enums.EnumCardsPatterns;
import org.magic.game.model.costs.ActionCost;
import org.magic.game.model.costs.Cost;
import org.magic.game.model.costs.EnergyCost;
import org.magic.game.model.costs.LifeCost;
import org.magic.game.model.costs.ManaCost;
import org.magic.game.model.costs.TapCost;
import org.magic.game.model.costs.TapCost.DIR;

public class CostsFactory {

	private static CostsFactory inst;


	public static CostsFactory getInstance()
	{

		if(inst==null)
			inst=new CostsFactory();

		return inst;
	}

	public Cost parseCosts(String c) {

		if(c.equals("{T}"))
			return new TapCost(DIR.TAP);

		if(c.equals("{Q}"))
			return new TapCost(DIR.UNTAP);


		if(c.contains("{E}"))
			return new EnergyCost(StringUtils.countMatches(c, "{E}"));

		var p = Pattern.compile(EnumCardsPatterns.COST_LIFE_PATTERN.getPattern());
		var m=p.matcher(c);
		if(m.find())
			return new LifeCost(Integer.parseInt(m.group(1)));

		p = Pattern.compile(EnumCardsPatterns.MANA_PATTERN.getPattern());
		m = p.matcher(c);
		if(m.matches())
		{
			m=m.reset();
			var mana = new ManaCost();
			while(m.find())
				mana.add(m.group());

			return mana;
		}

		var ac = new ActionCost();
		ac.setAction(c);

		return ac;
	}



}
