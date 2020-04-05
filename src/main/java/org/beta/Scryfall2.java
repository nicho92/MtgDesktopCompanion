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
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.tools.POMReader;

import forohfor.scryfall.api.Card;
import forohfor.scryfall.api.MTGCardQuery;
import forohfor.scryfall.api.Set;

public class Scryfall2 extends AbstractCardsProvider {

	private static final String CUSTOM = "custom";

	@Override
	public String getVersion() {
		return POMReader.readVersionFromPom(MTGCardQuery.class,"/META-INF/maven/io.github.forohforerror/ScryfallAPIBinding/pom.properties");
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	@Override
	public void init() {
		// do nothing
	}
	

	public static void main(String[] args) throws IOException {
		new Scryfall2().searchCardByName("Black Lotus", null,true).forEach(mc->{
			System.out.println(mc + " " + mc.getEditions() + " " + mc.getTypes());
		});
	
	}


	@Override
	public MagicCard getCardById(String id,MagicEdition ed) throws IOException {
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
		
		if(att.equals(CUSTOM))
			query = new StringBuilder(crit);
		
		if(att.equals("set"))
			query = new StringBuilder("++s:"+crit);
		
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
	public MagicEdition getSetById(String id)  {
		Optional<MagicEdition> opt;
		try {
			opt = loadEditions().stream().filter(ed->ed.getId().equalsIgnoreCase(id)).findFirst();

			if(opt.isPresent())
				return opt.get();
			
		} catch (IOException e) {
			logger.error("Error loading " + id,e);
		}
		
		
		return new MagicEdition(id,id);
		
		
	}

	@Override
	public String[] getLanguages() {
		return new String[] { "en","es","fr","de","it","pt","ja","ru","zhs","he","ar"};
	}


	@Override
	public String[] getQueryableAttributs() {
		return new String[] { "name", CUSTOM, "type", "color", "oracle", "mana", "cmc", "power", "toughness","loyalty", "is", "rarity", "cube", "artist", "flavor", "watermark", "border", "frame", "set" };
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
		try {
			
			MagicCard mc =  cacheCards.get(c.getScryfallUUID().toString(), new Callable<MagicCard>() {

				@Override
				public MagicCard call() throws Exception {
					MagicCard mc = new MagicCard();
						mc.setName(c.getName());
						mc.setCmc(c.getCmc().intValue());
						mc.setFrameVersion(c.getFrame());
						mc.setCost(c.getManaCost());
						mc.setTranformable(c.isMultifaced());
						mc.setFlippable(c.getLayout().equals("flip"));
						mc.setEdhrecRank(c.getEDHRecRank());
						mc.setLayout(MTGLayout.parseByLabel(c.getLayout()));
						mc.setReserved(c.isReserved());
						mc.setWatermarks(c.getWatermark());
						c.getLegalities().entrySet().forEach(e->mc.getLegalities().add(new MagicFormat(e.getKey(),e.getValue().equals("legal"))));
						parsingTypesLine(mc,c.getTypeLine());
						
							mc.getEditions().add(getSetById(c.getSetCode()));
							mc.getCurrentSet().setArtist(c.getArtist());
							mc.getCurrentSet().setFlavor(c.getFlavorText());
							mc.getCurrentSet().setNumber(c.getCollectorNumber());
							mc.getCurrentSet().setRarity(c.getCollectorNumber());
							
							
						mc.getEditions().addAll(loadingOtherEditions());
					
						
					return mc;
				}

				private List<MagicEdition> loadingOtherEditions() {
					List<MagicEdition> eds = new ArrayList<>();
					MTGCardQuery.search("++name:'"+c.getName()+"' -s:"+c.getSetCode() + " include:extras").forEach(c2->{
						
						MagicEdition ed = getSetById(c2.getSetCode());
							ed.setArtist(c2.getArtist());
							ed.setLayout(c2.getLayout());
							ed.setRarity(c2.getRarity());
							ed.setNumber(c2.getCollectorNumber());
							eds.add(ed);
					});
					
					
					return eds;
				}
			});
			notify(mc);
			return mc;
		} catch (Exception e) {
			logger.error("error parsing " + c,e);
			return null;
		}
	}
	
	private void parsingTypesLine(MagicCard mc, String line) {

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

}
