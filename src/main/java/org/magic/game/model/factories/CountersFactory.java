package org.magic.game.model.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.magic.api.beans.MagicCard;
import org.magic.game.model.abilities.LoyaltyAbilities;
import org.magic.game.model.counters.AbstractCounter;
import org.magic.game.model.counters.BonusCounter;
import org.magic.game.model.counters.ItemCounter;
import org.magic.game.model.counters.LoyaltyCounter;
import org.magic.game.model.effects.AbstractEffect;
import org.magic.game.model.effects.OneShotEffect;
import org.magic.tools.OracleCardsPatterns;

public class CountersFactory {

	
	private static CountersFactory instance;
	
	public static CountersFactory getInstance() {
		if(instance==null)
			instance = new CountersFactory();
	
		return instance;
	}
	
	private CountersFactory() {	}
	
	
	public List<AbstractCounter> createCounter(String text)
	{
		MagicCard mc = new MagicCard();
		mc.setText(text);
		return createCounter(mc);
	}
	
	public List<ItemCounter> createItemCounter(MagicCard mc)
	{
		ArrayList<ItemCounter> arr = new ArrayList<>();
		Pattern p = Pattern.compile(OracleCardsPatterns.COUNTERS.getPattern());
		Matcher m = p.matcher(mc.getText());
		while(m.find())
		{
			String value = m.group(2);
			if(!value.contains("/"))
			{
				ItemCounter bonus = new ItemCounter(value);
				arr.add(bonus);
			}
		}
		
		return arr;
	}
	
	
	public LoyaltyCounter createCounter(LoyaltyAbilities la)
	{
		return new LoyaltyCounter(la);
	}
	
	public List<AbstractCounter> createCounter(MagicCard mc)
	{
		ArrayList<AbstractCounter> arr = new ArrayList<>();
		
		Pattern p = Pattern.compile(OracleCardsPatterns.COUNTERS.getPattern());
			Matcher m = p.matcher(mc.getText());
			while(m.find())
			{
				String value = m.group(2);
				if(value.contains("/"))
				{
					String[] splitedValue = value.split("/");
					int power = Integer.parseInt(splitedValue[0].replaceAll("\\+", ""));
					int toughness = Integer.parseInt(splitedValue[1].replaceAll("\\+", ""));
					BonusCounter bonus = new BonusCounter(power, toughness);
					arr.add(bonus);
				}
				else
				{
					ItemCounter item = new ItemCounter(value);
					arr.add(item);
				}
			}
			
		return arr;
		
	}
	
}
