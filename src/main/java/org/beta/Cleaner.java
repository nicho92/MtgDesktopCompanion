package org.beta;

import java.util.Set;

import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;

public class Cleaner {

	public static void main(String[] args) {
		MTGControler.getInstance();
		
		
		PluginRegistry.inst().listPlugins().forEach(mtg->{
			System.out.println("*******************************"+mtg.getName() +" " + mtg.getType());
			
			var actualProperties = mtg.getProperties().keySet();
			actualProperties.removeAll(mtg.getDefaultAttributes().keySet());
			
			
			System.out.println(actualProperties);
			
			for(Object s : actualProperties)
			{
				//mtg.getProperties().remove(s);
			
			}
		});

	}

}
