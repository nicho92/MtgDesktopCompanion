package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.magic.api.beans.MTGBooster;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumCardVariation;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.enums.EnumExtra;
import org.magic.api.beans.enums.EnumFinishes;
import org.magic.api.beans.enums.EnumFrameEffects;
import org.magic.api.beans.enums.EnumLayout;
import org.magic.api.beans.enums.EnumPromoType;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.criterias.MTGCrit;
import org.magic.api.criterias.MTGQueryBuilder;
import org.magic.api.criterias.QueryAttribute;
import org.magic.api.criterias.builders.NoneCriteriaBuilder;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.tools.BeanTools;
import org.magic.services.tools.TCache;

public abstract class AbstractCardsProvider extends AbstractMTGPlugin implements MTGCardsProvider {

	public static final String SET_FIELD = "set";
	public static final String ALL = "all";


	protected TCache<MagicCard> cacheCards;
	private TCache<MagicEdition> cacheEditions;
	private TCache<List<MagicCard>> cacheCardsByEdition;

	protected abstract List<QueryAttribute> loadQueryableAttributs();
	public abstract List<MagicEdition> loadEditions() throws IOException;


	protected AbstractCardsProvider() {
		cacheCards = new TCache<>("cards");
		cacheCardsByEdition = new TCache<>("cardsByEdition");
		cacheEditions = new TCache<>("editions");
	}

	@Override
	public int hashCode() {
		return (getType()+getName()).hashCode();
	}

	@Override
	public MTGQueryBuilder<?> getMTGQueryManager() {
		var b= new NoneCriteriaBuilder();
		initBuilder(b);
		return b;
	}

	protected void initBuilder(MTGQueryBuilder<?> b)
	{
		b.addConvertor(EnumColors.class, EnumColors::getCode);
		b.addConvertor(MagicEdition.class, MagicEdition::getId);
		b.addConvertor(EnumLayout.class,(EnumLayout source)->source.name().toLowerCase());
		b.addConvertor(EnumFrameEffects.class,(EnumFrameEffects source)->source.name().toLowerCase());
		b.addConvertor(EnumRarity.class,(EnumRarity source)->source.name().toLowerCase());
		b.addConvertor(EnumPromoType.class,(EnumPromoType source)->source.name().toLowerCase());
		b.addConvertor(EnumCardVariation.class,(EnumCardVariation source)->source.name().toLowerCase());
		b.addConvertor(EnumPromoType.class,(EnumPromoType source)->source.name().toLowerCase());
		b.addConvertor(EnumFinishes.class,(EnumFinishes source)->source.name().toLowerCase());
	}


	@Override
	public QueryAttribute[] getQueryableAttributs() {

		List<QueryAttribute> atts = loadQueryableAttributs();
				atts.add(new QueryAttribute(SET_FIELD, MagicEdition.class));
				atts.add(new QueryAttribute(ALL, String.class));
		return atts.stream().toArray(QueryAttribute[]::new);
	}



	protected void postTreatmentCard(MagicCard mc)
	{
		
		try {
			var releaseYear = Integer.parseInt(mc.getCurrentSet().getReleaseDate().substring(0, 4));
			var frameYear =  Integer.parseInt(mc.getFrameVersion());
			if( (frameYear>=1993 && frameYear<=1997)  && releaseYear > 2019)
					mc.setTimeshifted(true);
				
			
		}catch(Exception e)
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

	public TCache<MagicCard> getCacheCards()
	{
		return cacheCards;
	}

	public TCache<MagicEdition> getCacheEditions()
	{
		return cacheEditions;
	}

	public TCache<List<MagicCard>> getCacheCardsEdition()
	{
		return cacheCardsByEdition;
	}


	@Override
	public MagicCard getCardByNumber(String id, String idMe) throws IOException {
		return getCardByNumber(id, getSetById(idMe));
	}

	@Override
	public MagicCard getCardById(String id) throws IOException {
		return getCardById(id,null);
	}

	@Override
	public List<MagicCard> searchCardByEdition(MagicEdition ed) throws IOException {
		try {
			return cacheCardsByEdition.get(ed.getId(), new Callable<List<MagicCard>>() {

				@Override
				public List<MagicCard> call() throws Exception {
					return searchCardByCriteria(SET_FIELD, ed.getId(), null, false);
				}
			});
		} catch (ExecutionException e) {
			throw new IOException(e);
		}

	}

	@Override
	public List<MagicCard> searchCardByName(String name, MagicEdition me, boolean exact) throws IOException {
		var ret = searchCardByCriteria("name",name, me, exact);

		if(ret.isEmpty())
			ret = searchCardByCriteria("faceName",name, me, false);

		return ret;

	}


	@Override
	public List<MagicCard> searchCardByName(String name, MagicEdition me, boolean exact, EnumCardVariation extra) throws IOException{
		return searchCardByCriteria("name",name, me, exact,extra);
	}


	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition me, boolean exact, EnumCardVariation extra) throws IOException {
		return searchCardByCriteria(att, crit, me, exact).stream().filter(mc->mc.getExtra()==extra).toList();
	}

	@Override
	public MagicEdition getSetByName(String name) throws IOException {
		return listEditions().stream().filter(ed->ed.getSet().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	@Override
	public MagicEdition getSetById(String id) {

		try {
			MagicEdition ed = cacheEditions.get(id, new Callable<MagicEdition>() {

				@Override
				public MagicEdition call() throws Exception {
					return listEditions().stream().filter(ed->ed.getId().equalsIgnoreCase(id)).findAny().orElse(new MagicEdition(id,id));
				}
			});

			return BeanTools.cloneBean(ed);
		} catch (Exception e) {
			return new MagicEdition(id,id);
		}

	}

	@Override
	public List<MagicCard> searchByCriteria(List<MTGCrit> crits) throws IOException {
		return searchByCriteria(crits.stream().toArray(MTGCrit[]::new));
	}


	@Override
	public PLUGINS getType() {
		return PLUGINS.PROVIDER;
	}

	@Override
	public List<MTGBooster> generateBooster(MagicEdition me, EnumExtra typeBooster,int qty) throws IOException {

		
		var list = new ArrayList<MTGBooster>();
		
		for(int i=0;i<qty;i++)
		{
		
		logger.debug("opening booster for {}",me);
		List<MagicCard> common = new ArrayList<>();
		List<MagicCard> uncommon = new ArrayList<>();
		List<MagicCard> rare = new ArrayList<>();
		List<MagicCard> lands = new ArrayList<>();
		var b = new MTGBooster();

		try {
			for (MagicCard mc : searchCardByEdition(me).stream().filter(MagicCard::isMainFace).toList())
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

		List<MagicCard> resList = new ArrayList<>();
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
	public List<MagicEdition> listEditions() throws IOException {
		if(cacheEditions.isEmpty())
		{
			logger.trace("cacheEditions not loaded. Filling it");
			loadEditions().forEach(ed->cacheEditions.put(ed.getId(), ed));
		}

		return cacheEditions.values();

	}


}
