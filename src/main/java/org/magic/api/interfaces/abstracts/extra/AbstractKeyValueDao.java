package org.magic.api.interfaces.abstracts.extra;


import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractMagicDAO;

public abstract class AbstractKeyValueDao extends AbstractMagicDAO {

	@Override
	public boolean isSQL() {
		return false;
	}

	protected final String separator = ":";
	protected final String collectionKey = "collections";
	protected final String cardKey = "card";
	
	
	public String key(MagicCollection collection)
	{
		return collection.getName();
	}
	
	public String key(MagicEdition ed)
	{
		return ed.getId();
	}
	
	public String key(MagicCard card)
	{
		return card.getId();
	}
	
	
	public String collectionCardKey(MagicCollection collection, MagicCard mc)
	{
		return new StringBuilder(cardKey).append(separator)
						 .append(key(collection)).append(separator)
						 .append(key(mc.getCurrentSet())).append(separator)
						 .append(key(mc))
						 .toString();
	}
	
}
