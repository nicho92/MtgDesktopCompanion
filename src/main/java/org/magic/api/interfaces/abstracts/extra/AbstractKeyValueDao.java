package org.magic.api.interfaces.abstracts.extra;


import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractMagicDAO;

public abstract class AbstractKeyValueDao extends AbstractMagicDAO {

	@Override
	public boolean isSQL() {
		return false;
	}

	protected static final String SEPARATOR = ":";
	protected static final String KEY_COLLECTIONS = "collections";
	protected static final String KEY_DECK = "decks";
	protected static final String KEY_CARDS = "cards";
	
	public String key(MagicCollection c)
	{
		return KEY_CARDS+SEPARATOR+c.getName();
	}
	
	
	public abstract Long incr(Object o);
	
	public String key(MagicCollection c , MagicCard m)
	{
		return key(c,m.getCurrentSet());
	}
	
	public String key(MagicCollection c , MagicEdition ed)
	{
		return  key(c)+SEPARATOR+ed.getId();
	}
	
	public String key(MagicDeck c)
	{
		return  KEY_DECK+SEPARATOR+c.getId();
	}
	
}
