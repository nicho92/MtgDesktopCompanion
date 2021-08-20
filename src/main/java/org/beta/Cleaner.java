package org.beta;

import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;

public class Cleaner {

	public static void main(String[] args) {
		MTGControler.getInstance();
		
		
		PluginRegistry.inst().listPlugins().forEach(mtg->{
			System.out.println("*******************************"+mtg.getName());
			
			var actualProperties = mtg.getProperties().keySet();
			var defaultProperties = mtg.getDefaultAttributes().keySet();
			
			System.out.println( actualProperties);
			System.out.println( defaultProperties);
			
		});

	}

}
