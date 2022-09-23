package org.magic.api.interfaces;

import java.util.List;

import org.magic.api.beans.MTGCombo;
import org.magic.api.beans.MagicCard;

public interface MTGComboProvider extends MTGPlugin {

	public List<MTGCombo> getComboWith(MagicCard mc);
}
