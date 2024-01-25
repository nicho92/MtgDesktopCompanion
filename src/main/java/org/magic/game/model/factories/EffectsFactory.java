package org.magic.game.model.factories;

import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.game.model.effects.AbstractEffect;
import org.magic.game.model.effects.OneShotEffect;

public class EffectsFactory {


	private static EffectsFactory instance;

	public static EffectsFactory getInstance() {
		if(instance==null)
			instance = new EffectsFactory();

		return instance;
	}

	private EffectsFactory() {	}


	public List<AbstractEffect> parseEffect(MTGCard mc, List<String> sentences)
	{
		ArrayList<AbstractEffect> arr = new ArrayList<>();

		if(sentences.isEmpty())
			return arr;

		if(sentences.size()==1)
		{
			arr.add(parseEffect(mc,sentences.get(0)));
		}
		else
		{
			for (String element : sentences) {
				arr.add(parseEffect(mc,element));

				if(element.endsWith("."))
					break;
			}
		}


		return arr;

	}

	public AbstractEffect parseEffect(MTGCard mc,String s)
	{
		var e = new OneShotEffect();
			e.setEffectDescription(s);
			e.setCard(mc);
			return e;
	}

}
