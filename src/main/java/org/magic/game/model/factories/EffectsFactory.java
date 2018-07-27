package org.magic.game.model.factories;

import java.util.ArrayList;
import java.util.List;

import org.magic.game.model.abilities.effects.Effect;
import org.magic.game.model.abilities.effects.OneShotEffect;

public class EffectsFactory {

	
	private static EffectsFactory instance;
	
	public static EffectsFactory getInstance() {
		if(instance==null)
			instance = new EffectsFactory();
	
		return instance;
	}
	
	private EffectsFactory() {	}
	
	
	
	public List<Effect> parseEffect(String text)
	{
		ArrayList<Effect> arr = new ArrayList<>();
		
		OneShotEffect eff = new OneShotEffect();
		
		String[] se = text.split(", then");
		
		if(se.length>1)
		{
			eff.setEffectDescription(se[0]);
			OneShotEffect effc = new OneShotEffect();
			effc.setEffectDescription(se[1]);
			eff.setChildEffect(effc);
		}
		
		arr.add(eff);
		return arr;
		
	}
}
