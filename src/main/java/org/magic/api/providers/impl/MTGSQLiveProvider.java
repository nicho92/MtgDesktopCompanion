package org.magic.api.providers.impl;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.magic.api.beans.MTGKeyWord;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.MagicFormat.AUTHORIZATION;
import org.magic.api.beans.MagicRuling;
import org.magic.api.beans.enums.MTGColor;
import org.magic.api.beans.enums.MTGFrameEffects;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.beans.enums.MTGRarity;
import org.magic.api.criterias.CardAttribute;
import org.magic.api.criterias.MTGCrit;
import org.magic.api.criterias.MTGQueryBuilder;
import org.magic.api.criterias.SQLCriteriaBuilder;
import org.magic.api.interfaces.MTGPool;
import org.magic.api.interfaces.abstracts.AbstractMTGJsonProvider;
import org.magic.api.pool.impl.HikariPool;
import org.magic.services.MTGConstants;

public class MTGSQLiveProvider extends AbstractMTGJsonProvider {

	private MTGPool pool;
	private MultiValuedMap<String, MagicCardNames> mapForeignData = new ArrayListValuedHashMap<>();
	private MultiValuedMap<String, MagicRuling> mapRules = new ArrayListValuedHashMap<>();
	private MultiValuedMap<String, MagicFormat> mapLegalities = new ArrayListValuedHashMap<>();

	
	@Override
	public String getOnlineDataFileZip() {
		return "https://mtgjson.com/api/v5/AllPrintings.sqlite.zip";
	}
	
	@Override
	public MTGQueryBuilder<?> getMTGQueryManager() {
		MTGQueryBuilder<?> b= new SQLCriteriaBuilder();
		b.addConvertor(Boolean.class,(Boolean source)->source.booleanValue()?"1":"0");
		initBuilder(b);
		return b;
	}
	
	
	@Override
	public List<MagicCard> searchByCriteria(MTGCrit<?>... crits) throws IOException {
		
		List<MagicCard> cards = new ArrayList<>();
		try (Connection c = pool.getConnection(); Statement pst = c.createStatement()) 
		{
			String sql = getMTGQueryManager().build(crits).toString();
			logger.debug("sql="+sql);
			try (ResultSet rs = pst.executeQuery(sql))
			{
				while(rs.next())
				{
					cards.add(generateCardsFromRs(rs,true));
				}
			}
		} 
		catch (SQLException e) {
			logger.error(e);
		}
		return cards;
	}
	
	
	@Override
	public File getDataFile() {
		return new File(MTGConstants.DATA_DIR, "AllPrintings.sqlite");
	}
	
	
	@Override
	public void init() {
		logger.info("init " + this);
		download();
		pool = new HikariPool();
		pool.init("jdbc:sqlite://"+getDataFile().getAbsolutePath(), "", "", true);
	}

	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition ed, boolean exact)throws IOException {
		
		
		if(att.equalsIgnoreCase(SET_FIELD))
		{
			att=SETCODE;
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
					cards.add(generateCardsFromRs(rs,true));
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
					cards.add(generateCardsFromRs(rs,true));
			}
		} 
		catch (SQLException e) 
		{
			logger.error(e);
		}
		return cards;
	}
	
	
	private void initRotatedCard(MagicCard mc, String name, String side)
	{
		String sql ="SELECT * FROM cards WHERE name like \"%" + name + "%\" and side ='"+side + "' and setCode='"+mc.getCurrentSet().getId()+"'";
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(sql)) 
		{
			try (ResultSet rs = pst.executeQuery())
			{
				rs.next();
				mc.setRotatedCard(generateCardsFromRs(rs,false));
			}
		} 
		catch (SQLException e) 
		{
			logger.error(e);
		}
	}
	
	
	
	private MagicCard generateCardsFromRs(ResultSet rs,boolean load) throws SQLException {
		MagicCard mc = new MagicCard();
				mc.setName(rs.getString(NAME));
				mc.setCmc(rs.getInt(CONVERTED_MANA_COST));
				mc.setCost(rs.getString(MANA_COST));
				mc.setText(rs.getString(TEXT));
				mc.setId(rs.getString(UUID));
				mc.setEdhrecRank(rs.getInt(EDHREC_RANK));
				mc.setFrameVersion(rs.getString(FRAME_VERSION));
				mc.setLayout(MTGLayout.parseByLabel(rs.getString(LAYOUT)));
				mc.setPower(rs.getString(POWER));
				mc.setToughness(rs.getString(TOUGHNESS));
				mc.getRulings().addAll(getRulings(mc.getId()));
				mc.setArtist(rs.getString(ARTIST));
				mc.setFlavor(rs.getString(FLAVOR_TEXT));
				mc.setWatermarks(rs.getString(WATERMARK));
				mc.setOriginalText(rs.getString(ORIGINAL_TEXT));
				mc.setOriginalType(rs.getString(ORIGINAL_TYPE));
				mc.setMkmId(rs.getInt("mcmId"));
				mc.setMtgArenaId(rs.getInt("mtgArenaId"));
				
				if(rs.getString(AVAILABILITY)!=null) {
					mc.setArenaCard(rs.getString(AVAILABILITY).contains("arena"));
					mc.setMtgoCard(rs.getString(AVAILABILITY).contains("mtgo"));
				}
				mc.setOnlineOnly(rs.getBoolean(IS_ONLINE_ONLY));
				mc.setPromoCard(rs.getBoolean(IS_PROMO));
				mc.setOversized(rs.getBoolean(IS_OVERSIZED));
				mc.setReprintedCard(rs.getBoolean(IS_REPRINT));
				mc.setReserved(rs.getBoolean(IS_RESERVED));
				mc.setFlavorName(rs.getString(FLAVOR_NAME));
				mc.setSide(rs.getString("side"));
				mc.setStorySpotlight(rs.getBoolean(IS_STORY_SPOTLIGHT));
				mc.setHasAlternativeDeckLimit(rs.getBoolean(HAS_ALTERNATIVE_DECK_LIMIT));
				mc.setFullArt(rs.getBoolean(IS_FULLART));
				mc.setHasContentWarning(rs.getBoolean(HAS_CONTENT_WARNING));
				
				if(rs.getString(FRAME_EFFECTS)!=null)
				{
					for(String s : rs.getString(FRAME_EFFECTS).split(","))
					{
						try {
							mc.getFrameEffects().add(MTGFrameEffects.parseByLabel(s));
						} catch (Exception e) {
							logger.error("couldn't find frameEffects for " + s);
						}
					}
				}
				
				if(rs.getString(KEYWORDS)!=null)
				{
					for(String s : rs.getString(KEYWORDS).split(","))
					{
						mc.getKeywords().add(new MTGKeyWord(s, MTGKeyWord.TYPE.ABILITIES));
					}
				}
				
				String ci = rs.getString(COLOR_IDENTITY);
				if(ci!=null)
					mc.setColorIdentity(Arrays.asList(ci.split(",")).stream().map(MTGColor::colorByCode).collect(Collectors.toList()));

				ci = rs.getString(COLORS);
				if(ci!=null)
					mc.setColors(Arrays.asList(ci.split(",")).stream().map(MTGColor::colorByCode).collect(Collectors.toList()));

				try {
					mc.setLoyalty(Integer.parseInt(rs.getString(LOYALTY)));
				} catch (NumberFormatException e) {
					mc.setLoyalty(0);
				} 
				String types = rs.getString(SUPERTYPES);
				
				if(types!=null)
				{
					mc.getSupertypes().addAll(Arrays.asList(rs.getString(SUPERTYPES).split(",")));
				}
				
				types = rs.getString(TYPES);
				
				if(types!=null)
				{
					mc.getTypes().addAll(Arrays.asList(rs.getString(TYPES).split(",")));
				}
				
				types = rs.getString(SUBTYPES);
				
				if(types!=null)
				{
					mc.getSubtypes().addAll(Arrays.asList(rs.getString(SUBTYPES).split(",")));
				}
				
				mc.getForeignNames().addAll(getTranslations(mc));
				mc.getLegalities().addAll(getLegalities(mc.getId()));
				
				MagicEdition set = getSetById(rs.getString(SETCODE));
							 set.setNumber(rs.getString(NUMBER));
							 set.setRarity(MTGRarity.rarityByName(rs.getString(RARITY)));
							 set.setFlavor(rs.getString(FLAVOR_TEXT));
							 set.setScryfallId(rs.getString("scryfallId"));
							 set.setMultiverseid(rs.getString("multiverseId"));
							 set.setBorder(rs.getString(BORDER_COLOR));
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
				
				int split = mc.getName().indexOf("/");
				if(split>1 && load)
				{
						mc.setFlavorName(mc.getName());
						if(mc.getSide().equals("a"))
						{
							mc.setName(mc.getFlavorName().substring(0, split).trim());
							initRotatedCard(mc, mc.getFlavorName().substring(split+2).trim(), "b");
						}
						else
						{
							mc.setName(mc.getFlavorName().substring(split+2).trim());
							initRotatedCard(mc, mc.getFlavorName().substring(0, split).trim(),"a");
							
						}
				}
		notify(mc);
		return mc;
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
								 ed.setKeyRuneCode(rs.getString(KEYRUNE_CODE));
								 ed.setOnlineOnly(rs.getBoolean(IS_ONLINE_ONLY));
								 ed.setFoilOnly(rs.getBoolean(IS_FOIL_ONLY));
								 ed.setTcgplayerGroupId(rs.getInt((TCGPLAYER_GROUP_ID)));
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
							names.setGathererId(rs.getInt(MULTIVERSE_ID));
							names.setLanguage(rs.getString(LANGUAGE));
							names.setName(rs.getString(NAME));
							names.setText(rs.getString(TEXT));
							names.setType(rs.getString(TYPE));
							String id = rs.getString(UUID);
							
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
							String id = rs.getString(UUID);
							
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
							String id = rs.getString(UUID);
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
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("Select DISTINCT "+LANGUAGE+" from foreign_data");ResultSet rs = pst.executeQuery())
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
	public List<CardAttribute> loadQueryableAttributs() {
		List<CardAttribute> ret = new ArrayList<>();
		
		try (Connection c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("PRAGMA table_info(cards)");ResultSet rs = pst.executeQuery())
		{
			while(rs.next())
			{
				if(rs.getString(NAME).startsWith("is") || rs.getString(NAME).startsWith("has"))
					ret.add(new CardAttribute(rs.getString(NAME), Boolean.class));
				else if(rs.getString(NAME).equals(SETCODE))
					ret.add(new CardAttribute(rs.getString(NAME), MagicEdition.class));
				else if(rs.getString(NAME).equals(COLORS) || rs.getString(NAME).equals(COLOR_IDENTITY))
					ret.add(new CardAttribute(rs.getString(NAME), MTGColor.class));
				else if(rs.getString(NAME).equals(LAYOUT))
					ret.add(new CardAttribute(rs.getString(NAME), MTGLayout.class));
				else if(rs.getString(NAME).equals(RARITY))
					ret.add(new CardAttribute(rs.getString(NAME), MTGRarity.class));
				else if(rs.getString(NAME).equals(FRAME_EFFECTS))
					ret.add(new CardAttribute(rs.getString(NAME), MTGFrameEffects.class));
				else
					ret.add(new CardAttribute(rs.getString(NAME), sqlToJavaType(rs.getString("type"))));
				
			}
			
			
			ret.add(new CardAttribute("sql",String.class));
			Collections.sort(ret);
			ret.remove(new CardAttribute(NAME,String.class));
			ret.add(0, new CardAttribute(NAME,String.class));
		} 
		catch (SQLException e) {
			logger.error(e);
			
		}
		
		return ret;
	}

	private Class<?> sqlToJavaType(String string) {
		if(string.startsWith("TEXT") || string.startsWith("VARCHAR"))
			return String.class;
		else if(string.startsWith("INTEGER"))
			return Integer.class;
		else if(string.startsWith("BOOL"))
			return Boolean.class;
		else
			return Float.class;
	}

	@Override
	public String getName() {
		return "MTGSQLive";
	}

}
