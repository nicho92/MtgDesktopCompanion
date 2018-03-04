package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.utils.patterns.observer.Observable;

public abstract class AbstractCardExport extends AbstractMTGPlugin implements MTGCardsExport {

	@Override
	public PLUGINS getType() {
		return PLUGINS.EXPORT;
	}
		
	public AbstractCardExport() {
		confdir = new File(MTGConstants.CONF_DIR, "exports");
		props=new Properties();
		if(!confdir.exists())
			confdir.mkdir();
		load();
	}
	

	@Override
	public void export(List<MagicCard> cards, File f) throws IOException {

		MagicDeck d = new MagicDeck();
				d.setName("export " + getName() + " cards");
				d.setDescription(getName() +" export to " + f.getName());
				d.setDateCreation(new Date());
		int i=0;
		for(MagicCard mc : cards)
		{
			d.add(mc);
			setChanged();
			notifyObservers(i++);
		}
		export(d,f);
	}
	
	
	protected List<MagicCardStock> importFromDeck(MagicDeck deck)
	{
		List<MagicCardStock> mcs = new ArrayList<>();
		
		for(MagicCard mc : deck.getMap().keySet())
		{
			MagicCardStock stock = new MagicCardStock();
				stock.setMagicCard(mc);
				stock.setQte(deck.getMap().get(mc));
				stock.setComment("import from " + deck.getName());
				stock.setIdstock(-1);
				stock.setUpdate(true);
				mcs.add(stock);
		}
		return mcs;
	}
	

}
