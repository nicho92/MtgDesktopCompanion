package org.magic.api.interfaces;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;

public interface MagicCardsProvider {

		
	public void init() ;
	
	public MagicCard getCardById(String id) throws Exception;
	public List<MagicCard> searchCardByCriteria(String att, String crit) throws Exception;
	public MagicCard getCardByNumber(String id, MagicEdition me) throws Exception;
	
	
	public List<MagicEdition> searchSetByCriteria(String att,String crit) throws Exception;
	public MagicEdition getSetById(String id) throws Exception;
	
	public List<String> getListType() throws Exception;
	public List<String> getListSubTypes() throws Exception;
	
	public String[] getLanguages();
	
	public String[] getQueryableAttributs();
	
	public List<MagicCard> openBooster(MagicEdition me) throws Exception;
	
	public String getVersion();
	
	public URL getWebSite() throws MalformedURLException;
	
}
