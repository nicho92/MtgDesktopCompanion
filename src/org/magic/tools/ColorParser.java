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
}
