package org.magic.api.interfaces;

import java.util.List;

import org.magic.api.beans.ShopItem;

public interface MTGShopper extends MTGPlugin {

	public List<ShopItem> search(String search);
	
	
}
