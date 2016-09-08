package org.magic.test;

import java.io.IOException;

import org.magic.api.providers.impl.MtgjsonProvider;

public class TestSubtreeSearch {
	
	public static void main(String[] args) throws IOException {
		MtgjsonProvider prov = new MtgjsonProvider();
		
			String crit = "emrakul";
			prov.search("$..cards[?(@.name =~ /^.*"+crit.replaceAll("\\+", " " )+".*$/i)]", "foreignNames", crit);
		
			crit = "Barricade";
			prov.search("$..cards[*].foreignNames[?(@.name =~ /^.*"+crit+".*$/i && @.language=='French')]", "foreignNames", crit);
			
			/*String format = "Standard";
			prov.search("$..cards[*].legalities[?(@.format =~ /^.*"+format+".*$/i && @.legality=='Legal')]", "legalities", format);
		*/
	}
}
