package org.magic.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ArgsLineParser {
	
	private ArgsLineParser() {	}

	
	public static void main(String[] args) {
		String[] vals = translateCommandline("search -c name=liliana of the veil");
		for(String s : vals)
			System.out.println(s);
	}
	
	
	public static String[] translateCommandline(final String toProcess) 
	{
        if (toProcess == null || toProcess.isEmpty()) {
          return new String[0];
        }
        StringTokenizer tok = new StringTokenizer(toProcess, " ", false);
        List<String> list = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        
        while (tok.hasMoreTokens()) 
        {
        	String currentTok = tok.nextToken();
        	
        	if(currentTok.startsWith("-"))
        	{
        		if(current.length()>0)
        		{
        			list.add(current.toString().trim());
        			current=new StringBuilder();
        		}
        		list.add(currentTok);
        	}
        	else
        	{
        		current.append(currentTok).append(" ");
        	}
        }
        
        if(!current.toString().isEmpty())
        	list.add(current.toString().trim());
        
        
        final String[] args = new String[list.size()];
        return list.toArray(args);
    }
	
}
