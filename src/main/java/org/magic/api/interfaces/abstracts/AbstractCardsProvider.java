package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.apache.commons.beanutils.BeanUtils;
import org.magic.api.beans.Booster;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.MTGCardVariation;
import org.magic.api.beans.enums.MTGColor;
import org.magic.api.beans.enums.MTGFinishes;
import org.magic.api.beans.enums.MTGFrameEffects;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.beans.enums.MTGPromoType;
import org.magic.api.beans.enums.MTGRarity;
import org.magic.api.criterias.MTGCrit;
import org.magic.api.criterias.MTGQueryBuilder;
import org.magic.api.criterias.QueryAttribute;
import org.magic.api.criterias.builders.NoneCriteriaBuilder;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.tools.TCache;

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
		b.addConvertor(MTGColor.class, MTGColor::getCode);
		b.addConvertor(MagicEdition.class, MagicEdition::getId);
		b.addConvertor(MTGLayout.class,(MTGLayout source)->source.name().toLowerCase());
		b.addConvertor(MTGFrameEffects.class,(MTGFrameEffects source)->source.name().toLowerCase());
		b.addConvertor(MTGRarity.class,(MTGRarity source)->source.name().toLowerCase());
		b.addConvertor(MTGPromoType.class,(MTGPromoType source)->source.name().toLowerCase());
		b.addConvertor(MTGCardVariation.class,(MTGCardVariation source)->source.name().toLowerCase());
		b.addConvertor(MTGPromoType.class,(MTGPromoType source)->source.name().toLowerCase());
		b.addConvertor(MTGFinishes.class,(MTGFinishes source)->source.name().toLowerCase());
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
		if(mc.getCurrentSet().getId().endsWith("MH2") && (mc.getFrameVersion().equals("1995")||mc.getFrameVersion().equals("1997")))
			mc.setTimeshifted(true);

		if(mc.getCurrentSet().getId().equals("H1R"))
			mc.setTimeshifted(true);

		if(mc.getCurrentSet().getId().equals("TSR") && Integer.parseInt(mc.getCurrentSet().getNumber())>=290)
			mc.setTimeshifted(true);

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
	public List<MagicCard> searchCardByName(String name, MagicEdition me, boolean exact, MTGCardVariation extra) throws IOException{
		return searchCardByCriteria("name",name, me, exact,extra);
	}


	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition me, boolean exact, MTGCardVariation extra) throws IOException {
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

			return (MagicEdition) BeanUtils.cloneBean(ed);
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
	public Booster generateBooster(MagicEdition me) throws IOException {

		logger.debug("opening booster for {}",me);
		List<MagicCard> common = new ArrayList<>();
		List<MagicCard> uncommon = new ArrayList<>();
		List<MagicCard> rare = new ArrayList<>();
		List<MagicCard> lands = new ArrayList<>();
		var b = new Booster();

		try {
			for (MagicCard mc : searchCardByEdition(me).stream().filter(MagicCard::isMainFace).toList())
			{
				if (mc.getRarity()==MTGRarity.COMMON && !mc.isBasicLand())
					common.add(mc);

				if (mc.getRarity()==MTGRarity.UNCOMMON)
					uncommon.add(mc);

				if (mc.getRarity()==MTGRarity.RARE)
					rare.add(mc);

				if (mc.getRarity()==MTGRarity.MYTHIC)
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

		logger.trace("generating cards for edition {} : {}",b.getEdition(),b.getCards());

		return b;
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
