package org.magic.api.providers.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.beanutils.BeanUtils;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.criterias.MTGCrit;
import org.magic.api.criterias.MTGQueryBuilder;
import org.magic.api.criterias.QueryAttribute;
import org.magic.api.criterias.builders.JsonCriteriaBuilder;
import org.magic.api.customs.CustomCardsManager;
import org.magic.api.customs.FileCustomManager;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.services.MTGConstants;

public class PrivateMTGSetProvider extends AbstractCardsProvider {


	public static final String PERSONNAL_DATA_SET_PROVIDER = "Personnal Data Set Provider";
	private CustomCardsManager manager;
	


	public void rebuildSet(MTGEdition ed) throws IOException {
		manager.rebuild(ed);
		
	}

	
	public void removeEdition(MTGEdition me) {
		manager.removeEdition(me);
	}


	public boolean removeCard(MTGEdition me, MTGCard mc) throws IOException {
		return manager.removeCard(me, mc);
	}
	
	private List<MTGCard> loadCardsFromSet(MTGEdition me) throws IOException {
		return manager.listCards(me);
	}

	public void addCard(MTGEdition me, MTGCard mc) throws IOException {
		postTreatmentCard(mc);
		manager.addCard(me, mc);
	}
	
	
	public void saveEdition(MTGEdition ed, List<MTGCard> cards) {
		manager.saveEdition(ed,cards);
	}

	public void saveEdition(MTGEdition me) throws IOException {
		manager.saveEdition(me);
	}
	
	public PrivateMTGSetProvider() {
		super();
		manager = new FileCustomManager( getFile("DIRECTORY"));
	}
	


	@Override
	public MTGQueryBuilder<?> getMTGQueryManager() {
		var b= new JsonCriteriaBuilder();
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
	public List<MTGCard> searchCardByEdition(MTGEdition ed) throws IOException {
		return loadCardsFromSet(ed);
	}



	@Override
	public List<MTGCard> listAllCards() throws IOException {
		List<MTGCard> res = new ArrayList<>();
			for (var ed : listEditions())
				for (var mc : loadCardsFromSet(ed))
						res.add(mc);

			return res;
	}


	@Override
	public List<MTGCard> searchCardByCriteria(String att, String crit, MTGEdition me, boolean exact)throws IOException {
		List<MTGCard> res = new ArrayList<>();
		if (me == null) {
				for (var mc : listAllCards())
					if (hasValue(mc, att, crit))
						res.add(mc);
		} 
		else 
		{
			for (MTGCard mc : loadCardsFromSet(me)) {
				if (hasValue(mc, att, crit))
				{
					res.add(mc);
				}
			}
		}

		return res;
	}

	private boolean hasValue(MTGCard mc, String att, String val) {
		try {
			return BeanUtils.getProperty(mc, att).toUpperCase().contains(val.toUpperCase());
		} catch (Exception e) {
			logger.error("error loading {} {} {}" ,mc,att,val,e);
			return false;
		}
	}

	@Override
	public MTGCard getCardByNumber(String id, MTGEdition me) throws IOException {

		var ed = getSetById(me.getId());

		for (MTGCard mc : loadCardsFromSet(ed))
			if (mc.getNumber().equals(id))
				return mc;

		return null;

	}

	@Override
	public List<MTGEdition> loadEditions() throws IOException {
		return manager.loadEditions();
	}

	@Override
	public List<MTGEdition> listEditions() throws IOException {
		//bypass cache
		return loadEditions();
	}
	
	
	@Override
	public MTGEdition getSetById(String id){
		return manager.getSetById(id);
	}

	@Override
	public List<String> loadCardsLangs() throws IOException {
		return new ArrayList<>();
	}

	@Override
	public List<QueryAttribute> loadQueryableAttributs() {
		try {
				var keys = BeanUtils.describe(new MTGCard()).keySet();
				return keys.stream().map(k->new QueryAttribute(k,String.class)).collect(Collectors.toList());
			} catch (Exception e) {
			logger.error(e);
			return new ArrayList<>();
		}
	}

	@Override
	public String getVersion() {
		return "0.1";
	}

	@Override
	public String getName() {
		return PERSONNAL_DATA_SET_PROVIDER;
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(MTGConstants.IMAGE_LOGO);
	}


	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("DIRECTORY", MTGProperty.newDirectoryProperty(new File(MTGConstants.DATA_DIR, "privateSets")));
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
