package org.magic.api.analyzer;

import org.magic.api.beans.MagicCard;
import org.magic.tools.ColorParser;

public class TokenAnalyzer {

	public static MagicCard generateTokenFrom(MagicCard mc) throws Exception
	{
		
		MagicCard token = new MagicCard();
		
		
		String text = mc.getText();
		
		text=text.toLowerCase();
		
		int put = text.indexOf("put");
		
		
		
		text=text.substring(put);
		text=text.substring(0,text.indexOf("."));
		
		
		
		int with = text.indexOf("with");//could be -1
		int onto = text.indexOf("onto the");
		int slash = text.indexOf("/");
		int tokens = text.indexOf("token");
		int artifact = text.indexOf("artifact"); // could be -1
		int creature = text.indexOf("creature");
		int named=text.indexOf("named");
		
		int startColor = slash+1+text.substring(slash,slash+3).trim().length();
		int endColor=0;
		
		if(text.substring(startColor,creature).contains("and"))//can manage 2 color max;
		{
			String colors[]=text.substring(startColor,creature).split("and");
			token.getColors().add(colors[0].trim());
			token.getColors().add(colors[1].trim().substring(0,colors[1].trim().indexOf(" ")).trim());
			endColor=startColor+(colors[0].trim()+" and " + colors[1].substring(0,colors[1].trim().indexOf(" "))).length();
			
		}
		else
		{
			String color =text.substring(startColor,creature).substring(0,text.substring(startColor,creature).indexOf(" "));
			
			endColor=startColor+color.length();
			
			token.getColors().add(color);
		}
		
		
			if(named>-1)
				token.setName(text.substring(named+5,onto).trim());
			else
				token.setName(text.substring(endColor, creature).trim());
			
		
	
		
				if(artifact>-1)
					token.getSubtypes().add(text.substring(endColor, artifact).trim());
				else
					token.getSubtypes().add(text.substring(endColor, creature).trim());
				token.getSubtypes().add("Token");
				
				token.setCost("");
				for(String c : token.getColors())
					token.setCost(token.getCost()+ColorParser.parse(c));
				
				
				token.setNumber("T");
				token.setRarity("Common");
				token.setCmc(0);
				
				for(String c : token.getColors())
					token.getColorIdentity().add(ColorParser.parse(c));
				
				token.setLegalities(mc.getLegalities());
				token.setMultiverseid(Integer.valueOf(mc.getEditions().get(0).getMultiverse_id()));
				token.setEditions(mc.getEditions());
				token.setFlavor("");
				token.setArtist("MTG Desktop Companion");		
				token.setWatermarks("");
				token.setToken(true);
				
				if(artifact>-1)
					token.getTypes().add("Artifact");
		
				if(creature>-1)
					token.getTypes().add("Creature");
				
				if(text.substring(0,slash).contains("legendary"))
					token.getSupertypes().add("Legendary");
				
				
				if(with>-1)
				{
					if(named==-1)
						token.setText(text.substring(with+4,onto).trim().replaceAll("and", ","));
					else
						token.setText(text.substring(with+4,named).trim().replaceAll("and", ","));
				}
				else
				{
					token.setText("");
				}
				
				if(slash>-1)
				{
					token.setPower(text.substring(slash-2,slash).trim());
					token.setToughness(text.substring(slash+1,slash+3).trim());
				}
		return token;
	}
}
