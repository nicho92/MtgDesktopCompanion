package org.beta;

import java.io.IOException;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.magic.tools.CardsPatterns;
import org.magic.tools.URLTools;

public class RulesParser {

	public static void main(String[] args) throws IOException {
		String s = URLTools.extractAsString("http://media.wizards.com/2018/downloads/MagicCompRules%2020181005.txt","ISO-8859-15");

		
		String[] split = s.split("\n");
		
		
		
		
		for(String line : split)
		{
			
			if(!StringUtils.isAllBlank(line))
			{
				Matcher m = CardsPatterns.extract(line,CardsPatterns.RULES_LINE);
				
				System.out.print(line);
				if(m.find())
					System.out.println("\n\t" + m.group());
			}
			
			
			
		}
	}
}
