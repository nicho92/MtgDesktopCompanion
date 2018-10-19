package org.magic.api.providers.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.magic.api.beans.Booster;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.MagicRuling;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;
import org.magic.services.MTGConstants;
import org.magic.tools.Chrono;
import org.magic.tools.ColorParser;
import org.magic.tools.URLTools;

import com.google.common.cache.Cache;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.spi.cache.CacheProvider;
import com.jayway.jsonpath.spi.cache.LRUCache;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;


public class Mtgjson4Provider extends AbstractCardsProvider {

	
	private static final String URL_VERSION = "URL_VERSION";
	private static final String URL_SET_JSON_ZIP = "URL_SET_JSON_ZIP";
	private static final String CARDS_ROOT_SEARCH = ".cards[?(@.";
	private static final Object NAME = "name";
	private List<String> currentSet = null;
	
	
	private File fileSetJsonTemp = new File(confdir, "AllSets-x.json4.zip");
	private File fileSetJson = new File(confdir, "AllSets-x4.json");
	private File fversion = new File(confdir, "version4");
	private String version;
	private Chrono chrono;
	private ReadContext ctx;
	
	
	public static void main(String[] args) {
		MTGCardsProvider prov = new Mtgjson4Provider();
		prov.init();
		
		try {
			for(MagicCard mc : prov.searchCardByName("Arlinn Kord",null,false))
				System.out.println(mc +" " + mc.getCurrentSet());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public Mtgjson4Provider() {
		super();
		currentSet = new ArrayList<>();
		if(CacheProvider.getCache()==null)
			CacheProvider.setCache(new LRUCache(getInt("LRU_CACHE")));
		
	}

	private void unZipIt() {

		byte[] buffer = new byte[1024];

		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(fileSetJsonTemp))) {

			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {
				logger.info(this + " unzip : " + fileSetJson.getAbsoluteFile());

				try (FileOutputStream fos = new FileOutputStream(fileSetJson)) {
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					ze = zis.getNextEntry();
				}
			}
		} catch (IOException ex) {
			logger.error(ex);
		}

		boolean del = FileUtils.deleteQuietly(fileSetJsonTemp);
		logger.debug("remove " + fileSetJsonTemp + "=" + del);

	}

	private boolean hasNewVersion() {
		String temp = "";
		
			try (BufferedReader br = new BufferedReader(new FileReader(fversion))) 
			{
				temp = br.readLine();
			}
			catch(FileNotFoundException ex)
			{
				logger.error(fversion + " doesn't exist"); 
			} catch (IOException e) {
				logger.error(e);
			}
			
			try {
			logger.debug("check new version of " + toString() + " (" + temp + ")");

			Document d = URLTools.extractHtml(getURL(URL_VERSION));
			version = d.select("div.splash H4").html().replace("Current version: ", "").trim();
			if (!version.equals(temp)) {
				logger.info("new version datafile exist (" + version + "). Downloading it");
				return true;
			}

			logger.debug("check new version of " + this + ": up to date");
			return false;
		} catch (Exception e) {
			version = temp;
			logger.error("Error getting last version ",e);
			return false;
		}

	}

	public void init() {
		logger.info("init " + this);

		chrono=new Chrono();
		cacheBoosterCards = new HashMap<>();

		Configuration.setDefaults(new Configuration.Defaults() {

			private final JsonProvider jsonProvider = new GsonJsonProvider();
			private final MappingProvider mappingProvider = new GsonMappingProvider();

			@Override
			public JsonProvider jsonProvider() {
				return jsonProvider;
			}

			@Override
			public MappingProvider mappingProvider() {
				return mappingProvider;
			}

			@Override
			public Set<Option> options() {
				return EnumSet.noneOf(Option.class);
			}

		});
		Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

		try {

			logger.debug("loading file " + fileSetJson);

			if (!fileSetJson.exists() || fileSetJson.length() == 0) {
				logger.info("datafile does not exist. Downloading it");
				FileUtils.copyInputStreamToFile(URLTools.openConnection(getURL(URL_SET_JSON_ZIP)).getInputStream(),fileSetJsonTemp);
				unZipIt();
				FileUtils.writeStringToFile(fversion,version,MTGConstants.DEFAULT_ENCODING,false);
			}
			else if (hasNewVersion()) {
				FileUtils.copyInputStreamToFile(URLTools.openConnection(getURL(URL_SET_JSON_ZIP)).getInputStream(),fileSetJsonTemp);
				unZipIt();
				FileUtils.writeStringToFile(fversion,version,MTGConstants.DEFAULT_ENCODING,false);
			}
			logger.debug(this + " : parsing db file");
			ctx = JsonPath.parse(fileSetJson);
			logger.debug(this + " : parsing OK ");
			
		} catch (Exception e1) {
			logger.error(e1);
		}
	}

	public MagicCard getCardById(String id) throws IOException {
		return searchCardByCriteria("uuid", id, null, true).get(0);
	}
	

	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition ed, boolean exact) throws IOException {
		
		String filterEdition = ".";

		if (ed != null)
			filterEdition = filterEdition + ed.getId();

		String jsquery = "$" + filterEdition + CARDS_ROOT_SEARCH + att + " =~ /^.*" + crit.replaceAll("\\+", " ")+ ".*$/i)]";

		if (exact)
			jsquery = "$" + filterEdition + CARDS_ROOT_SEARCH + att + " == \"" + crit.replaceAll("\\+", " ") + "\")]";

		if (att.equalsIgnoreCase("set")) 
		{
			if (cacheBoosterCards.get(crit) != null) {
				logger.debug(crit + " is already in cache. Loading from it");
				return cacheBoosterCards.get(crit);
			}
			else {
				jsquery = "$." + crit.toUpperCase() + ".cards";
			}
		}
		else if(StringUtils.isNumeric(crit)) {
			jsquery = "$" + filterEdition + CARDS_ROOT_SEARCH + att + " == " + crit + ")]";
		}
		
		return search(jsquery, att, crit);
		
	}

	
	private List<MagicCard> search(String jsquery, String att, String crit) {
		
		List<String> currentSet = new ArrayList<>();
		ArrayList<MagicCard> ret = new ArrayList<>();
		List<Map<String, Object>> cardsElement = ctx.withListeners(fr -> {
			if (fr.path().startsWith("$")) {
				currentSet.add(fr.path().substring(fr.path().indexOf("$[") + 3, fr.path().indexOf("]") - 1));
			}
			return null;
		}).read(jsquery, List.class);
		
		
		
		
		logger.debug("parsing " + jsquery + " " + currentSet);
		
		int indexSet = 0;
		for (Map<String, Object> map : cardsElement) {
			
			MagicCard mc = new MagicCard();
					  mc.setFlippable(false);
					  mc.setTranformable(false);
					  mc.setId(String.valueOf(map.get("uuid").toString()));
					  mc.setText(String.valueOf(map.get("text")));
					  
			if (map.get("name") != null)
				mc.setName(String.valueOf(map.get("name")));
					  
			if (map.get("manaCost") != null)
				mc.setCost(String.valueOf(map.get("manaCost")));
			else
				mc.setCost("");
			
			if (map.get("multiverseId") != null)
				mc.setMultiverseid((int)Double.parseDouble(map.get("multiverseId").toString()));
			
			if (map.get("rarity") != null)
				mc.setRarity(String.valueOf(map.get("rarity")));
			
			if (map.get("number") != null)
				mc.setNumber(String.valueOf(map.get("number")));

			if (map.get("text") != null)
				mc.setText(String.valueOf(map.get("text")));
			
			if (map.get("convertedManaCost") != null)
				mc.setCmc((int)Double.parseDouble(map.get("convertedManaCost").toString()));
			
			if (map.get("frameVersion") != null)
				mc.setFrameVersion(String.valueOf(map.get("frameVersion")));

			if (map.get("isReserved") != null)
				mc.setReserved(Boolean.valueOf(String.valueOf(map.get("isReserved"))));

			if (map.get("layout") != null)
				mc.setLayout(String.valueOf(map.get("layout")));
			
			if (map.get("flavorText") != null)
				mc.setFlavor(String.valueOf(map.get("flavorText")));
			
			if (map.get("originalText") != null)
				mc.setOriginalText(String.valueOf(map.get("originalText")));
			
			if (map.get("originalType") != null)
				mc.setOriginalType(String.valueOf(map.get("originalType")));
			
			if (map.get("supertypes") != null)
				mc.getSupertypes().addAll((List<String>) map.get("supertypes"));

			if (map.get("types") != null)
				mc.getTypes().addAll((List<String>) map.get("types"));

			if (map.get("subtypes") != null)
				mc.getSubtypes().addAll((List<String>) map.get("subtypes"));

			if (map.get("power") != null)
				mc.setPower(String.valueOf(map.get("power")));
			
			if (map.get("toughness") != null)
				mc.setToughness(String.valueOf(map.get("toughness")));
			
			if (map.get("colors") != null)
				((List<String>) map.get("colors")).forEach(s->mc.getColors().add(ColorParser.getNameByCode(s)));

			if (map.get("colorIdentity") != null)
				mc.getColorIdentity().addAll((List<String>) map.get("colorIdentity"));
			
			
			if (map.get("loyalty") != null) {
				try {
					mc.setLoyalty((int) Double.parseDouble(map.get("loyalty").toString()));
				} catch (Exception e) {
					mc.setLoyalty(0);
				}
			}
			
			if (map.get("legalities") != null) {
				
				for (Map.Entry<String,String> mapFormats : ((Map<String,String>) map.get("legalities")).entrySet()) {
					MagicFormat mf = new MagicFormat();
					mf.setFormat(String.valueOf(mapFormats.getKey()));
					mf.setLegality(String.valueOf(mapFormats.getValue()));
					mc.getLegalities().add(mf);
				}
			}
			
			if (map.get("rulings") != null) {
				for (Map<String, Object> mapRules : (List<Map>) map.get("rulings")) {
					MagicRuling mr = new MagicRuling();
					mr.setDate(String.valueOf(mapRules.get("date")));
					mr.setText(String.valueOf(mapRules.get("text")));
					mc.getRulings().add(mr);
				}
			}
			
			if (mc.getLayout().equals("double-faced") || mc.getLayout().equals("meld"))
				mc.setTranformable(true);


			MagicCardNames defnames = new MagicCardNames();
			defnames.setLanguage("English");
			defnames.setName(mc.getName());
			if (mc.getMultiverseid() != null)
				defnames.setGathererId(mc.getMultiverseid());

			mc.getForeignNames().add(defnames);
			
			if (map.get("foreignData") != null) {

				for (Map<String, Object> mapNames : (List<Map>) map.get("foreignData")) {
					MagicCardNames fnames = new MagicCardNames();
								   fnames.setLanguage(String.valueOf(mapNames.get("language")));
								   fnames.setName(String.valueOf(mapNames.get("name")));
								   
								   if (mapNames.get("text") != null)
									   fnames.setText(String.valueOf(mapNames.get("text")));
								   
								   if (mapNames.get("type") != null)
									   fnames.setText(String.valueOf(mapNames.get("type")));
								   

					if (mapNames.get("multiverseId") != null)
						fnames.setGathererId((int) (double) mapNames.get("multiverseId"));

					mc.getForeignNames().add(fnames);
				}
			}
			
			
			String codeEd;
			if (currentSet.size() <= 1)
				codeEd = currentSet.get(0);
			else
				codeEd = currentSet.get(indexSet++);

			MagicEdition me = getSetById(codeEd);
						 me.setRarity(mc.getRarity());
						 me.setNumber(mc.getNumber());
						 me.setFlavor(mc.getFlavor());
						 if(mc.getMultiverseid()!=null)
							 me.setMultiverseid(String.valueOf(mc.getMultiverseid()));
						
			mc.getEditions().add(me);
			
			if (!mc.isBasicLand() && map.get("printings") != null)
			{
				for (String print : (List<String>) map.get("printings")) {
					if (!print.equalsIgnoreCase(codeEd)) {
						MagicEdition meO = getSetById(print);
						if (mc.getMultiverseid() == null)
							meO.setMultiverseid(String.valueOf(0));

						initOtherEditionCardsVar(mc, meO);
						mc.getEditions().add(meO);
					}
				}

			}
			notify(mc);
			
			ret.add(mc);
		}
		
		
		return ret;
	}
	
	private void initOtherEditionCardsVar(MagicCard mc, MagicEdition me) {
		String edCode = me.getId();

		if (!edCode.startsWith("p"))
			edCode = edCode.toUpperCase();

		String jsquery = "$." + edCode + ".cards[?(@.name==\""+ mc.getName().replaceAll("\\+", " ").replaceAll("\"", "\\\\\"") + "\")]";

		List<Map<String, Object>> cardsElement = null;
		try {
			cardsElement = ctx.read(jsquery, List.class);
		} catch (Exception e) {
			logger.error("error in " + jsquery +" " +  e);
			return ;
		}

		if (cardsElement != null)
			for (Map<String, Object> map : cardsElement) {
				try {
					me.setRarity(String.valueOf(map.get("rarity")));
				} catch (Exception e) {
					me.setRarity(mc.getRarity());
				}

				try {

					me.setNumber(String.valueOf(map.get("number")));
				} catch (Exception e) {
					logger.trace("initOtherEditionCardsVar number not found");
				}

				try {
					me.setMultiverseid(String.valueOf((int) (double) map.get("multiverseId")));
				} catch (Exception e) {
					logger.trace("multiverseNotFound for " + me);
				}
			}
	}


	public List<MagicEdition> loadEditions() throws IOException {
		String jsquery = "$.*";

		if (!cacheEditions.values().isEmpty()) {
			logger.trace("editions already loaded.Return cache");
			return new ArrayList<>(cacheEditions.values());
		}
		logger.debug("load editions");
		chrono.start();
		
		final List<String> codeEd = new ArrayList<>();
		ctx.withListeners(fr -> {
			if (fr.path().startsWith("$"))
				codeEd.add(fr.path().substring(fr.path().indexOf("$[") + 3, fr.path().indexOf("]") - 1));
			return null;

		}).read(jsquery, List.class);

		codeEd.forEach(codeedition->cacheEditions.put(codeedition, getSetById(codeedition)));
		logger.debug("Loading editions OK in " + chrono.stop() + " sec.");
		
		return new ArrayList<>(cacheEditions.values());
	}

	public MagicEdition getSetById(String id) {
		
		if(id.startsWith("p"))
			id=id.toUpperCase();
		
		if(id.equals("CON"))
			id="CON_";
		
		MagicEdition ed = new MagicEdition(id);
		String base = "$." + id.toUpperCase();
		try{
		ed.setSet(ctx.read(base + ".name", String.class));
		}catch(PathNotFoundException pnfe)
		{	ed.setSet(id);}
		
		
		try{
		ed.setReleaseDate(ctx.read(base + ".releaseDate", String.class));
		}catch(PathNotFoundException pnfe)
		{	}
		
		try{
		ed.setType(ctx.read(base + ".type", String.class));
		}catch(PathNotFoundException pnfe)
		{	}
		
		try{
			ed.setBlock(ctx.read(base + ".block", String.class));
		}catch(PathNotFoundException pnfe)
		{	}
		
		try{
			ed.setBorder(ctx.read(base + ".cards[0].borderColor", String.class));
		}catch(PathNotFoundException pnfe)
		{ 	}
		
		if (ed.getCardCount() == 0)
			try{	
				Integer i = ctx.read(base + ".cards.length()");
				ed.setCardCount(i);
			}
			catch(Exception e)
			{	logger.error("error " + id);		}
		
		
		return ed;

	}

	public String[] getQueryableAttributs() {
		return new String[] { "name","set","artist","text","convertedManaCost","flavorText","frameVersion","isReserved","layout","manaCost","multiverseId","number","rarity","hasFoil","hasNonFoil","collection" };
	}

	public String getName() {
		return "MTGJson4";
	}

	public String[] getLanguages() {
		return new String[] { "English","French","Italian","German","Portuguese (Brazil)"  };
	}

	

	public Booster generateBooster(MagicEdition me) {
		return null;
	
	}
	
	
	

	public MagicCard getCardByNumber(String num, MagicEdition me) throws IOException {
			return null;
	}

	public String getVersion() {
		return version;
	}

	@Override
	public URL getWebSite() throws MalformedURLException {
		return new URL("https://mtgjson.com/v4");
	}


	@Override
	public void initDefault() {
		setProperty(URL_SET_JSON_ZIP, "https://mtgjson.com/v4/json/AllSets.json.zip");
		setProperty(URL_VERSION, "https://mtgjson.com/v4/");
		setProperty("LRU_CACHE", "400");
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	@Override
	public Icon getIcon() {
		return new ImageIcon(AbstractMTGPlugin.class.getResource("/icons/plugins/MTGJson.png"));
	}
}
