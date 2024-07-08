import org.magic.services.*;
import org.magic.api.beans.*;

MTGDeckManager manager = new MTGDeckManager();

manager.listDecks().each{ d->
	Map<MTGCard, Integer> updateM = new HashMap<>();
	Map<MTGCard, Integer> updateS = new HashMap<>();
	
	d.getMain().each{e->
		var mc = provider.searchCardByName(e.getKey().getName(),e.getKey().getCurrentSet(),true).get(0);
		int qty = e.getValue();
		updateM.put(mc,qty);
	}

	d.getSideBoard().each{e->
		var	mc = provider.searchCardByName(e.getKey().getName(),e.getKey().getCurrentSet(),true).get(0);
		int qty = e.getValue();
		updateS.put(mc,qty);
	}
	
	d.getMain().clear();
	d.getSideBoard().clear();

	d.setMain(updateM);
	d.setSideBoard(updateS);

	manager.saveDeck(d);

	println("Saving " + d);
}
