package org.magic.api.interfaces;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCard;

public interface MTGCardsIndexer extends MTGPlugin {


	public Map<MTGCard,Float> similarity(MTGCard mc) throws IOException;
	public void initIndex(boolean force) throws IOException;
	public long size();
	public String[] listFields();
	public List<MTGCard> search(String q);
	public Map<String,Long> terms(String field);
	public List<MTGCard> listCards();
	public List<String> suggestCardName(String q);
	public Date getIndexDate();
	}
