package org.beta;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.magic.tools.URLTools;

public class RulesParser {

	public static void main(String[] args) throws IOException {
		String s = URLTools.extractAsString("http://media.wizards.com/2018/downloads/MagicCompRules%2020181005.txt");

		
		String[] split = s.split("\n");
		
		for(String line : split)
		{
			if(!StringUtils.isAllBlank(line))
			{
				//do parsing
			}
			
		}
	}
}
