package org.magic.tools;

public class ColorParser {

	
	public static String parse(String string) {
		if(string.equals("White"))
			return "{W}";
		else
			if(string.equals("Blue"))
				return "{U}";
			else
				if(string.equals("Black"))
					return "{B}";
				else
					if(string.equals("Red"))
						return "{R}";
					else
						if(string.equals("Green"))
							return "{G}";
							
							return string;
	}
}
