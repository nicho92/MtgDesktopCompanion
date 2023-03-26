package org.magic.api.providers.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.beanutils.BeanUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.criterias.MTGCrit;
import org.magic.api.criterias.QueryAttribute;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.services.MTGConstants;
import org.magic.services.tools.FileTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PrivateMTGSetProvider extends AbstractCardsProvider {

	private static final String CARDS = "cards";

	private String ext = ".json";
	private File setDirectory;
	private JsonExport serializer;
	
	public void removeEdition(MagicEdition me) {
		var f = new File(setDirectory, me.getId() + ext);
		try {
			logger.debug("delete : {}",f);
			FileTools.deleteFile(f);
		} catch (IOException e) {
			logger.error(e);
		}
	}


	public boolean removeCard(MagicEdition me, MagicCard mc) throws IOException {
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

	
	
	
	private List<MagicCard> loadCardsFromSet(MagicEdition me) throws IOException {
		
		var root = FileTools.readJson(new File(setDirectory, me.getId() + ext)).getAsJsonObject().get(CARDS);
		return serializer.fromJsonList(root.toString(), MagicCard.class);

	}

	public void addCard(MagicEdition me, MagicCard mc) throws IOException {
		var f = new File(setDirectory, me.getId() + ext);
		var root = FileTools.readJson(f).getAsJsonObject();
		var cards = root.get(CARDS).getAsJsonArray();

		int index = indexOf(mc, cards);

		if (index > -1) {
			cards.set(index, serializer.toJsonElement(mc));
		} else {
			cards.add(serializer.toJsonElement(mc));
			me.setCardCount(me.getCardCount() + 1);
			root.addProperty("cardCount", me.getCardCount());
		}
		FileTools.saveFile(f, root.toString());
	}

	private int indexOf(MagicCard mc, JsonArray arr) {
		for (var i = 0; i < arr.size(); i++)
			if (arr.get(i).getAsJsonObject().get("id") != null && (arr.get(i).getAsJsonObject().get("id").getAsString().equals(mc.getId())))
				return i;

		return -1;
	}

	private MagicEdition loadEditionnFromFile(File f) throws IOException {
		var root = FileTools.readJson(f).getAsJsonObject();
		return serializer.fromJson(root.get("main").toString(), MagicEdition.class);
	}

	public void saveEdition(MagicEdition ed, List<MagicCard> cards) {
		
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




	public void saveEdition(MagicEdition me) throws IOException {
		var cardCount = 0;
		try {
			cardCount = loadCardsFromSet(me).size();

		} catch (Exception e) {
			logger.error(e);
		}

		me.setCardCount(cardCount);

		var jsonparams = new JsonObject();
		jsonparams.add("main", serializer.toJsonElement(me));

		if (cardCount == 0)
			jsonparams.add(CARDS, new JsonArray());
		else
			jsonparams.add(CARDS, serializer.toJsonElement(loadCardsFromSet(me)));

		FileTools.saveFile(new File(setDirectory, me.getId() + ext), jsonparams.toString());

	}
	public PrivateMTGSetProvider() {
		super();
		setDirectory = getFile("DIRECTORY");
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
	public MagicCard getCardById(String id, MagicEdition ed) {
		try {
			return searchCardByCriteria("id", id, ed, true).get(0);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public MagicCard getCardById(String id) throws IOException {
		try {
			return searchCardByCriteria("id", id, null, true).get(0);
		}
		catch(IndexOutOfBoundsException e)
		{
			return null;
		}
	}



	@Override
	public List<MagicCard> searchCardByEdition(MagicEdition ed) throws IOException {
		return loadCardsFromSet(ed);
	}



	@Override
	public List<MagicCard> listAllCards() throws IOException {
		List<MagicCard> res = new ArrayList<>();
			for (MagicEdition ed : listEditions())
				for (MagicCard mc : loadCardsFromSet(ed))
						res.add(mc);

			return res;
	}


	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition me, boolean exact)throws IOException {
		List<MagicCard> res = new ArrayList<>();
		if (me == null) {
			for (MagicEdition ed : listEditions())
				for (MagicCard mc : loadCardsFromSet(ed))
					if (hasValue(mc, att, crit))
						res.add(mc);
		} else {
			for (MagicCard mc : loadCardsFromSet(me)) {
				if (hasValue(mc, att, crit))
					res.add(mc);
			}
		}

		return res;
	}

	private boolean hasValue(MagicCard mc, String att, String val) {
		try {
			return BeanUtils.getProperty(mc, att).toUpperCase().contains(val.toUpperCase());
		} catch (Exception e) {
			logger.error("error loading {} {} {}" ,mc,att,val,e);
			return false;
		}
	}

	@Override
	public MagicCard getCardByNumber(String id, MagicEdition me) throws IOException {

		MagicEdition ed = getSetById(me.getId());

		for (MagicCard mc : loadCardsFromSet(ed))
			if (mc.getCurrentSet().getNumber().equals(id))
				return mc;

		return null;

	}

	@Override
	public List<MagicEdition> loadEditions() throws IOException {
		List<MagicEdition> ret = new ArrayList<>();
		for (File f : setDirectory.listFiles(pathname -> pathname.getName().endsWith(ext))) {
			ret.add(loadEditionnFromFile(f));
		}

		return ret;
	}

	@Override
	public MagicEdition getSetById(String id){
		try {
			return loadEditionnFromFile(new File(setDirectory, id + ext));
		} catch (IOException e) {
			return new MagicEdition(id,id);
		}
	}

	@Override
	public String[] getLanguages() {
		return new String[] { "French" };
	}

	@Override
	public List<QueryAttribute> loadQueryableAttributs() {
		try {
				Set<String> keys = BeanUtils.describe(new MagicCard()).keySet();

				return keys.stream().map(k->new QueryAttribute(k,String.class)).toList();
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
		return "Personnal Data Set Provider";
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(MTGConstants.IMAGE_LOGO);
	}


	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of("DIRECTORY",new File(MTGConstants.DATA_DIR, "privateSets").getAbsolutePath());
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
	public List<MagicCard> searchByCriteria(MTGCrit<?>... crits) throws IOException {
		throw new IOException("Not implemented");
	}


	@Override
	public MagicCard getCardByArenaId(String id) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public MagicCard getCardByScryfallId(String crit) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}



}
