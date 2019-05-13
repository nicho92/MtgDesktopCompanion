package org.beta;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.MTGControler;
import org.openjdk.jol.info.GraphLayout;
import org.openjdk.jol.util.Multiset;

public class MemoryTest {

	
	
	
	public static void main(String[] args) throws IOException {
		MTGCardsProvider prov = MTGControler.getInstance().getEnabled(MTGCardsProvider.class);
		
		prov.init();
		List<MagicCard> mcs = prov.searchCardByEdition(new MagicEdition("WAR"));
		Multiset<Class<?>> size = GraphLayout.parseInstance(mcs.get(0)).getClassSizes();
		Multiset<Class<?>> count =GraphLayout.parseInstance(mcs.get(0)).getClassCounts(); 
		
		size.keys().forEach(cl->{
			
			long s=size.count(cl);
			long c=count.count(cl);
			
			System.out.println("SUM=" + s + "\tCOUNT=" + c + "\t"+cl);
		});
    }
}