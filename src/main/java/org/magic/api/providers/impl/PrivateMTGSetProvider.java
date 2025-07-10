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
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.services.MTGConstants;
import org.magic.services.tools.FileTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PrivateMTGSetProvider extends AbstractCardsProvider {

	private static final String DIRECTORY = "DIRECTORY";

	public static final String PERSONNAL_DATA_SET_PROVIDER = "Personnal Data Set Provider";

	private static final String CARDS = "cards";

	private String ext = ".json";
	private File setDirectory;
	private JsonExport serializer;
	
	public void removeEdition(MTGEdition me) {
		var f = new File(setDirectory, me.getId() + ext);
		try {
			logger.debug("delete : {}",f);
			FileTools.deleteFile(f);
		} catch (IOException e) {
			logger.error(e);
		}
	}


	public boolean removeCard(MTGEdition me, MTGCard mc) throws IOException {
		var f = new File(setDirectory, me.getId() + ext);
		var root = FileTools.readJson(f).getAsJsonObject();
		var cards = root.get(CARDS).getAsJsonArray();

		for (var i = 0; i < cards.size(); i++) {
			JsonElement el = cards.get(i);
			if (el.getAsJsonObject().get("id").getAsString().equals(mc.getId())) {
				cards.remove(el);
				FileTools.saveFile(new File(setDirectory, me.getId() + ext), root.toString());
				return true;
			}
		}
		return false;
	}
	
	@Override
	public MTGQueryBuilder<?> getMTGQueryManager() {
		var b= new JsonCriteriaBuilder();
		initBuilder(b);
		return b;
	}
	

	
	
	
	private List<MTGCard> loadCardsFromSet(MTGEdition me) throws IOException {
		
		var f = new File(setDirectory, me.getId() + ext);
		
		if (!f.toPath().normalize().startsWith(getString(DIRECTORY))) {
            throw new IOException("Entry is outside of the target directory");
        }
		
		if(!f.exists())
			return new ArrayList<>();
		
		
		var root = FileTools.readJson(f).getAsJsonObject().get(CARDS);
		
		if(root==null || root.isJsonNull())
			return new ArrayList<>();
		
		return serializer.fromJsonList(root.toString(), MTGCard.class);

	}

	public void addCard(MTGEdition me, MTGCard mc) throws IOException {
		var f = new File(setDirectory, me.getId() + ext);
		var root = FileTools.readJson(f).getAsJsonObject();
		var cards = root.get(CARDS).getAsJsonArray();
		mc.setEdition(me);
		postTreatmentCard(mc);
		
		int index = indexOf(mc, cards);

		if (index > -1) 
		{
			cards.set(index, serializer.toJsonElement(mc));
		} 
		else {
			cards.add(serializer.toJsonElement(mc));
			me.setCardCount(me.getCardCount() + 1);
			root.addProperty("cardCount", me.getCardCount());
		}
		logger.info("Adding {} card to {} set with id={}",mc,me,mc.getId());
		FileTools.saveFile(f, root.toString());
	}

	private int indexOf(MTGCard mc, JsonArray arr) {
		for (var i = 0; i < arr.size(); i++)
			if (arr.get(i).getAsJsonObject().get("id") != null && (arr.get(i).getAsJsonObject().get("id").getAsString().equals(mc.getId())))
				return i;

		return -1;
	}

	private MTGEdition loadEditionFromFile(File f) throws IOException {
		if(f.getCanonicalPath().startsWith(getFile(DIRECTORY).getCanonicalPath()))
		{
			var root = FileTools.readJson(f).getAsJsonObject();
			return serializer.fromJson(root.get("main").toString(), MTGEdition.class);
		}
		
		throw new IOException("Path is incorrect : "+f.getAbsolutePath());
	}

	public void saveEdition(MTGEdition ed, List<MTGCard> cards) {
		
		cards.forEach(mc->{
			try {
				removeCard(ed, mc);
				addCard(ed, mc);
				saveEdition(ed);
			} catch (IOException e) {
				logger.error(e);
			}
		});
	}




	public void saveEdition(MTGEdition me) throws IOException {

		var cards= loadCardsFromSet(me);
		
		me.setCardCount(cards.size());
		me.setCardCountOfficial(cards.size());
		me.setCardCountPhysical(cards.size());
		
		var jsonparams = new JsonObject();
		jsonparams.add("main", serializer.toJsonElement(me));

		if (cards.isEmpty())
			jsonparams.add(CARDS, new JsonArray());
		else
			jsonparams.add(CARDS, serializer.toJsonElement(cards));

		FileTools.saveFile(new File(setDirectory, me.getId() + ext), jsonparams.toString());

	}
	public PrivateMTGSetProvider() {
		super();
		setDirectory = getFile(DIRECTORY);
		serializer = new JsonExport();
		
	}
	

	@Override
	public void init() {
	
		logger.debug("Opening directory {}",setDirectory);
		if (!setDirectory.exists())
		{
			logger.debug("Directory {} doesn't exist",setDirectory);
			setDirectory.mkdir();
		}
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
			for (MTGEdition ed : listEditions())
				for (MTGCard mc : loadCardsFromSet(ed))
						res.add(mc);

			return res;
	}


	@Override
	public List<MTGCard> searchCardByCriteria(String att, String crit, MTGEdition me, boolean exact)throws IOException {
		List<MTGCard> res = new ArrayList<>();
		if (me == null) {
			for (MTGEdition ed : listEditions())
				for (MTGCard mc : loadCardsFromSet(ed))
					if (hasValue(mc, att, crit))
					{
						res.add(mc);
					}
		} else {
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

		MTGEdition ed = getSetById(me.getId());

		for (MTGCard mc : loadCardsFromSet(ed))
			if (mc.getNumber().equals(id))
				return mc;

		return null;

	}

	@Override
	public List<MTGEdition> loadEditions() throws IOException {
		List<MTGEdition> ret = new ArrayList<>();
		for (File f : setDirectory.listFiles(pathname -> pathname.getName().endsWith(ext))) {
			ret.add(loadEditionFromFile(f));
		}
		return ret;
	}

	@Override
	public List<MTGEdition> listEditions() throws IOException {
		//bypass cache
		return loadEditions();
	}
	
	
	@Override
	public MTGEdition getSetById(String id){
		try {
			return loadEditionFromFile(new File(setDirectory, id + ext));
		} catch (IOException _) {
			return new MTGEdition(id,id);
		}
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
	public STATUT getStatut() {
		return STATUT.DEV;
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
		return Map.of(DIRECTORY, MTGProperty.newDirectoryProperty(new File(MTGConstants.DATA_DIR, "privateSets")));
	}


	@Override
	public boolean equals(Object obj) {

		if(obj ==null)
			return false;

		return hashCode()==obj.hashCode();
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public List<MTGCard> searchByCriteria(MTGCrit<?>... crits) throws IOException {
		throw new IOException("Not implemented");
	}


	@Override
	public MTGCard getCardByArenaId(String id) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public MTGCard getCardByScryfallId(String crit) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}



}
