package org.magic.api.interfaces.abstracts.extra;


import java.sql.SQLException;

import org.magic.api.beans.MTGAlert;
import org.magic.api.beans.MTGAnnounce;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGCollection;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGNews;
import org.magic.api.beans.MTGSealedStock;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.GedEntry;
import org.magic.api.interfaces.MTGSerializable;
import org.magic.api.interfaces.abstracts.AbstractMagicDAO;
import org.magic.services.MTGConstants;

public abstract class AbstractKeyValueDao extends AbstractMagicDAO {

	@Override
	public boolean isSQL() {
		return false;
	}

	protected static final String SEPARATOR = ":";
	protected static final String KEY_COLLECTIONS = "collections";
	protected static final String KEY_DECK = "decks";
	protected static final String KEY_STOCKS = "stocks";
	protected static final String KEY_SEALED = "sealeds";
	protected static final String KEY_TRANSACTIONS ="transactions";
	protected static final String KEY_CONTACTS ="contacts";
	protected static final String KEY_ALERTS ="alerts";
	protected static final String KEY_ANNOUNCES ="announces";
	protected static final String KEY_NEWS ="news";
	protected static final String KEY_GED ="ged";
	
	
	@Override
	public void init() throws SQLException {
		initDefaultData();
		
	}
	

	protected void initDefaultData() throws SQLException
	{
		
		if(listCollections().isEmpty()) {
			for(String s : MTGConstants.getDefaultCollectionsNames())	
				saveCollection(s);
		}
		
		if(getContactById(1)==null)
			saveOrUpdateContact(MTGConstants.DEFAULT_CONTACT);
			
	}
	
	
	protected <T extends MTGSerializable> String key(GedEntry<T> gedItem) {
		return KEY_GED+SEPARATOR+gedItem.getClasse().getName()+SEPARATOR+gedItem.getId();
	}
	
	protected String key(MTGAnnounce c)
	{
		return KEY_ANNOUNCES+SEPARATOR+c.getId();
	}
	
	protected String key(MTGNews c)
	{
		return KEY_NEWS+SEPARATOR+c.getId();
	}
	
	protected String key(MTGAlert c)
	{
		return KEY_ALERTS+SEPARATOR+c.getId();
	}
	
	protected String key(MTGCollection c)
	{
		return KEY_STOCKS+SEPARATOR+c.getName();
	}
	
	protected String key(Contact c)
	{
		return KEY_CONTACTS+SEPARATOR+c.getId();
	}
	
	protected String key(Transaction c)
	{
		return KEY_TRANSACTIONS+SEPARATOR+c.getId();
	}
	
	protected String key(MTGCardStock c)
	{
		return key(c.getMagicCollection(),c.getProduct().getEdition())+SEPARATOR+c.getId();
	}
	
	protected String key(MTGSealedStock c)
	{
		return KEY_SEALED+SEPARATOR+c.getId();
	}
	
	public abstract Long incr(Class<?> c);
	
	protected String key(MTGCollection c , MTGCard m)
	{
		return key(c,m.getEdition());
	}
	
	protected String key(MTGCollection c , MTGEdition ed)
	{
		if(ed==null)
			return key(c);
		
		return  key(c)+SEPARATOR+ed.getId();
	}
	
	protected String key(MTGDeck c)
	{
		return  KEY_DECK+SEPARATOR+c.getId();
	}
	
	
	
	
}
