package org.magic.game.tokens;

import org.magic.api.beans.MagicCard;

public interface IToken {

	public enum TYPE_TOKEN { LOYALITY_TOKEN, COUNTER_TOKEN,CREATURE_TOKEN};
	
	public abstract TYPE_TOKEN getType(); 
	
	public abstract void setModificator(int strength,int toughness);
	public abstract void setLoyalty(int loyalty);
	public abstract MagicCard creature(String name, String color, int s, int t, String text);
	
}
