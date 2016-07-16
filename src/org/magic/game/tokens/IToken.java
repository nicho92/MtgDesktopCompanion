package org.magic.game.tokens;

public interface IToken {

	public enum TYPE_TOKEN { LOYALITY_TOKEN, COUNTER_TOKEN,CREATURE_TOKEN};
	
	public abstract TYPE_TOKEN getType(); 
	
	
}
