package org.beta;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.MTGControler;
import org.openjdk.jol.info.GraphLayout;
import org.openjdk.jol.util.Multiset;

public class MemoryTools {

	
	static long total=0;
	
	
	
	
	public static void main(String[] args) throws IOException {
		MTGCardsProvider prov = MTGControler.getInstance().getEnabled(MTGCardsProvider.class);
		
		prov.init();
		List<MagicCard> mcs = prov.searchCardByEdition(new MagicEdition("WAR"));
		Multiset<Class<?>> size = GraphLayout.parseInstance(mcs).getClassSizes();
		Multiset<Class<?>> count =GraphLayout.parseInstance(mcs).getClassCounts(); 
		
		total=0;
		
		size.keys().forEach(cl->{
			
			long s=size.count(cl);
			long c=count.count(cl);
			long avg=s/c;
			System.out.println("COUNT=" + c + "\tAVG=" + avg + "\tSUM="+s+ "\t"+cl.getName());
			total+=s;
		});
		System.out.println("TOTAL="+(total/1024/1024)+"MB");
    }
}