package org.beta;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;

import forohfor.scryfall.api.Card;
import forohfor.scryfall.api.MTGCardQuery;
import forohfor.scryfall.api.Set;

public class Scryfall2 extends AbstractCardsProvider {

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public MagicCard getCardById(String id) throws IOException {
		return toMagicCard(MTGCardQuery.getCardByScryfallId(id));
	}

	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition me, boolean exact) throws IOException {
		return null;
	}

	@Override
	public MagicCard getCardByNumber(String id, MagicEdition me) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MagicEdition> loadEditions() throws IOException {
		
		if (cacheEditions.size() <= 0) {
			MTGCardQuery.getSets().stream().map(this::toMagicEdition).forEach(ed->cacheEditions.put(ed.getId(), ed));
		}
		
		return new ArrayList<>(cacheEditions.values());
	}
	
	@Override
	public MagicEdition getSetById(String id) throws IOException {
		Optional<MagicEdition> opt = loadEditions().stream().filter(ed->ed.getSet().equalsIgnoreCase(id)).findFirst();
		
		if(opt.isPresent())
			return opt.get();
		
		return new MagicEdition(id,id);
		
		
	}

	@Override
	public String[] getLanguages() {
		return new String[] { "en","es","fr","de","it","pt","ja","ru","zhs","he","ar"};
	}


	@Override
	public String[] getQueryableAttributs() {
		// TODO Auto-generated method stub
		return null;
	}
	

	private MagicEdition toMagicEdition(Set s) {
		MagicEdition ed = new MagicEdition();
				     ed.setBlock(s.getBlockName());
				     ed.setId(s.getCode());
				     ed.setReleaseDate(new SimpleDateFormat("yyyy-MM-dd").format(s.getReleasedAt()));
				     ed.setCardCount(s.getCardCount());
				     ed.setType(s.getSetType());
				     ed.setSet(s.getName());
		
		return ed;
	}

	private MagicCard toMagicCard(Card c) {
		
		return new MagicCard();
		
	}
	
	

	@Override
	public URL getWebSite() throws MalformedURLException {
		return new URL("www.scryfall.com");
	}

	@Override
	public String getName() {
		return "Scryfall";
	}

	public static void main(String[] args) throws IOException {
		MTGCardQuery.toCardList(List.of("Black Lotus"), false).forEach(c->{
			System.out.println(c);
		});

	}

}
