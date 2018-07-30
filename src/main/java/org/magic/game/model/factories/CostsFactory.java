package org.magic.game.model.factories;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.magic.game.model.costs.ActionCost;
import org.magic.game.model.costs.Cost;
import org.magic.game.model.costs.EnergyCost;
import org.magic.game.model.costs.LifeCost;
import org.magic.game.model.costs.ManaCost;
import org.magic.game.model.costs.TapCost;
import org.magic.tools.MTGOraclePatterns;

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
			return new TapCost();
		
		if(c.contains("{E}"))
			return new EnergyCost(StringUtils.countMatches(c, "{E}"));
		
		////////////////
		Pattern p = Pattern.compile(MTGOraclePatterns.COST_LIFE_PATTERN.getPattern());
		Matcher m=p.matcher(c);
		if(m.find())
			return new LifeCost(Integer.parseInt(m.group(1)));
		
		////////////////		
		p = Pattern.compile(MTGOraclePatterns.MANA_PATTERN.getPattern());
		m = p.matcher(c);
		if(m.matches()) 
		{
			m=m.reset();
			ManaCost mana = new ManaCost();
			while(m.find())
			{
					mana.add(m.group());
			}
			return mana;
		}
		
	
		////////////////		
		ActionCost ac = new ActionCost();
		ac.setAction(c);
		
		return ac;
	}


	
}
