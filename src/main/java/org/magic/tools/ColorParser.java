package org.magic.tools;

import java.awt.Color;
import java.util.List;



public class ColorParser {

	private ColorParser() {
	}

	
	public static String getCodeByName(List<String> fullColorNames,boolean bracket) {
		
		if(fullColorNames.isEmpty())
			return getCodeByName("",bracket);
		
		StringBuilder build = new StringBuilder();
		for(String c : fullColorNames)
			build.append(getCodeByName(c, bracket));
		
		return build.toString();
		
	}
	
	
	public static String getCodeByName(String fullColorName,boolean bracket) {
		
		String bracketO="";
		String bracketC="";
		
		if(bracket)
		{
			bracketO="{";
			bracketC="}";
		}

		if (fullColorName.equalsIgnoreCase("white"))
			return bracketO+"W"+bracketC;
		else if (fullColorName.equalsIgnoreCase("blue"))
			return bracketO+"U"+bracketC;
		else if (fullColorName.equalsIgnoreCase("black"))
			return bracketO+"B"+bracketC;
		else if (fullColorName.equalsIgnoreCase("red"))
			return bracketO+"R"+bracketC;
		else if (fullColorName.equalsIgnoreCase("green"))
			return bracketO+"G"+bracketC;

		return bracketO+"C"+bracketC;
	}
	
	public static Color getColorByCode(List<String> manas)
	{
		
		if(manas.size()>1)
			return Color.YELLOW;
		
		if(manas.isEmpty())
			return new Color(139,69,19);
			
		if(manas.get(0).contains("U"))
			return Color.BLUE;
		else if (manas.get(0).equalsIgnoreCase("B"))
			return Color.BLACK;
		else if (manas.get(0).equalsIgnoreCase("R"))
			return Color.RED;
		else if (manas.get(0).equalsIgnoreCase("G"))
			return Color.GREEN;
		
		return Color.WHITE;
	}
	
	public static Color getColorByName(List<String> fullColorName)
	{
		
		if(fullColorName.size()>1)
			return Color.YELLOW;
		
		if(fullColorName.isEmpty())
			return new Color(139,69,19);
			
			
		if(fullColorName.get(0).equalsIgnoreCase("blue"))
			return Color.BLUE;
		else if (fullColorName.get(0).equalsIgnoreCase("black"))
			return Color.BLACK;
		else if (fullColorName.get(0).equalsIgnoreCase("red"))
			return Color.RED;
		else if (fullColorName.get(0).equalsIgnoreCase("green"))
			return Color.GREEN;
		
		return Color.WHITE;
	}
	
	
	public static String getNameByCode(String code) {
		if (code == null)
			return "";

		if (code.toLowerCase().contains("w"))
			return "White";
		else if (code.toLowerCase().contains("u"))
			return "Blue";
		else if (code.toLowerCase().contains("b"))
			return "Black";
		else if (code.toLowerCase().contains("r"))
			return "Red";
		else if (code.toLowerCase().contains("g"))
			return "Green";

		return "";
	}



}
