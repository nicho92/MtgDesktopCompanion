package org.magic.api.analyzer;

import org.apache.commons.lang3.text.WordUtils;
import org.magic.api.beans.MagicCard;
import org.magic.tools.ColorParser;

public class CardAnalyser {

	public static MagicCard generateEmblemFrom(MagicCard mc) throws Exception
	{
		MagicCard emblem = new MagicCard();
		
		emblem.setLegalities(mc.getLegalities());
		emblem.setMultiverseid(Integer.valueOf(mc.getEditions().get(0).getMultiverse_id()));
		emblem.setEditions(mc.getEditions());
		emblem.setFlavor("");
		emblem.setArtist("MTG Desktop Companion");		
		emblem.setWatermarks("");
		emblem.setLayout(MagicCard.LAYOUT.Emblem.toString());
		emblem.setNumber("E");
		emblem.setRarity("Common");
		emblem.setCmc(0);
		emblem.getTypes().add("Emblem");
		emblem.getSubtypes().add(mc.getName());
		emblem.setCost("");
		emblem.getColors().addAll(mc.getColors());
		emblem.setName(mc.getName());
		
		String text = mc.getText();
		
		int start = text.indexOf(" an emblem with \"")+" an emblem with \"".length();
		int end = text.indexOf("\"", start);
		emblem.setText(text.substring(start, end));
		
		return emblem;
		
	}
	
	
	
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
			else if (artifact>-1)
				token.setName(text.substring(endColor, artifact).trim());
			else
				token.setName(text.substring(endColor, creature).trim());
			
			token.setName(WordUtils.capitalize(token.getName()));
	
		
				if(artifact>-1)
					token.getSubtypes().add(text.substring(endColor, artifact).trim());
				else
					token.getSubtypes().add(text.substring(endColor, creature).trim());
				
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
				token.setLayout(MagicCard.LAYOUT.Token.toString());

				
				if(artifact>-1)
					token.getTypes().add("Artifact");
		
				if(creature>-1)
					token.getTypes().add("Creature");
				
				if(text.substring(0,slash).contains("legendary"))
					token.getSupertypes().add("Legendary");
				
				token.getSupertypes().add("Token");
				
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
