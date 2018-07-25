package org.magic.tools;

import java.awt.Color;
import java.util.List;

public class ColorParser {

	private ColorParser() {
	}

	public static String parse(String fullColorName) {
		if (fullColorName.equalsIgnoreCase("white"))
			return "{W}";
		else if (fullColorName.equalsIgnoreCase("blue"))
			return "{U}";
		else if (fullColorName.equalsIgnoreCase("black"))
			return "{B}";
		else if (fullColorName.equalsIgnoreCase("red"))
			return "{R}";
		else if (fullColorName.equalsIgnoreCase("green"))
			return "{G}";

		return "{C}";
	}

	public static Color getColorParse(List<String> fullColorName)
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
