package org.magic.api.beans.enums;

public enum EnumItems
{
	SEALED ("Sealed Product"), 
	CARD ("Card"),
	BOX ("Booster box"),
	BOOSTER ("Booster"),
	LOTS ("Packs of cards"),
	BUNDLE ("Bundle"),
	STARTER ("Starter"),
	SET ("Full Collection"),
	CONSTRUCTPACK ("Preconstruct deck pack"),
	PRERELEASEPACK ("Pre release pack"),
	CHALLENGERDECK ("Challenger deck"),
	FATPACK ("Fat Pack"),
	DECK ("Deck"),  
	COMMANDER_DECK ("Commander Deck"),
	CASE ("Case of Sealed product"), 
	DRAFT_PACK ("3 boosters pack"),
	WELCOME ("Welcome pack");
	
	
	private String label;

	private EnumItems(String label) {
		this.label=label;
	}
	
	@Override
	public String toString() {
		return label;
	}
	
	public String getLabel() {
		return label;
	}
	
	
	public static EnumItems parseByLabel(String s)
	{
		for(var e : values())
		{
			if(e.getLabel().equalsIgnoreCase(s))
				return e;
		}
		
		return null;
		
	}
	
	
	
}
