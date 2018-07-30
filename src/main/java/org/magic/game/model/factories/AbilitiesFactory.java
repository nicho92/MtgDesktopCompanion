package org.magic.game.model.factories;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.MTGKeyWord;
import org.magic.api.beans.MTGKeyWord.EVENT;
import org.magic.api.beans.MTGKeyWord.SUBTYPE;
import org.magic.api.beans.MTGKeyWord.TYPE;
import org.magic.api.pricers.impl.MagicVillePricer;
import org.magic.api.beans.MagicCard;
import org.magic.game.model.abilities.AbstractAbilities;
import org.magic.game.model.abilities.ActivatedAbilities;
import org.magic.game.model.abilities.LoyaltyAbilities;
import org.magic.game.model.abilities.ManaAbilities;
import org.magic.game.model.abilities.StaticAbilities;
import org.magic.game.model.abilities.TriggeredAbilities;
import org.magic.game.model.abilities.TriggeredAbilities.KEYWORDS;
import org.magic.game.model.abilities.costs.ActionCost;
import org.magic.game.model.abilities.costs.Cost;
import org.magic.game.model.abilities.costs.EnergyCost;
import org.magic.game.model.abilities.costs.LifeCost;
import org.magic.game.model.abilities.costs.LoyaltyCost;
import org.magic.game.model.abilities.costs.ManaCost;
import org.magic.game.model.abilities.costs.TapCost;
import org.magic.services.MTGControler;
import org.magic.tools.MTGOraclePatterns;

public class AbilitiesFactory {

	private BreakIterator bi;
	private static AbilitiesFactory inst;
	
	
	public static AbilitiesFactory getInstance()
	{
		
		if(inst==null)
			inst=new AbilitiesFactory();
		
		return inst;
	}
	
	
	private AbilitiesFactory() {
		bi = BreakIterator.getSentenceInstance(Locale.US);
	}
	
//	private List<String> listSentences(String s)
//	{
//		List<String> arr = new ArrayList<>();
//		
//		bi.setText(s);
//		int lastIndex = bi.first();
//		while (lastIndex != BreakIterator.DONE) {
//			int firstIndex = lastIndex;
//            lastIndex = bi.next();
//            if (lastIndex != BreakIterator.DONE) {
//            	String s2 = s.substring(firstIndex, lastIndex);
//            	
//            	if(!s2.startsWith("("))
//            		arr.add(s2);
//            }
//		}
//		return arr;
//	}
//	
	
	
	private List<String> listSentences(MagicCard mc)
	{
		List<String> arr = new ArrayList<>();
		for(String s :  mc.getText().split("\n"))
		{
//			if(s.indexOf('.')<s.length())
//				arr.addAll(listSentences(s));
//			else
				arr.add(s);
		}
		
		return arr;
	}
	
	public List<AbstractAbilities> getAbilities(MagicCard mc)
	{
		
		mc.setText(removeParenthesis(mc.getText()));
		List<AbstractAbilities> ret = new ArrayList<>();
		ret.addAll(getActivatedAbilities(mc));
		ret.addAll(getLoyaltyAbilities(mc));
		ret.addAll(getTriggeredAbility(mc));
		ret.addAll(parseManaAbilities(mc));
		ret.addAll(parseStaticAbilities(mc));
		return ret;
	}
	
	private String removeParenthesis(String text)
	{
		return text.replaceAll(MTGOraclePatterns.PARENTHESES_PATTERN.getPattern(),"");
	}
	
	
	private List<ActivatedAbilities> getActivatedAbilities(MagicCard mc) {
		List<ActivatedAbilities> ret = new ArrayList<>();
	
		
		
		
		if(!mc.isPlaneswalker())
		{
			for(String s : listSentences(mc))
			{
				int end = s.indexOf('.');
				
				if(s.indexOf(':')>1 && s.indexOf(':')<end)
				{
					String[] costs = s.substring(0,s.indexOf(':')).split(",");
					ActivatedAbilities abs = new ActivatedAbilities();
					
					for(String c : costs)
					{
						abs.addCost(CostsFactory.getInstance().parseCosts(c.trim()));
					}
					
					abs.addEffect(EffectsFactory.getInstance().parseEffect(s.substring(s.indexOf(':')+1)));
					
					
					ret.add(abs);
					
				}
			}
		}
		return ret;
	}



	private List<LoyaltyAbilities> getLoyaltyAbilities(MagicCard mc) {
		
		List<LoyaltyAbilities> list = new ArrayList<>();
		if(mc.getFullType().toLowerCase().contains("planeswalker"))
		{
			for(String s : listSentences(mc))
			{
				if(s.contains(":")) {
					LoyaltyAbilities abilities = new LoyaltyAbilities();
					abilities.setCard(mc);
					
						String subs = s.substring(0,s.indexOf(':')+1);
						if(subs.startsWith("+"))
						{
							try{
								abilities.setCost(new LoyaltyCost(Integer.parseInt(subs.substring(1,subs.indexOf(':')))));
							}
							catch(Exception e)
							{
								abilities.setCost(new LoyaltyCost("+"));
							}
						}
						else if(subs.startsWith("0"))
						{
							abilities.setCost(new LoyaltyCost(0));
						}
						else
						{
							try{
								abilities.setCost(new LoyaltyCost(Integer.parseInt("-"+subs.substring(1,subs.indexOf(':')))));
							}
							catch(Exception e)
							{
								abilities.setCost(new LoyaltyCost("-"));
							}	
						}
						
						abilities.addEffect(EffectsFactory.getInstance().parseEffect(s.substring(subs.length())));
						list.add(abilities);
				}
			}
		}
		return list;
		
	}


	private List<ManaAbilities> parseManaAbilities(MagicCard mc) {
		return new ArrayList<>();
	}


	private List<StaticAbilities> parseStaticAbilities(MagicCard mc) {
		List<StaticAbilities> list = new ArrayList<>();
		
		Set<MTGKeyWord> keys= MTGControler.getInstance().getKeyWordManager().getKeywordsFrom(mc);
		
		keys.forEach(key->{
			
			if(key.getEvent().equals(EVENT.STATIC))
			{
				StaticAbilities abs = new StaticAbilities();
				abs.setCard(mc);
				abs.init(key);
				list.add(abs);
			}
		});
		
		
		return list;
	}


	public List<TriggeredAbilities> getTriggeredAbility(MagicCard mc)
	{
		List<TriggeredAbilities> arr =new ArrayList<>();
		int i=0;
		for(String s : listSentences(mc))
		{
			for(KEYWORDS k : TriggeredAbilities.KEYWORDS.values())
			{
				if(s.split(" ")[0].equalsIgnoreCase(k.name()))
				{
					TriggeredAbilities t = new TriggeredAbilities();
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
