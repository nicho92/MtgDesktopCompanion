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
