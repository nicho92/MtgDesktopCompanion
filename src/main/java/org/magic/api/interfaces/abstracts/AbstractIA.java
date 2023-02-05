package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGIA;
import org.magic.services.MTGControler;

public abstract class AbstractIA extends AbstractMTGPlugin implements MTGIA {

	
	protected static final String SET_QUERY = "Tell me more about MTG set : ";
	protected static final String CARD_QUERY = "Tell me more about MTG card ";
	protected static final String DECK_QUERY = "Build a magic the gathering deck with this cards : ";

	
	@Override
	public PLUGINS getType() {
		return PLUGINS.IA;
	}

	
	@Override
	public String suggestDeckWith(List<MagicCard> cards) throws IOException {
		if(cards.isEmpty())
			throw new IOException("You should add some cards before asking n IA");
		
		return ask(DECK_QUERY + cards.stream().map(MagicCard::getName).collect(Collectors.joining("/")));
	}

	
	@Override
	public String describe(MagicEdition ed) throws IOException {
		if(ed ==null)
			throw new IOException("You should select a card before calling IA");
		
		return  ask( SET_QUERY+" \"" + ed.getSet() +"\" in "+MTGControler.getInstance().getLocale().getDisplayLanguage(Locale.US));
	}
	
	@Override
	public String describe(MagicCard card) throws IOException {
		if(card ==null)
			throw new IOException("You should select a card before calling IA");
		
		return  ask( CARD_QUERY+" \"" + card.getName() +"\" in "+MTGControler.getInstance().getLocale().getDisplayLanguage(Locale.US));
	}

	
}
