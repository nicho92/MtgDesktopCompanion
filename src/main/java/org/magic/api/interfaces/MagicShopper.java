package org.magic.api.interfaces;

import java.util.List;

import org.magic.api.beans.ShopItem;

public interface MagicShopper extends MTGPlugin {

	public List<ShopItem> search(String search);
	
	
}
