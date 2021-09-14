import org.magic.services.*;
import org.magic.api.beans.*;

MTGDeckManager manager = new MTGDeckManager();

manager.listLocalDecks().each{ d->
	try{
		manager.saveDeck(d);
	}
	catch(Exception e)
	{
		System.out.println("error for " + d + " : " + e);
	}
}
