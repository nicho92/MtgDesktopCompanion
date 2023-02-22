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
	
	
}
