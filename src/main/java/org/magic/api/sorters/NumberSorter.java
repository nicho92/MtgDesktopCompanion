package org.magic.api.sorters;

import java.util.Comparator;
import java.util.regex.Pattern;

public class NumberSorter implements Comparator<String>{

	Pattern p;
	public NumberSorter() {
		p = Pattern.compile("\\d+");
	}

	@Override
	public int compare(String num1, String num2)
	{
		
		
		var m1 = p.matcher(num1);
		var m2 = p.matcher(num2);
		
		if(m1.find() && m2.find())
		{
			try {
				if (Integer.parseInt(m1.group()) > Integer.parseInt(m2.group()))
					return 1;
				
				if (Integer.parseInt(m1.group()) == Integer.parseInt(m2.group()))
					return 0;
				
				if (Integer.parseInt(m1.group()) < Integer.parseInt(m2.group()))
					return -1;
				
			} catch (NumberFormatException _) {
				return -1;
			}

		}
		return -1;


	}
}
