package org.magic.api.interfaces;

import java.util.List;

import org.magic.api.beans.MTGCombo;
import org.magic.api.beans.MTGCard;

public interface MTGComboProvider extends MTGPlugin {

	public List<MTGCombo> getComboWith(MTGCard mc);
}
