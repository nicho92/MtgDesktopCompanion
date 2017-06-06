package org.magic.tests;

import java.io.File;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.exports.impl.MKMOnlineWantListExport;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.services.MTGControler;


public class TestMkMOnline {

	
	public static void main(String[] args) throws Exception {

		MKMOnlineWantListExport exp = new MKMOnlineWantListExport();
		MagicCardsProvider prov = MTGControler.getInstance().getEnabledProviders();
		prov.init();
		
		MagicEdition ed = new MagicEdition();
			ed.setId("LEA");
		
		List<MagicCard> list = prov.searchCardByCriteria("name", "Black Lotus", ed);
		
		System.out.println(list.get(0).getEditions().get(0).getMkm_name());
		
		exp.export(list, new File("TEST"));
		
		
	}
}
