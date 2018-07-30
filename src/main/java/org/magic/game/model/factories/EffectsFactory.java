package org.magic.game.model.factories;

import java.util.ArrayList;
import java.util.List;

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
	
	
	public List<AbstractEffect> parseEffect(List<String> sentences)
	{
		ArrayList<AbstractEffect> arr = new ArrayList<>();
		
		if(sentences.isEmpty())
			return arr;
		
		if(sentences.size()==1)
		{
			arr.add(parseEffect(sentences.get(0)));
		}
		else
		{
			for(int i=0;i<sentences.size();i++)
			{
				arr.add(parseEffect(sentences.get(i)));
				
				if(sentences.get(i).endsWith("."))
					break;
			}
		}
			
			
		return arr;
		
	}
	
	public AbstractEffect parseEffect(String s)
	{
			OneShotEffect e = new OneShotEffect();
			e.setEffectDescription(s);
			return e;
	}
	
}
