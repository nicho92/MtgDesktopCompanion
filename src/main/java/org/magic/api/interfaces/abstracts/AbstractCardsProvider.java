package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.magic.api.beans.MTGBooster;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumCardVariation;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.enums.EnumExtra;
import org.magic.api.beans.enums.EnumFinishes;
import org.magic.api.beans.enums.EnumFrameEffects;
import org.magic.api.beans.enums.EnumLayout;
import org.magic.api.beans.enums.EnumPromoType;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.beans.enums.EnumSecurityStamp;
import org.magic.api.beans.technical.TCache;
import org.magic.api.criterias.MTGCrit;
import org.magic.api.criterias.MTGQueryBuilder;
import org.magic.api.criterias.QueryAttribute;
import org.magic.api.interfaces.MTGCardsProvider;

public abstract class AbstractCardsProvider extends AbstractMTGPlugin implements MTGCardsProvider {

	public static final String SET_FIELD = "set";
	public static final String ALL = "all";


	protected TCache<MTGCard> cacheCards;
	private TCache<MTGEdition> cacheEditions;
	private TCache<String> cacheLanguages;

	protected abstract List<QueryAttribute> loadQueryableAttributs();
	public abstract List<MTGEdition> loadEditions() throws IOException;
	public abstract List<String> loadCardsLangs() throws IOException;
	
	

	protected AbstractCardsProvider() {
		cacheCards = new TCache<>("cards");
		cacheEditions = new TCache<>("editions");
		cacheLanguages = new TCache<>("langs");
	}

	@Override
	public int hashCode() {
		return (getType()+getName()).hashCode();
	}

	

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<String> getLanguages() {
		if(cacheLanguages.isEmpty())
			try {
				loadCardsLangs().forEach(s->cacheLanguages.put(s, s));
			} catch (Exception e) {
				logger.error(e);
			}
		
		return cacheLanguages.values();
	}
	
	

	protected void initBuilder(MTGQueryBuilder<?> b)
	{
		b.addConvertor(EnumColors.class, EnumColors::getCode);
		b.addConvertor(MTGEdition.class, MTGEdition::getId);
		b.addConvertor(EnumLayout.class,(EnumLayout source)->source.name().toLowerCase());
		b.addConvertor(EnumFrameEffects.class,(EnumFrameEffects source)->source.name().toLowerCase());
		b.addConvertor(EnumRarity.class,(EnumRarity source)->source.name().toLowerCase());
		b.addConvertor(EnumPromoType.class,(EnumPromoType source)->source.name().toLowerCase());
		b.addConvertor(EnumCardVariation.class,(EnumCardVariation source)->source.name().toLowerCase());
		b.addConvertor(EnumPromoType.class,(EnumPromoType source)->source.name().toLowerCase());
		b.addConvertor(EnumFinishes.class,(EnumFinishes source)->source.name().toLowerCase());
		b.addConvertor(EnumSecurityStamp.class, (EnumSecurityStamp source)->source.name().toLowerCase());
	}


	@Override
	public QueryAttribute[] getQueryableAttributs() {

		List<QueryAttribute> atts = loadQueryableAttributs();
				atts.add(new QueryAttribute(SET_FIELD, MTGEdition.class));
				atts.add(new QueryAttribute(ALL, String.class));
		return atts.stream().toArray(QueryAttribute[]::new);
	}



	protected void postTreatmentCard(MTGCard mc)
	{
		
		try {
			var releaseYear = Integer.parseInt(mc.getEdition().getReleaseDate().substring(0, 4));
			var frameYear =  Integer.parseInt(mc.getFrameVersion());
			if( (frameYear>=1993 && frameYear<=1997)  && releaseYear > 2019)
					mc.setRetro(true);
			
			
			if(mc.getEdition().getId().endsWith("BLB") && mc.getName().startsWith("Season of "))
				mc.setText(mc.getText().replace("{P}","{Paw Print}"));
			
			
			
		}catch(Exception _)
		{
			//do nothing
		}
	}


	@Override
	public boolean equals(Object obj) {

		if(obj ==null)
			return false;

		return hashCode()==obj.hashCode();
	}

	public TCache<MTGCard> getCacheCards()
	{
		return cacheCards;
	}

	public TCache<MTGEdition> getCacheEditions()
	{
		return cacheEditions;
	}


	@Override
	public MTGCard getCardByNumber(String id, String idMe) throws IOException {
		return getCardByNumber(id, getSetById(idMe));
	}

	@Override
	public List<MTGCard> searchCardByEdition(MTGEdition ed) throws IOException {
			return  searchCardByCriteria(SET_FIELD, ed.getId(), null, false);
	}

	@Override
	public List<MTGCard> searchCardByName(String name, MTGEdition me, boolean exact) throws IOException {
		return searchCardByCriteria("name",name, me, exact);
	}


	@Override
	public List<MTGCard> searchCardByName(String name, MTGEdition me, boolean exact, EnumCardVariation extra) throws IOException{
		return searchCardByCriteria("name",name, me, exact,extra);
	}


	@Override
	public List<MTGCard> searchCardByCriteria(String att, String crit, MTGEdition me, boolean exact, EnumCardVariation extra) throws IOException {
		
		if(extra==null)
			return searchCardByCriteria(att, crit, me, exact).stream().toList();
		
		return searchCardByCriteria(att, crit, me, exact).stream().filter(mc->mc.getExtra().contains(extra)).toList();
	}

	@Override
	public MTGEdition getSetByName(String name) throws IOException {
		return listEditions().stream().filter(ed->ed.getSet().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	@Override
	public MTGEdition getSetById(String id) {

		try {
			return listEditions().stream().filter(ed->ed.getId().equalsIgnoreCase(id)).findFirst().orElse(new MTGEdition(id, "Not found set "+id));
		} catch (IOException e) {
			logger.error(e);
			return null;
		}
	}

	@Override
	public List<MTGCard> searchByCriteria(List<MTGCrit> crits) throws IOException {
		return searchByCriteria(crits.stream().toArray(MTGCrit[]::new));
	}


	@Override
	public PLUGINS getType() {
		return PLUGINS.PROVIDER;
	}

	@Override
	public List<MTGBooster> generateBooster(MTGEdition me, EnumExtra typeBooster,int qty) throws IOException {

		
		var list = new ArrayList<MTGBooster>();
		
		for(int i=0;i<qty;i++)
		{
		
		logger.debug("opening booster for {}",me);
		List<MTGCard> common = new ArrayList<>();
		List<MTGCard> uncommon = new ArrayList<>();
		List<MTGCard> rare = new ArrayList<>();
		List<MTGCard> lands = new ArrayList<>();
		var b = new MTGBooster();

		try {
			for (MTGCard mc : searchCardByEdition(me).stream().filter(MTGCard::isMainFace).toList())
			{
				if (mc.getRarity()==EnumRarity.COMMON && !mc.isBasicLand())
					common.add(mc);

				if (mc.getRarity()==EnumRarity.UNCOMMON)
					uncommon.add(mc);

				if (mc.getRarity()==EnumRarity.RARE)
					rare.add(mc);

				if (mc.getRarity()==EnumRarity.MYTHIC)
					rare.add(mc);


				if (mc.isBasicLand())
					lands.add(mc);

			}
			Collections.shuffle(lands);
			Collections.shuffle(common);
			Collections.shuffle(uncommon);
			Collections.shuffle(rare);
		} catch (Exception e) {
			logger.error("Error opening booster", e);
		}

		List<MTGCard> resList = new ArrayList<>();
		resList.addAll(common.subList(0, 11));
		resList.addAll(uncommon.subList(0, 3));
		resList.add(rare.get(0));

		if (!lands.isEmpty())
			resList.addAll(lands.subList(0, 1));

		b.setCards(resList);
		b.setEdition(me);
		notify(b);
		list.add(b);
		}
		
		return list;
	}



	@Override
	public List<MTGEdition> listEditions() throws IOException {
		if(cacheEditions.isEmpty())
		{
			logger.debug("cacheEditions not loaded. Filling it");
			loadEditions().forEach(ed->cacheEditions.put(ed.getId(), ed));
		}
		return cacheEditions.values();
	}


}
