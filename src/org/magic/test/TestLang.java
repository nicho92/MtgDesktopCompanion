package org.magic.test;

import java.io.IOException;

import org.magic.api.providers.impl.MtgjsonProvider;

public class TestLang {

	public static void main(String[] args) throws IOException {
		
		MtgjsonProvider prov = new MtgjsonProvider();
		
		System.out.println(prov.searchCardByCriteria("foreignNames", "Prêtresse"));
		
		
	}
	
}
