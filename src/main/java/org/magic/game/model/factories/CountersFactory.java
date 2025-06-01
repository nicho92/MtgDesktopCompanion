package org.magic.game.model.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.enums.EnumCardsPatterns;
import org.magic.game.model.counters.AbstractCounter;
import org.magic.game.model.counters.BonusCounter;
import org.magic.game.model.counters.ItemCounter;

public class CountersFactory {


	private static CountersFactory instance;

	public static CountersFactory getInstance() {
		if(instance==null)
			instance = new CountersFactory();

		return instance;
	}

	private CountersFactory() {	}

	public List<ItemCounter> createItemCounter(MTGCard mc)
	{
		
		if(mc.getText()==null)
			return new ArrayList<>();
		
		ArrayList<ItemCounter> arr = new ArrayList<>();
		var p = Pattern.compile(EnumCardsPatterns.COUNTERS.getPattern());
		var m = p.matcher(mc.getText());
		while(m.find())
		{
			String value = m.group(2);
			if(!value.contains("/"))
			{
				var bonus = new ItemCounter(value);
				arr.add(bonus);
			}
		}

		return arr;
	}

	public List<AbstractCounter> createCounter(MTGCard mc)
	{
		ArrayList<AbstractCounter> arr = new ArrayList<>();

		var p = Pattern.compile(EnumCardsPatterns.COUNTERS.getPattern());
		var m = p.matcher(mc.getText());
			while(m.find())
			{
				String value = m.group(2);
				if(value.contains("/"))
				{
					String[] splitedValue = value.split("/");
					var power = Integer.parseInt(splitedValue[0].replace("\\+", ""));
					var toughness = Integer.parseInt(splitedValue[1].replace("\\+", ""));
					var bonus = new BonusCounter(power, toughness);
					arr.add(bonus);
				}
				else
				{
					var item = new ItemCounter(value);
					arr.add(item);
				}
			}

		return arr;

	}

}
