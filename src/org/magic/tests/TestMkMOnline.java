package org.magic.tests;

import java.util.List;

import org.magic.api.exports.impl.MKMOnlineWantListExport;
import org.magic.api.exports.impl.MKMOnlineWantListExport.Want;
import org.magic.api.exports.impl.MKMOnlineWantListExport.WantList;


public class TestMkMOnline {

	public static void main(String[] args) throws Exception {

		MKMOnlineWantListExport exp = new MKMOnlineWantListExport();
		
		List<WantList> list = exp.getWantList();
		List<Want> wants = exp.getWants(list.get(1));
		
		for(Want t : wants)
		{
			System.out.println(t.getProduct());
		}
		
		/*
		MagicCardsProvider prov = MTGControler.getInstance().getEnabledProviders();
		prov.init();
		
		MagicEdition ed = new MagicEdition();
			ed.setId("LEA");
		
		List<MagicCard> list = prov.searchCardByCriteria("name", "Black Lotus", ed);
		
		exp.export(list, new File("TEST"));
		*/
		
	}
}
