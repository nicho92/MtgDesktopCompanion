package org.beta;

import java.io.IOException;
import java.sql.SQLException;

import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGSealedProvider;
import org.magic.services.MTGControler;
import org.magic.services.tools.MTG;

public class SealedBuilder {

	public static void main(String[] args) throws SQLException, IOException {
		
		MTGControler.getInstance();
		
		var provider = MTG.getEnabledPlugin(MTGCardsProvider.class);
		provider.init();
		
		
		var sealedProvider = MTG.getEnabledPlugin(MTGSealedProvider.class);
		
		provider.listEditions().forEach(me->{
			var items = sealedProvider.getItemsFor(me);
			
			for(var item : items)
			{
				sealedProvider.getPictureFor(item);
			}
			
		});
		
		
		

	}

}
