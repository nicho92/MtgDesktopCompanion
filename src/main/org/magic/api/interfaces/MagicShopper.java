package org.magic.api.interfaces;

import java.util.List;
import java.util.Properties;

import org.magic.api.beans.ShopItem;

public interface MagicShopper extends MTGPlugin {

	public List<ShopItem> search(String search);
	
	
}
