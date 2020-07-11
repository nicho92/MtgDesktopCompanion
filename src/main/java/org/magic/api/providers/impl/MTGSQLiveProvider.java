package org.magic.api.providers.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.ArrayUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.MagicFormat.AUTHORIZATION;
import org.magic.api.beans.MagicRuling;
import org.magic.api.beans.enums.MTGColor;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.beans.enums.MTGRarity;
import org.magic.api.interfaces.MTGPool;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.api.pool.impl.HikariPool;
import org.magic.services.MTGConstants;
import org.magic.tools.FileTools;
import org.magic.tools.URLTools;

import com.google.gson.JsonElement;

public class MTGSQLiveProvider extends AbstractCardsProvider {

	private static final String NAME = "name";
	private static final String POWER = "power";
	private static final String TOUGHNESS = "toughness";
	private static final String ARTIST = "artist";
	private static final String FLAVOR_TEXT = "flavorText";
	private static final String LANGUAGE = "language";
	private String version;
	private File sqlLiteZipFile = new File(MTGConstants.DATA_DIR,"AllPrintings.sqlite.zip");
	private File sqlLiteFile = new File(MTGConstants.DATA_DIR, "AllPrintings.sqlite");
	public static final String URL_SQLITE_ALL_PRINTS_ZIP ="https://mtgjson.com/api/v5/AllPrintings.sqlite.zip";
	private static final String FORCE_RELOAD = "FORCE_RELOAD";
	private MTGPool pool;
	private MultiValuedMap<String, MagicCardNames> mapForeignData = new ArrayListValuedHashMap<>();
	private MultiValuedMap<String, MagicRuling> mapRules = new ArrayListValuedHashMap<>();
	private MultiValuedMap<String, MagicFormat> mapLegalities = new ArrayListValuedHashMap<>();

	
	private boolean hasNewVersion() {
		String temp = "";
			try  
			{
				temp = FileTools.readFile(Mtgjson5Provider.fversion);
			}
			catch(FileNotFoundException ex)
			{
				logger.error(Mtgjson5Provider.fversion + " doesn't exist"); 
			} catch (IOException e) {
				logger.error(e);
			}
			
			try {
				logger.debug("check new version of " + toString() + " (" + temp + ")");
	
				JsonElement d = URLTools.extractJson(Mtgjson5Provider.URL_JSON_VERSION);
				version = d.getAsJsonObject().get("data").getAsJsonObject().get("version").getAsString();
				
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
	
	@Override
	public void initDefault() {
		setProperty(FORCE_RELOAD, "false");
	}
	
	@Override
	public void init() {
		try {
			logger.debug("loading file " + sqlLiteFile);

			if (hasNewVersion()||!sqlLiteFile.exists() || sqlLiteFile.length() == 0 || getBoolean(FORCE_RELOAD)) {
				logger.info("Downloading "+version + " datafile");
				URLTools.download(URL_SQLITE_ALL_PRINTS_ZIP, sqlLiteZipFile);
				FileTools.unZipIt(sqlLiteZipFile,sqlLiteFile);
				FileTools.saveFile(Mtgjson5Provider.fversion,version);
				setProperty(FORCE_RELOAD, "false");
			}
		} catch (Exception e1) {
			logger.error("error init",e1);
		}
		pool = new HikariPool();
		
		pool.init("jdbc:sqlite://"+sqlLiteFile.getAbsolutePath(), "", "", true);

	}
	
	@Override
	public MagicCard getCardByNumber(String id, MagicEdition me) throws IOException {
		return searchCardByCriteria("number", id, me, true).get(0);
	}
	
	@Override
	public MagicCard getCardById(String id, MagicEdition ed) throws IOException {
		return searchCardByCriteria("uuid", id, ed, true).get(0);
	}

	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition ed, boolean exact)throws IOException {
		
		
		if(att.equalsIgnoreCase(SET_FIELD))
		{
			att="setCode";
			exact=true;
		}
		
		StringBuilder temp = new StringBuilder("SELECT * FROM cards WHERE ").append(att);
		
		
		if(exact)
			temp.append(" = ");
		else
			temp.append(" like ");
		
		temp.append("?");
		
		if(ed!=null && !ed.getId().isEmpty())
			temp.append(" AND setCode ='").append(ed.getId()).append("'");
		
		
		if(att.equals("sql"))
		{
			temp = new StringBuilder();
			temp.append("SELECT * FROM cards WHERE ").append(crit);
		}
			
		List<MagicCard> cards = new ArrayList<>();
		
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(temp.toString())) 
		{
			
			if(!att.equalsIgnoreCase("sql"))
			{
				if(exact)
					pst.setString(1, crit);
				else
					pst.setString(1, "%"+crit+"%");
				
			}
			
			logger.debug(temp.toString().replaceFirst("\\?", "'"+crit+"'"));
			
			try (ResultSet rs = pst.executeQuery())
			{
				while(rs.next())
				{
					cards.add(generateCardsFromRs(rs));
				}
			}
			
			
		} 
		catch (SQLException e) {
			logger.error(e);
		}
		return cards;
	}
	
	
	
	@Override
	public List<MagicCard> listAllCards()throws IOException {
		List<MagicCard> cards = new ArrayList<>();
		
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * from cards")) 
		{
			try (ResultSet rs = pst.executeQuery())
			{
				while(rs.next())
					cards.add(generateCardsFromRs(rs));
			}
		} 
		catch (SQLException e) 
		{
			logger.error(e);
		}
		return cards;
	}

	private MagicCard generateCardsFromRs(ResultSet rs) throws SQLException {
		MagicCard mc = new MagicCard();
				mc.setName(rs.getString(NAME));
				mc.setCmc(rs.getInt("convertedManaCost"));
				mc.setCost(rs.getString("manaCost"));
				mc.setText(rs.getString("text"));
				mc.setId(rs.getString("uuid"));
				mc.setEdhrecRank(rs.getInt("edhrecRank"));
				mc.setFrameVersion(rs.getString("frameVersion"));
				mc.setLayout(MTGLayout.parseByLabel(rs.getString("layout")));
				mc.setPower(rs.getString(POWER));
				mc.setToughness(rs.getString(TOUGHNESS));
				mc.getRulings().addAll(getRulings(mc.getId()));
				mc.setArtist(rs.getString(ARTIST));
				mc.setFlavor(rs.getString(FLAVOR_TEXT));
				mc.setWatermarks(rs.getString("watermark"));
				mc.setOriginalText(rs.getString("originalText"));
				mc.setOriginalType(rs.getString("originalType"));
				mc.setArenaCard(rs.getBoolean("isArena"));
				mc.setMkmId(rs.getInt("mcmId"));
				mc.setMtgArenaId(rs.getInt("mtgArenaId"));
				mc.setMtgoCard(rs.getBoolean("isMtgo"));
				mc.setOnlineOnly(rs.getBoolean("isOnlineOnly"));
				mc.setPromoCard(rs.getBoolean("isPromo"));
				mc.setOversized(rs.getBoolean("isOversized"));
				mc.setReprintedCard(rs.getBoolean("isReprint"));
				mc.setReserved(rs.getBoolean("isReserved"));
				mc.setFlavorName(rs.getString("flavorName"));
				
				
				String ci = rs.getString("colorIdentity");
				if(ci!=null)
					mc.setColorIdentity(Arrays.asList(ci.split(",")).stream().map(MTGColor::colorByCode).collect(Collectors.toList()));

				ci = rs.getString("colors");
				if(ci!=null)
					mc.setColors(Arrays.asList(ci.split(",")).stream().map(MTGColor::colorByCode).collect(Collectors.toList()));

				try {
					mc.setLoyalty(Integer.parseInt(rs.getString("loyalty")));
				} catch (NumberFormatException e) {
					mc.setLoyalty(0);
				} 
				String types = rs.getString("supertypes");
				
				if(types!=null)
				{
					mc.getSupertypes().addAll(Arrays.asList(rs.getString("supertypes").split(",")));
				}
				
				types = rs.getString("types");
				
				if(types!=null)
				{
					mc.getTypes().addAll(Arrays.asList(rs.getString("types").split(",")));
				}
				
				types = rs.getString("subtypes");
				
				if(types!=null)
				{
					mc.getSubtypes().addAll(Arrays.asList(rs.getString("subtypes").split(",")));
				}
				
				
				
				MagicEdition set = getSetById(rs.getString("setCode"));
							 set.setNumber(rs.getString("number"));
							 set.setRarity(MTGRarity.rarityByName(rs.getString("rarity")));
							 set.setFlavor(rs.getString(FLAVOR_TEXT));
							 set.setScryfallId(rs.getString("scryfallId"));
							 set.setMultiverseid(rs.getString("multiverseId"));
							 set.setBorder(rs.getString("borderColor"));
							 set.setArtist(rs.getString(ARTIST));
							 mc.getEditions().add(set);
				
				for(String ids : rs.getString("printings").split(",")) 
				{
					if(!ids.equals(set.getId()))
					{
						MagicEdition ed = getSetById(ids);
						ed.setRarity(set.getRarity());
						mc.getEditions().add(ed);
					}
				}
				
				if(rs.getString("otherFaceIds")!=null)
				{
					String[] ids = rs.getString("otherFaceIds").split(",");
					if(ids.length==1)
					{
						ids = ArrayUtils.removeElement(ids, mc.getId());
						mc.setRotatedCardName(getCardNameFor(ids[0]));
					}
					else if(ids.length>2)
					{
						mc.setRotatedCardName(getCardNameFor(ids[1]));
						//[Bruna, the Fading Light, Brisela, Voice of Nightmares, Gisela, the Broken Blade]
					}
					
				}
				
				mc.getForeignNames().addAll(getTranslations(mc));
				mc.getLegalities().addAll(getLegalities(mc.getId()));
				
		notify(mc);
		return mc;
	}

	private String getCardNameFor(String id)
	{
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT name FROM cards WHERE uuid=?")) 
		{
			pst.setString(1, id);
			try (ResultSet rs = pst.executeQuery())
			{ 
				rs.next();
				return rs.getString(NAME);
			}
			
		} catch (SQLException e) {
			logger.error("error getting name for " +id ,e);
			return null;
		}
	}
	
	
	
	

	@Override
	public List<MagicEdition> loadEditions() throws IOException {
		
		List<MagicEdition> eds=new ArrayList<>();
			try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("select * from sets");ResultSet rs = pst.executeQuery()) 
			{
				
				while(rs.next())
				{
					
					MagicEdition ed = new MagicEdition();
								 ed.setSet(rs.getString(NAME));
								 ed.setId(rs.getString("code"));
								 ed.setBlock(rs.getString("block"));
								 ed.setReleaseDate(rs.getString("releaseDate"));
								 ed.setCardCount(rs.getInt("totalSetSize"));
								 ed.setCardCountOfficial(rs.getInt("baseSetSize"));
								 ed.setType(rs.getString("type"));
								 ed.setMkmName(rs.getString("mcmName"));
								 ed.setMkmid(rs.getInt("mcmId"));
								 ed.setKeyRuneCode(rs.getString("keyruneCode"));
								 ed.setOnlineOnly(rs.getBoolean("isOnlineOnly"));
								 ed.setFoilOnly(rs.getBoolean("isFoilOnly"));
								 ed.setTcgplayerGroupId(rs.getInt(("tcgplayerGroupId")));
								 initTranslations(ed);
								 eds.add(ed);
				}
			} 
			catch (SQLException e) {
				throw new IOException(e);
			}
		return eds;
	}
	
	private List<MagicCardNames> getTranslations(MagicCard mc) {
	
		MagicCardNames defaultName = new MagicCardNames();
		defaultName.setFlavor(mc.getFlavor());
		try{
			defaultName.setGathererId(Integer.parseInt(mc.getCurrentSet().getMultiverseid()));
		}
		catch(Exception e)
		{
			//do nothing
		}
		defaultName.setLanguage("English");
		defaultName.setName(mc.getName());
		defaultName.setText(mc.getText());
		defaultName.setType(mc.getFullType());
		
		mc.getForeignNames().add(defaultName);
		
		if(mapForeignData.isEmpty())
		{
			logger.debug("foreignData empty. Loading it");
				try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM foreign_data")) 
				{
					try (ResultSet rs = pst.executeQuery())
					{ 
						while(rs.next())
						{
							MagicCardNames names = new MagicCardNames();
							names.setFlavor(rs.getString(FLAVOR_TEXT));
							names.setGathererId(rs.getInt("multiverseId"));
							names.setLanguage(rs.getString(LANGUAGE));
							names.setName(rs.getString(NAME));
							names.setText(rs.getString("text"));
							names.setType(rs.getString("type"));
							String id = rs.getString("uuid");
							
							mapForeignData.put(id, names);
						}
					}
					
				} catch (SQLException e) {
					logger.error("error getting foreignData for " + mc ,e);
				}
		}
		
		return (List<MagicCardNames>) mapForeignData.get(mc.getId());
		
	}
	
	private List<MagicRuling> getRulings(String uuid) {
		
		if(mapRules.isEmpty())
		{
			logger.debug("rulings empty. Loading it");
				try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM rulings")) 
				{
					try (ResultSet rs = pst.executeQuery())
					{ 
						while(rs.next())
						{
							MagicRuling names = new MagicRuling();
							names.setText(rs.getString("text"));
							names.setDate(rs.getString("date"));
							String id = rs.getString("uuid");
							
							mapRules.put(id, names);
						}
					}
					
				} catch (SQLException e) {
					logger.error("error getting rules for " + uuid ,e);
				}
		}
		
		return (List<MagicRuling>) mapRules.get(uuid);
		
	}
	
	private List<MagicFormat> getLegalities(String uuid){
		if(mapLegalities.isEmpty())
		{
			logger.debug("legalities empty. Loading it");
				try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM legalities")) 
				{
					try (ResultSet rs = pst.executeQuery())
					{ 
						while(rs.next())
						{
							String id = rs.getString("uuid");
							mapLegalities.put(id, new MagicFormat(rs.getString("format"), AUTHORIZATION.valueOf(rs.getString("status").toUpperCase())));
						}
					}
					
				} catch (SQLException e) {
					logger.error("error getting legalities for " + uuid ,e);
				}
		}
		
		return (List<MagicFormat>) mapLegalities.get(uuid);
	}
	
	private void initTranslations(MagicEdition ed)
	{
		
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM set_translations WHERE setCode=?")) 
		{
			pst.setString(1, ed.getId());
			try (ResultSet rs = pst.executeQuery())
			{ 
				while(rs.next())
					ed.getTranslations().put(rs.getString(LANGUAGE), rs.getString("translation"));
			}
			
		} catch (SQLException e) {
			logger.error("error getting translation for " + ed ,e);
		}
	}

	@Override
	public String[] getLanguages() {
		List<String> ret = new ArrayList<>();
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("Select DISTINCT language from foreign_data");ResultSet rs = pst.executeQuery())
		{
			ret.add("English");
			while(rs.next())
			{
				ret.add(rs.getString(LANGUAGE));
			}
		} 
		catch (SQLException e) {
			logger.error(e);
			
		}
		
		return ret.stream().toArray(String[]::new);
	}

	@Override
	public List<String> loadQueryableAttributs() {
		List<String> ret = new ArrayList<>();
		
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("PRAGMA table_info(cards)");ResultSet rs = pst.executeQuery())
		{
			
			while(rs.next())
			{
				ret.add(rs.getString(NAME));
			}
			
			
			ret.add("sql");
			Collections.sort(ret);
			ret.remove("name");
			ret.add(0, "name");
			
		} 
		catch (SQLException e) {
			logger.error(e);
			
		}
		
		return ret;
	}

	@Override
	public URL getWebSite() throws MalformedURLException {
		return new URL("https://github.com/mtgjson/mtgsqlive");
	}

	@Override
	public String getName() {
		return "MTGSQLive";
	}
	
	@Override
	public String getVersion() {
		return version;
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

}
