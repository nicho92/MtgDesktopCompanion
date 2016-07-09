package org.magic.tools;

public class ColorParser {

	
	public static String parse(String fullColorName) {
		if(fullColorName.toLowerCase().equals("white"))
			return "{W}";
		else
			if(fullColorName.equals("blue"))
				return "{U}";
			else
				if(fullColorName.equals("black"))
					return "{B}";
				else
					if(fullColorName.equals("red"))
						return "{R}";
					else
						if(fullColorName.equals("green"))
							return "{G}";
							
							return "{C}";
	}
}
