package org.magic.services.tools;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.exports.impl.CardKingdomCardExport;
import org.magic.services.providers.PluginsAliasesProvider;





public class CardKingdomTools {

	
	
	private CardKingdomTools()
	{
		
	}
	
	public static String getCKFormattedName(MagicCard card) {
		
		String name = card.getName();
			
		if(name.contains("//") && (!card.getLayout().toString().equalsIgnoreCase(MTGLayout.SPLIT.toString())))
		{
			name = name.split(" //")[0];
		}
		
		
		if(card.isToken())
		{
			name = name + " Token";
		}
		
		name = name.replace("ú", "u");
		name = name.replace("â", "a");
		name = name.replace("á", "a");
		name = name.replace("ö", "o");
		
		
		if(card.isShowCase())
		{
			name = name + " (Showcase)";
		}
		
		
		return name;
	}
	
	
	public static String getCKFormattedSet(MagicCard card) {
		
		String set = PluginsAliasesProvider.inst().getSetNameFor(new CardKingdomCardExport(), card.getEdition());
		
		
		
		if(card.isToken())
		{
			set = set.replace(" Tokens", "");
			set = PluginsAliasesProvider.inst().getSetNameFor(new CardKingdomCardExport(), set);
		}
		
		if(card.isShowCase())
		{
			set = set + " Variants";
		}
		
		
		
		return set;
	}
	
	
	
	
	
	
	
}
