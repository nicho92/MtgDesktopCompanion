package org.magic.api.interfaces;

import java.util.List;
import java.util.Properties;

import org.magic.api.beans.ShopItem;

public interface MagicShopper {

	public List<ShopItem> search(String search);

	public void enable(boolean boolean1);

	boolean isEnable();

	public Properties getProperties();
	public void setProperties(String k,Object value);

	public void load();
	public void save();
	
	
	public String getName();
	
	
	
}
