package org.magic.game.model.factories;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import org.magic.api.beans.MTGKeyWord.EVENT;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.enums.EnumCardsPatterns;
import org.magic.game.model.abilities.AbstractAbilities;
import org.magic.game.model.abilities.ActivatedAbilities;
import org.magic.game.model.abilities.LoyaltyAbilities;
import org.magic.game.model.abilities.StaticAbilities;
import org.magic.game.model.abilities.TriggeredAbilities;
import org.magic.game.model.abilities.TriggeredAbilities.KEYWORDS;
import org.magic.game.model.costs.LoyaltyCost;
import org.magic.services.keywords.AbstractKeyWordsManager;

public class AbilitiesFactory implements Serializable{

	private static final long serialVersionUID = 1L;
	private static AbilitiesFactory inst;

	public static AbilitiesFactory getInstance()
	{

		if(inst==null)
			inst=new AbilitiesFactory();

		return inst;
	}


	private AbilitiesFactory() {

	}

	private List<String> listSentences(MagicCard mc)
	{
		if(mc.getText()==null)
		{
			return new ArrayList<>();
		}
		
		return Arrays.asList(mc.getText().split("\n"));
	}

	public List<AbstractAbilities> getAbilities(MagicCard mc)
	{

		mc.setText(removeParenthesis(mc.getText()));
		List<AbstractAbilities> ret = new ArrayList<>();
		ret.addAll(getActivatedAbilities(mc));
		ret.addAll(getLoyaltyAbilities(mc));
		ret.addAll(getTriggeredAbility(mc));
		ret.addAll(getStaticAbilities(mc));
		return ret;
	}

	private String removeParenthesis(String text)
	{
		return text.replaceAll(EnumCardsPatterns.REMINDER.getPattern(),"");
	}


	public List<AbstractAbilities> getActivatedAbilities(MagicCard mc) {
		List<AbstractAbilities> ret = new ArrayList<>();
		if(!mc.isPlaneswalker())
		{
			for(String s : listSentences(mc))
			{
				int end = s.indexOf('.');

				if(s.indexOf(':')>1 && s.indexOf(':')<end)
				{
					String[] costs = s.substring(0,s.indexOf(':')).split(",");
					var abs = new ActivatedAbilities();
					abs.setCard(mc);

					for(String c : costs)
						abs.addCost(CostsFactory.getInstance().parseCosts(c.trim()));

					abs.addEffect(EffectsFactory.getInstance().parseEffect(mc,s.substring(s.indexOf(':')+1)));


					ret.add(abs);

				}
			}
		}
		return ret;
	}

	public List<LoyaltyAbilities> getLoyaltyAbilities(MagicCard mc) {
		List<LoyaltyAbilities> list = new ArrayList<>();
		if(mc.isPlaneswalker())
		{

			for(String s : listSentences(mc))
			{

				Matcher m  = EnumCardsPatterns.extract(s, EnumCardsPatterns.LOYALTY_PATTERN);
				if(m.matches()) {

				var abilities = new LoyaltyAbilities();
				abilities.setCard(mc);
				String c = m.group(1);
				if(c.startsWith("+"))
				{
					try{
						abilities.setCost(new LoyaltyCost(Integer.parseInt(c.substring(1))));
					}
					catch(Exception e)
					{
						abilities.setCost(new LoyaltyCost("+"));
					}
				}
				else if(c.startsWith("0"))
				{
					abilities.setCost(new LoyaltyCost(0));
				}
				else
				{
					try{
						abilities.setCost(new LoyaltyCost(-Integer.parseInt(c.substring(1))));
					}
					catch(Exception e)
					{
						abilities.setCost(new LoyaltyCost("-"));
					}
				}
				abilities.addEffect(EffectsFactory.getInstance().parseEffect(mc,m.group(2)));
				list.add(abilities);
				}
			}
		}
		return list;

	}

	public List<StaticAbilities> getStaticAbilities(MagicCard mc) {
		return AbstractKeyWordsManager.getInstance().getKeywordsFrom(mc, EVENT.STATIC).stream().map(StaticAbilities::new).toList();
	}

	public List<TriggeredAbilities> getTriggeredAbility(MagicCard mc)
	{
		List<TriggeredAbilities> arr =new ArrayList<>();
		var i=0;
		for(String s : listSentences(mc))
		{
			for(KEYWORDS k : TriggeredAbilities.KEYWORDS.values())
			{
				if(s.split(" ")[0].equalsIgnoreCase(k.name()))
				{
					var t = new TriggeredAbilities();
									   t.setCard(mc);
									   t.setCost(null);
									   t.init(k,listSentences(mc).subList(i, listSentences(mc).size()));

					arr.add(t);
				}
			}
			i++;
		}
		return arr;
	}


}
