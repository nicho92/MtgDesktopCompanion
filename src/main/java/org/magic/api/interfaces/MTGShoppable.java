package org.magic.api.interfaces;

import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;

public interface MTGShoppable {

	public int getQte();
	public Double getPrice();
	public String itemName();
	public MagicEdition getEdition();
	public MagicCollection getMagicCollection();
}
