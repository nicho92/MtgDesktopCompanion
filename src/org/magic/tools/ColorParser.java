package org.magic.tools;

public class ColorParser {

	
	public static String parse(String fullColorName) {
		if(fullColorName.toLowerCase().equals("white"))
			return "{W}";
		else
			if(fullColorName.toLowerCase().equals("blue"))
				return "{U}";
			else
				if(fullColorName.toLowerCase().equals("black"))
					return "{B}";
				else
					if(fullColorName.toLowerCase().equals("red"))
						return "{R}";
					else
						if(fullColorName.toLowerCase().equals("green"))
							return "{G}";
							
							return "{C}";
	}
	
	public static String getNameByCode(String code) {
		
		if(code==null)
			return "";
		
		if(code.toLowerCase().contains("W"))
			return "White";
		else
			if(code.toLowerCase().contains("U"))
				return "Blue";
			else
				if(code.toLowerCase().contains("B"))
					return "Black";
				else
					if(code.toLowerCase().contains("R"))
						return "Red";
					else
						if(code.toLowerCase().contains("G"))
							return "Green";
							
							return "";
	}
	
	
}
