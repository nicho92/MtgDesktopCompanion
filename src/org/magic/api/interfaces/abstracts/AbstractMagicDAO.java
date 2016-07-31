package org.magic.api.interfaces.abstracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.ShopItem;
import org.magic.api.interfaces.MagicDAO;

public abstract class AbstractMagicDAO implements MagicDAO {

	
	private boolean enable=true;
	protected Properties props;
	protected File confdir = new File(System.getProperty("user.home")+"/magicDeskCompanion/");

	
	public AbstractMagicDAO() {
		props=new Properties();
		load();
	}
	
	
	@Override
	public Properties getProperties() {
		return props;
	}

	public abstract void init() throws ClassNotFoundException, SQLException;
	public abstract String getName();

	@Override
	public boolean isEnable() {
		return enable;
	}

	public void load()
	{
		try {
			File f = new File(confdir, getName()+".conf");
			
			if(f.exists())
			{	
				FileInputStream fis = new FileInputStream(f);
				props.load(fis);
				fis.close();
			}
			else
			{
				//save();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void save()
	{
		try {
			File f = new File(confdir, getName()+".conf");
		
			FileOutputStream fos = new FileOutputStream(f);
			props.store(fos,"");
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	@Override
	public void enable(boolean enabled) {
		this.enable=enabled;
	}
	
	
	public abstract void saveCard(MagicCard mc, MagicCollection collection) throws SQLException ;
	public abstract void removeCard(MagicCard mc, MagicCollection collection) throws SQLException ;
	public abstract MagicCard loadCard(String name, MagicCollection collection) throws SQLException;
	public abstract List<MagicCard> listCards() throws SQLException ;
	public abstract int getCardsCount(MagicCollection list,MagicEdition me) throws SQLException ;
	public abstract List<MagicCard> getCardsFromCollection(MagicCollection collection) throws SQLException ;
	public abstract List<MagicCard> getCardsFromCollection(MagicCollection collection, MagicEdition me) throws SQLException ;
	public abstract List<String> getEditionsIDFromCollection(MagicCollection collection) throws SQLException;
	public abstract MagicCollection getCollection(String name) throws SQLException ;
	public abstract void saveCollection(MagicCollection c) throws SQLException ;
	public abstract void removeCollection(MagicCollection c) throws SQLException;
	public abstract List<MagicCollection> getCollections() throws SQLException;
	public abstract void removeEdition(MagicEdition ed, MagicCollection col) throws SQLException ;
	public abstract List<MagicDeck> listDeck()throws SQLException;
	public abstract void saveDeck(MagicDeck d)throws SQLException;
	public abstract void deleteDeck(MagicDeck d)throws SQLException;
	public abstract List<MagicCollection> getCollectionFromCards(MagicCard mc) throws SQLException;
	public abstract void saveShopItem(ShopItem mp, String string) throws SQLException;
	public abstract String getSavedShopItemAnotation(ShopItem id) throws SQLException;

	
	public abstract String getDBLocation() ;
	public abstract long getDBSize() ;

}
