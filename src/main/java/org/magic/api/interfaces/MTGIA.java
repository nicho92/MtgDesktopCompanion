package org.magic.api.interfaces;

import java.io.IOException;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;

public interface MTGIA extends MTGPlugin {

	public MTGCard generateRandomCard(String description, MTGEdition mtgEdition, String number) throws IOException;

}
