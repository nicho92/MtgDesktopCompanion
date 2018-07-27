package org.magic.game.model.factories;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.jdesktop.beansbinding.AbstractBindingListener;
import org.magic.api.beans.MagicCard;
import org.magic.game.model.AbstractSpell;
import org.magic.game.model.abilities.AbstractAbilities;
import org.magic.game.model.abilities.ManaAbilities;
import org.magic.game.model.abilities.StaticAbilities;
import org.magic.game.model.abilities.TriggeredAbilities;
import org.magic.game.model.abilities.TriggeredAbilities.KEYWORDS;
import org.magic.game.model.abilities.effects.Effect;

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
	
	
	private List<String> listSentences(MagicCard mc)
	{
		List<String> arr = new ArrayList<>();
		bi.setText(mc.getText());
		int lastIndex = bi.first();
		while (lastIndex != BreakIterator.DONE) {
			int firstIndex = lastIndex;
            lastIndex = bi.next();
            if (lastIndex != BreakIterator.DONE) {
            	arr.add(mc.getText().substring(firstIndex, lastIndex));
            }
		}
		return arr;
	}
	
	public List<AbstractAbilities> getAbilities(MagicCard mc)
	{
		List<AbstractAbilities> ret = new ArrayList<>();
		ret.add(getTriggeredAbility(mc));
		ret.addAll(parseManaAbilities(mc));
		ret.addAll(parseStaticAbilities(mc));
		
		return ret;
	}
	
	
	
	private List<ManaAbilities> parseManaAbilities(MagicCard mc) {
		return new ArrayList<>();
	}


	private List<StaticAbilities> parseStaticAbilities(MagicCard mc) {
		return new ArrayList<>();
	}


	public TriggeredAbilities getTriggeredAbility(MagicCard mc)
	{
		for(String s : listSentences(mc))
		{
			for(KEYWORDS k : TriggeredAbilities.KEYWORDS.values())
				if(s.toUpperCase().startsWith(k.name()))
				{
					TriggeredAbilities t = new TriggeredAbilities();
									   t.setCard(mc);
									   t.setCost(null);
									   t.init(k,s);
					return t;
				}
				
		}
		return null;
	}

	
}
