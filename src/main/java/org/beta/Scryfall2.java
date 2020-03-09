package org.beta;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;

import com.google.common.collect.Lists;

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
		try {
			return cacheCards.get(id,new Callable<MagicCard>() {
				
				@Override
				public MagicCard call() throws Exception {
					return toMagicCard(MTGCardQuery.getCardByScryfallId(id));
				}
			});
		} catch (ExecutionException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition me, boolean exact) throws IOException {
		
		StringBuilder query = new StringBuilder();
		
		query.append("++");
		
		if(exact)
			query.append(att).append(":\"").append(crit).append("\"");
		else
			query.append(att).append(":").append(crit);
		
		if(me!=null)
			query.append(" s:"+me.getId());
		
		query.append(" include:extras");
		
		logger.debug("Executing "  + query.toString());
		return MTGCardQuery.search(query.toString()).stream().map(this::toMagicCard).collect(Collectors.toList());
	}

	@Override
	public MagicCard getCardByNumber(String number, MagicEdition me) throws IOException {
		try { 
			return searchCardByCriteria("number", number, me, false).get(0);
		}
		catch(IndexOutOfBoundsException ioobe)
		{
			logger.error(number + " in " + me + " not found");
			return null;
		}
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
		Optional<MagicEdition> opt = loadEditions().stream().filter(ed->ed.getId().equalsIgnoreCase(id)).findFirst();
		
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
		return new String[] { "name", "custom", "type", "color", "oracle", "mana", "cmc", "power", "toughness","loyalty", "is", "rarity", "cube", "artist", "flavor", "watermark", "border", "frame", "set" };
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
		
		MagicCard mc = new MagicCard();
			mc.setName(c.getName());
			mc.setCmc(c.getCmc().intValue());
			mc.setFrameVersion(c.getFrame());
			mc.setCost(c.getManaCost());
			mc.setTranformable(c.isMultifaced());
			generateTypes(mc,c.getTypeLine());
			
			try {
				mc.getEditions().add(getSetById(c.getSetCode()));
			} catch (IOException e) {
				logger.error(e);
			}
			
			mc.getCurrentSet().setArtist(c.getArtist());

			
		return mc;
		
	}
	
	private void generateTypes(MagicCard mc, String line) {

		line = line.replaceAll("\"", "");

		for (String k : new String[] { "Legendary", "Basic", "Ongoing", "Snow", "World" }) {
			if (line.contains(k)) {
				mc.getSupertypes().add(k);
				line = line.replaceAll(k, "").trim();
			}
		}

		String sep = "\u2014";

		if (line.contains(sep)) {

			for (String s : line.substring(0, line.indexOf(sep)).trim().split(" "))
				mc.getTypes().add(s.replaceAll("\"", ""));

			for (String s : line.substring(line.indexOf(sep) + 1).trim().split(" "))
				mc.getSubtypes().add(s);
		} else {
			for (String s : line.split(" "))
				mc.getTypes().add(s.replaceAll("\"", ""));
		}

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
		new Scryfall2().searchCardByName("black lotus", null,true).forEach(mc->{
			System.out.println(mc + " " + mc.getCurrentSet() + " " + mc.getTypes());
		});
		

	}

}
