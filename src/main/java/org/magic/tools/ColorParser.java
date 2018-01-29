package org.magic.tools;

public class ColorParser {
	
	private ColorParser() {
	}
	
	public static String parse(String fullColorName) {
		if(fullColorName.equalsIgnoreCase("white"))
			return "{W}";
		else
			if(fullColorName.equalsIgnoreCase("blue"))
				return "{U}";
			else
				if(fullColorName.equalsIgnoreCase("black"))
					return "{B}";
				else
					if(fullColorName.equalsIgnoreCase("red"))
						return "{R}";
					else
						if(fullColorName.equalsIgnoreCase("green"))
							return "{G}";
							
							return "{C}";
	}
	
	public static String getNameByCode(String code) {
		if(code==null)
			return "";
		
		if(code.toLowerCase().contains("w"))
			return "White";
		else
			if(code.toLowerCase().contains("u"))
				return "Blue";
			else
				if(code.toLowerCase().contains("b"))
					return "Black";
				else
					if(code.toLowerCase().contains("r"))
						return "Red";
					else
						if(code.toLowerCase().contains("g"))
							return "Green";
							
							return "";
	}
	
	
}
