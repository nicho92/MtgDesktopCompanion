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
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.MagicFormat.AUTHORIZATION;
import org.magic.api.beans.MagicRuling;
import org.magic.api.beans.enums.MTGColor;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.beans.enums.MTGRarity;
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
	public MTGQueryBuilder<?> getMTGQueryManager() {
		return new SQLCriteriaBuilder();
	}

	
	@Override
	public String getOnlineDataFileZip() {
		return "https://mtgjson.com/api/v5/AllPrintings.sqlite.zip";
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
				mc.setCmc(rs.getInt("convertedManaCost"));
				mc.setCost(rs.getString("manaCost"));
				mc.setText(rs.getString("text"));
				mc.setId(rs.getString(UUID));
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
				mc.setMkmId(rs.getInt("mcmId"));
				mc.setMtgArenaId(rs.getInt("mtgArenaId"));
				mc.setArenaCard(rs.getString("availability").contains("arena"));
				mc.setMtgoCard(rs.getString("availability").contains("mtgo"));
				mc.setOnlineOnly(rs.getBoolean("isOnlineOnly"));
				mc.setPromoCard(rs.getBoolean("isPromo"));
				mc.setOversized(rs.getBoolean("isOversized"));
				mc.setReprintedCard(rs.getBoolean("isReprint"));
				mc.setReserved(rs.getBoolean("isReserved"));
				mc.setFlavorName(rs.getString("flavorName"));
				mc.setSide(rs.getString("side"));
				mc.setStorySpotlight(rs.getBoolean("isStorySpotlight"));
				mc.setHasAlternativeDeckLimit(rs.getBoolean("hasAlternativeDeckLimit"));
				
				
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
				
				mc.getForeignNames().addAll(getTranslations(mc));
				mc.getLegalities().addAll(getLegalities(mc.getId()));
				
				
				
				
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
	public String getName() {
		return "MTGSQLive";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

}
