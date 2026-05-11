package org.magic.api.providers.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.Icon;
import org.apache.commons.beanutils.BeanUtils;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.criterias.MTGCrit;
import org.magic.api.criterias.MTGQueryBuilder;
import org.magic.api.criterias.QueryAttribute;
import org.magic.api.criterias.builders.JsonCriteriaBuilder;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.api.sorters.CardsEditionSorter;
import org.magic.services.MTGConstants;
import org.magic.services.tools.BeanTools;
import org.magic.services.tools.CryptoUtils;
import org.magic.services.tools.MTG;

public class PrivateMTGSetProvider extends AbstractCardsProvider {

	public static final String PERSONNAL_DATA_SET_PROVIDER = "Personnal Data Set Provider";

	public boolean deleteCustomCard(MTGCard mc) {
		try {
			MTG.getEnabledPlugin(MTGDao.class).deleteCustomCard(mc);
			return true;
		} catch (SQLException e) {
			logger.error(e);
			return false;
		}
	}

	public void saveCustomSet(MTGEdition me) throws IOException {
		try {
			MTG.getEnabledPlugin(MTGDao.class).saveCustomSet(me);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	public void saveCustomSet(MTGEdition ed, List<MTGCard> cards) {

		cards.forEach(mc -> {
			try {
				saveCustomCard(ed, mc);
			} catch (IOException e) {
				logger.error(e);
			}
		});
	}

	public void deleteCustomSet(MTGEdition me) throws IOException {
		try {
			MTG.getEnabledPlugin(MTGDao.class).deleteCustomSet(me);
		} catch (SQLException e) {
			throw new IOException(e);
		}

	}

	public void saveCustomCard(MTGEdition me, MTGCard mc) throws IOException {

		mc.setEdition(me);

		if (mc.getId() == null)
			mc.setId(CryptoUtils.uuid());

		AbstractCardsProvider.postTreatmentCard(mc);

		try {
			MTG.getEnabledPlugin(MTGDao.class).saveCustomCard(mc);
			notify(mc);
		} catch (SQLException e) {
			throw new IOException(e);
		}

	}

	public void rebuild(MTGEdition ed) throws IOException {
		var cards = searchCardByEdition(ed);
		ed.setCardCount(cards.size());
		ed.setCardCountOfficial((int) cards.stream().filter(mc -> mc.getSide().equals("a") && mc.isToken()).count());
		ed.setCardCountPhysical(ed.getCardCountOfficial());

		cards.forEach(mc -> {
			mc.getEditions().clear();
			try {
				mc.getEditions().add(BeanTools.cloneBean(ed));
				mc.setEdition(ed);
				mc.setNumber(null);
			} catch (Exception e) {
				logger.error(e);
			}
		});
		Collections.sort(cards, new CardsEditionSorter());

		for (var i = 0; i < cards.size(); i++)
			cards.get(i).setNumber(String.valueOf((i + 1)));

		var i = 1;
		for (var t : cards.stream().filter(MTGCard::isToken).toList()) {
			t.setNumber("T" + (i++));
		}
		
		for (var b : cards.stream().filter(c->c.getSide().equals("b")).toList()) {
				var opt = cards.stream().filter(a->a.getName().equals(b.getRotatedCard().getName())).findFirst();
				if(opt.isPresent()) {
					var faceACard = opt.get();
					b.setNumber(faceACard.getNumber());
				}
		}
		saveCustomSet(ed, cards);
	}

	@Override
	public MTGQueryBuilder<?> getMTGQueryManager() {
		var b = new JsonCriteriaBuilder();
		initBuilder(b);
		return b;
	}

	@Override
	public MTGCard getCardById(String id) {
		try {
			return searchCardByCriteria("id", id, null, true).get(0);
		} catch (Exception _) {
			return null;
		}
	}

	@Override
	public List<MTGCard> searchCardByEdition(MTGEdition me) throws IOException {
		try {
			return MTG.getEnabledPlugin(MTGDao.class).listCustomCards(me);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<MTGCard> listAllCards() throws IOException {
		List<MTGCard> res = new ArrayList<>();
		for (var ed : listEditions())
			for (var mc : searchCardByEdition(ed))
				res.add(mc);

		return res;
	}

	@Override
	public List<MTGCard> searchCardByCriteria(String att, String crit, MTGEdition me, boolean exact)
			throws IOException {
		var res = new ArrayList<MTGCard>();

		if (me == null) {
			for (var mc : listAllCards())
				if (hasValue(mc, att, crit)) {
					notify(mc);
					res.add(mc);
				}
		} else {
			for (MTGCard mc : searchCardByEdition(me)) {
				if (hasValue(mc, att, crit)) {
					notify(mc);
					res.add(mc);
				}
			}
		}
		return res;
	}

	private boolean hasValue(MTGCard mc, String att, String val) {
		try {
			if (att.equals("set"))
				return mc.getEdition().getId().equals(val);

			return BeanUtils.getProperty(mc, att).toUpperCase().contains(val.toUpperCase());
		} catch (Exception e) {
			logger.error("error loading {} {} {}", mc, att, val, e);
			return false;
		}
	}

	@Override
	public MTGCard getCardByNumber(String id, MTGEdition me) throws IOException {

		var ed = getSetById(me.getId());

		for (MTGCard mc : searchCardByEdition(ed))
			if (mc.getNumber().equals(id))
				return mc;

		return null;

	}

	@Override
	public List<MTGEdition> loadEditions() throws IOException {
		try {
			return MTG.getEnabledPlugin(MTGDao.class).listCustomSets();
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public MTGEdition getSetById(String id) {
		try {
			return MTG.getEnabledPlugin(MTGDao.class).getCustomSetById(id);
		} catch (SQLException _) {
			return null;
		}
	}

	@Override
	public List<String> loadCardsLangs() throws IOException {
		return new ArrayList<>();
	}

	@Override
	public List<QueryAttribute> loadQueryableAttributs() {
		try {

			var mc = new MTGCard();

			var keys = BeanUtils.describe(mc).keySet();

			return keys.stream().map(k -> {
				var qa = new QueryAttribute(k, String.class);
				return qa;
			}).sorted().collect(Collectors.toList());

		} catch (Exception e) {
			logger.error(e);
			return new ArrayList<>();
		}
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getName() {
		return PERSONNAL_DATA_SET_PROVIDER;
	}

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_LOGO;
	}

	@Override
	public List<MTGCard> searchByCriteria(MTGCrit<?>... crits) throws IOException {
		throw new IOException("Not implemented");
	}

	@Override
	public MTGCard getCardByArenaId(String id) throws IOException {
		return null;
	}

	@Override
	public MTGCard getCardByScryfallId(String crit) throws IOException {
		return null;
	}

}
