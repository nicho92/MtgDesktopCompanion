package org.magic.tests;

import java.io.IOException;

import org.magic.api.providers.impl.MtgjsonProvider;

public class SearchTest {

	
	public static void main(String[] args) throws IOException {
		MtgjsonProvider prov = new MtgjsonProvider();
		//prov.init();
		
		System.out.println(prov.searchCardByCriteria("name", "Sigarda, Heron's Grace", null));
	}
	
	
}
