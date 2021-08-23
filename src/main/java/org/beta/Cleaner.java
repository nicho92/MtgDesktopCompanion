package org.beta;


import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;

public class Cleaner {

	public static void main(String[] args) {
		MTGControler.getInstance();
		
		
		PluginRegistry.inst().listPlugins().forEach(mtg->{
			System.out.println("*******************************"+mtg.getName() +" " + mtg.getType());
			
			mtg.getProperties().keySet().removeIf(o->!mtg.getDefaultAttributes().containsKey(o));
			
			mtg.save();
			
			
		});

	}

}
