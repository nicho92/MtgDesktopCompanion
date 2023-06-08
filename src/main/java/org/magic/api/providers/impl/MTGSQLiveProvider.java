package org.magic.api.providers.impl;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.MTGFormat.AUTHORIZATION;
import org.magic.api.beans.MTGKeyWord;
import org.magic.api.beans.MTGRuling;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumBorders;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.enums.EnumFinishes;
import org.magic.api.beans.enums.EnumFrameEffects;
import org.magic.api.beans.enums.EnumLayout;
import org.magic.api.beans.enums.EnumPromoType;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.criterias.MTGCrit;
import org.magic.api.criterias.MTGQueryBuilder;
import org.magic.api.criterias.QueryAttribute;
import org.magic.api.criterias.builders.SQLCriteriaBuilder;
import org.magic.api.interfaces.MTGPool;
import org.magic.api.interfaces.abstracts.extra.AbstractMTGJsonProvider;
import org.magic.api.pool.impl.HikariPool;
import org.magic.services.MTGConstants;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;

public class MTGSQLiveProvider extends AbstractMTGJsonProvider {

	private MTGPool pool;
	private MultiValuedMap<String, MagicCardNames> mapForeignData = new ArrayListValuedHashMap<>();
	private MultiValuedMap<String, MTGRuling> mapRules = new ArrayListValuedHashMap<>();
	private MultiValuedMap<String, MTGFormat> mapLegalities = new ArrayListValuedHashMap<>();

	@Override
	public String getOnlineDataFileZip() {
		return MTGJSON_API_URL+"/AllPrintings.sqlite.zip";
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
		try (var c = pool.getConnection(); Statement pst = c.createStatement())
		{
			var sql = getMTGQueryManager().build(crits).toString();
			logger.debug("sql={}",sql);
			try (ResultSet rs = pst.executeQuery(sql))
			{
				while(rs.next())
					cards.add(generateCardsFromRs(rs,true));
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
	public MagicCard getTokenFor(MagicCard mc, EnumLayout layout) throws IOException {
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("select * from tokens where (relatedCards like ? or name like ? ) and types like ? and setCode like ?"))
		{
			pst.setString(1, "%"+mc.getName()+"%");
			pst.setString(2, "%"+mc.getName()+"%");
			pst.setString(3, "%"+layout.toPrettyString()+"%");
			pst.setString(4, "%"+mc.getCurrentSet().getId().toUpperCase());
			var rs = pst.executeQuery();

			if(rs.next())
				return generateTokenFromRs(rs,mc.getCurrentSet());
		}
		catch(Exception e)
		{
			throw new IOException(e);
		}
		logger.error("No token found for {} with layout={}", mc,layout);
		return null;
	}

	@Override
	public List<MagicCard> listToken(MagicEdition ed) throws IOException {

		var ret= new ArrayList<MagicCard>();

		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("select * from tokens where setCode like ?"))
		{
			pst.setString(1, "%"+ed.getId().toUpperCase());
			var rs = pst.executeQuery();

			while(rs.next())
			{
				ret.add(generateTokenFromRs(rs,ed));
			}
		}
		catch(Exception e)
		{
			throw new IOException(e);
		}
		return ret;

	}


	private MagicCard generateTokenFromRs(ResultSet rs,MagicEdition ed) throws SQLException {
		var mc = new MagicCard();
			mc.setId(rs.getString(UUID));
			mc.setName(rs.getString(NAME));
			mc.setText(rs.getString(TEXT));
			mc.setScryfallId(rs.getString(SCRYFALL_ID));
			mc.setScryfallIllustrationId(rs.getString(SCRYFALL_ILLUSTRATION_ID));

			mc.setFrameVersion(rs.getString(FRAME_VERSION));
			mc.setWatermarks(rs.getString(WATERMARK));
			mc.setTypes(List.of(rs.getString(TYPES).split(",")));
			mc.setPower(rs.getString(POWER));
			mc.setToughness(rs.getString(TOUGHNESS));
			mc.setBorder(EnumBorders.parseByLabel(rs.getString(BORDER_COLOR)));
			mc.setArtist(rs.getString(ARTIST));
			mc.setRarity(EnumRarity.COMMON);
			mc.setLayout(EnumLayout.parseByLabel(rs.getString(LAYOUT)));


			if(rs.getString(SUPERTYPES)!=null)
				mc.setSupertypes(List.of(rs.getString(SUPERTYPES).split(",")));

			if(rs.getString(SUBTYPES)!=null)
				mc.setSubtypes(List.of(rs.getString(SUBTYPES).split(",")));

			var ci = rs.getString(COLOR_IDENTITY);
			if(ci!=null)
				mc.setColorIdentity(Arrays.asList(ci.split(",")).stream().map(EnumColors::colorByCode).toList());

			ci = rs.getString(COLORS);
			if(ci!=null)
				mc.setColors(Arrays.asList(ci.split(",")).stream().map(EnumColors::colorByCode).toList());

			if(rs.getString(KEYWORDS)!=null)
				for(String s : rs.getString(KEYWORDS).split(","))
				{
					mc.getKeywords().add(new MTGKeyWord(s, MTGKeyWord.TYPE.ABILITIES));
				}

			var ted = getSetById(ed.getId());
				ted.setNumber(rs.getString(NUMBER));

				mc.getEditions().add(ted);




		return mc;
	}

	@Override
	public void init() {
		logger.info("init {} provider",this);
		download();
		pool = new HikariPool();
		pool.init("jdbc:sqlite://"+getDataFile().getAbsolutePath(), "", "", true);

		ThreadManager.getInstance().executeThread(new MTGRunnable() {

			@Override
			protected void auditedRun() {
				logger.debug("Loading {} extra data cards in background",getName());
				initForeign();
				initLegalities();
				initRules();

			}
		}, getName() + "extradata loading");


	}

	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition ed, boolean exact)throws IOException {


		if(att.equalsIgnoreCase(SET_FIELD))
		{
			att=SETCODE;
			exact=true;
		}

		StringBuilder temp = new StringBuilder("SELECT * FROM cards, cardIdentifiers WHERE cardIdentifiers.uuid=cards.uuid AND ").append(att);


		if(exact)
			temp.append(" = ");
		else
			temp.append(" like ");

		temp.append("?");

		if(ed!=null && !ed.getId().isEmpty())
			temp.append(" AND "+SETCODE+" ='").append(ed.getId()).append("'");

		List<MagicCard> cards = new ArrayList<>();
		
		logger.debug("executing {}",temp);
		
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(temp.toString()))
		{
				if(exact)
					pst.setString(1, crit);
				else
					pst.setString(1, "%"+crit+"%");

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

		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * from cards"))
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


	private void initRotatedCard(MagicCard mc, String id, String side)
	{
		var sql ="SELECT * FROM cards WHERE uuid = ?" ;
		
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(sql))
		{
			pst.setString(1, id);

			try (ResultSet rs = pst.executeQuery())
			{
				rs.next();
				mc.setRotatedCard(generateCardsFromRs(rs,false));
			}

			String name = mc.getName();
			if(side.equals("b"))
			{
				mc.setName(name.substring(name.indexOf('/')+2).trim());
				mc.getRotatedCard().setName(name.substring(0,name.indexOf('/')).trim());
			}
			else
			{
				mc.getRotatedCard().setName(name.substring(name.indexOf('/')+2).trim());
				mc.setName(name.substring(0,name.indexOf('/')).trim());

			}


		}
		catch (Exception e)
		{
			logger.error("Error generating rotatedcard for {} id={} side={} msg={}",mc,id,side,e.getMessage());
		}
	}

	private MagicCard generateCardsFromRs(ResultSet rs,boolean load) throws SQLException {
		var mc = new MagicCard();
				mc.setName(rs.getString(NAME));
				mc.setCmc(rs.getInt(CONVERTED_MANA_COST));
				mc.setCost(rs.getString(MANA_COST));
				mc.setText(rs.getString(TEXT));
				mc.setId(rs.getString(UUID));
				mc.setEdhrecRank(rs.getInt(EDHREC_RANK));
				mc.setFrameVersion(rs.getString(FRAME_VERSION));
				mc.setLayout(EnumLayout.parseByLabel(rs.getString(LAYOUT)));
				mc.setPower(rs.getString(POWER));
				mc.setToughness(rs.getString(TOUGHNESS));
				mc.getRulings().addAll(getRulings(mc.getId()));
				mc.setArtist(rs.getString(ARTIST));
				mc.setFlavor(rs.getString(FLAVOR_TEXT));
				mc.setWatermarks(rs.getString(WATERMARK));
				mc.setMkmId(rs.getInt(MCM_ID));
				mc.setMtgArenaId(rs.getInt("mtgArenaId"));
				mc.setAsciiName(rs.getString(ASCII_NAME));
				
				
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
				mc.setSide(rs.getString(SIDE));
				mc.setStorySpotlight(rs.getBoolean(IS_STORY_SPOTLIGHT));
				mc.setHasAlternativeDeckLimit(rs.getBoolean(HAS_ALTERNATIVE_DECK_LIMIT));
				mc.setFullArt(rs.getBoolean(IS_FULLART));
				mc.setHasContentWarning(rs.getBoolean(HAS_CONTENT_WARNING));
				mc.setScryfallId(rs.getString(SCRYFALL_ID));
				mc.setBorder(EnumBorders.parseByLabel(rs.getString(BORDER_COLOR)));
				mc.setOriginalReleaseDate(rs.getString(ORIGINAL_RELEASE_DATE));
				mc.setTimeshifted(rs.getBoolean(TIMESHIFTED));
				mc.setRarity(EnumRarity.rarityByName(rs.getString(RARITY)));
				mc.setFunny(rs.getBoolean(IS_FUNNY));
				mc.setSecurityStamp(rs.getString(SECURITYSTAMP));
				mc.setRebalanced(rs.getBoolean(IS_REBALANCED));
				mc.setTcgPlayerId(rs.getInt(TCGPLAYER_PRODUCT_ID));
				mc.setSignature(rs.getString(SIGNATURE));
				mc.setDefense(rs.getInt(DEFENSE));

				if(rs.getString(FINISHES)!=null)
				{
					for(String s : rs.getString(FINISHES).split(","))
					{
						try {
							mc.getFinishes().add(EnumFinishes.parseByLabel(s));
						} catch (Exception e) {
							logger.error("couldn't find finishes for {}", s);
						}
					}
				}

				if(rs.getString(FRAME_EFFECTS)!=null)
				{
					for(String s : rs.getString(FRAME_EFFECTS).split(","))
					{
						try {
							mc.getFrameEffects().add(EnumFrameEffects.parseByLabel(s));
						} catch (Exception e) {
							logger.error("couldn't find frameEffects for {}",s);
						}
					}
				}

				if(rs.getString(PROMO_TYPE)!=null)
				{
					for(String s : rs.getString(PROMO_TYPE).split(","))
					{
						mc.getPromotypes().add(EnumPromoType.parseByLabel(s));
					}
				}


				if(rs.getString(KEYWORDS)!=null)
				{
					for(String s : rs.getString(KEYWORDS).split(","))
					{
						mc.getKeywords().add(new MTGKeyWord(s, MTGKeyWord.TYPE.ABILITIES));
					}
				}

				
				
				
				var ci = rs.getString(COLOR_IDENTITY);
				if(ci!=null)
					mc.setColorIdentity(Arrays.asList(ci.split(",")).stream().map(EnumColors::colorByCode).toList());

				ci = rs.getString(COLORS);
				if(ci!=null)
					mc.setColors(Arrays.asList(ci.split(",")).stream().map(EnumColors::colorByCode).toList());

				ci = rs.getString(COLOR_INDICATOR);
				if(ci!=null)
					mc.setColorIndicator(Arrays.asList(ci.split(",")).stream().map(EnumColors::colorByCode).toList());

				try {
					mc.setLoyalty(Integer.parseInt(rs.getString(LOYALTY)));
				} catch (NumberFormatException e) {
					mc.setLoyalty(0);
				}
				var types = rs.getString(SUPERTYPES);

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
							 set.setMultiverseid(rs.getString(MULTIVERSE_ID));
							 mc.getEditions().add(set);
							 mc.setEdition(set);

				if(rs.getString("printings")!=null)
					for(String ids : rs.getString("printings").split(","))
					{
						if(!ids.equals(set.getId()))
						{
							MagicEdition ed = getSetById(ids);
							mc.getEditions().add(ed);
						}
					}

				int split = mc.getName().indexOf("/");
				if(split>1 && load)
				{
					initRotatedCard(mc, rs.getString("otherFaceIds"),mc.getSide());
				}

		postTreatmentCard(mc);

		notify(mc);
		return mc;
	}

	@Override
	public List<MagicEdition> loadEditions() throws IOException {

		List<MagicEdition> eds=new ArrayList<>();
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("select * from sets");ResultSet rs = pst.executeQuery())
			{

				while(rs.next())
				{

					var ed = new MagicEdition();
								 ed.setSet(rs.getString(NAME));
								 ed.setId(rs.getString("code"));
								 ed.setBlock(rs.getString("block"));
								 ed.setReleaseDate(rs.getString("releaseDate"));
								 ed.setCardCount(rs.getInt("totalSetSize"));
								 ed.setCardCountOfficial(rs.getInt("baseSetSize"));
								 ed.setType(rs.getString("type"));
								 testMkm(ed,rs);
								 ed.setKeyRuneCode(rs.getString(KEYRUNE_CODE));
								 ed.setOnlineOnly(rs.getBoolean(IS_ONLINE_ONLY));
								 ed.setFoilOnly(rs.getBoolean(IS_FOIL_ONLY));
								 ed.setTcgplayerGroupId(rs.getInt((TCGPLAYER_GROUP_ID)));
								 ed.setForeignOnly(rs.getBoolean(IS_FOREIGN_ONLY));
								 ed.setPreview(LocalDate.parse(ed.getReleaseDate(),DateTimeFormatter.ofPattern("yyyy-MM-dd")).isAfter(LocalDate.now()));
								// initTranslations(ed);
								 eds.add(ed);
				}
			}
			catch (SQLException e) {
				throw new IOException(e);
			}
		return eds;
	}

	private void testMkm(MagicEdition ed, ResultSet rs) {


		 try {
			ed.setMkmName(rs.getString(MCM_NAME));
			ed.setMkmid(rs.getInt(MCM_ID));
		} catch (SQLException e) {
			//do nothing
		}


	}

	private List<MagicCardNames> getTranslations(MagicCard mc) {

		var defaultName = new MagicCardNames();
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
			initForeign();

		return (List<MagicCardNames>) mapForeignData.get(mc.getId());

	}

	private void initRules()
	{
		logger.debug("rulings empty. Loading it");
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM cardRulings"))
		{
			try (ResultSet rs = pst.executeQuery())
			{
				while(rs.next())
				{
					var names = new MTGRuling();
					names.setText(rs.getString("text"));
					names.setDate(rs.getString("date"));
					var id = rs.getString(UUID);

					mapRules.put(id, names);
				}
			}

		} catch (SQLException e) {
			logger.error("error loading rules" ,e);
		}
	}


	private void initLegalities()
	{
		logger.debug("legalities empty. Loading it");
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM cardLegalities"))
		{
			try (ResultSet rs = pst.executeQuery())
			{
				while(rs.next())
				{
					var id = rs.getString(UUID);
					mapLegalities.put(id, new MTGFormat(rs.getString("format"), AUTHORIZATION.valueOf(rs.getString("status").toUpperCase())));
				}
			}

		} catch (SQLException e) {
			logger.error("error loading legalities",e);
		}
	}

	private void initForeign() {


			logger.debug("foreignData empty. Loading it");
				try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM cardForeignData"))
				{
					try (ResultSet rs = pst.executeQuery())
					{
						while(rs.next())
						{
							var names = new MagicCardNames();
							names.setFlavor(rs.getString(FLAVOR_TEXT));
							names.setGathererId(rs.getInt(MULTIVERSE_ID));
							names.setLanguage(rs.getString(LANGUAGE));
							names.setName(rs.getString(NAME));
							names.setText(rs.getString(TEXT));
							names.setType(rs.getString(TYPE));
							var id = rs.getString(UUID);

							mapForeignData.put(id, names);
						}
					}

				} catch (SQLException e) {
					logger.error("error loading foreignData",e);
				}


	}

	private List<MTGRuling> getRulings(String uuid) {
		if(mapRules.isEmpty())
			initRules();

		return (List<MTGRuling>) mapRules.get(uuid);
	}

	private List<MTGFormat> getLegalities(String uuid){
		if(mapLegalities.isEmpty())
			initLegalities();


		return (List<MTGFormat>) mapLegalities.get(uuid);
	}

//	private void initTranslations(MagicEdition ed)
//	{
//
//		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM setTranslations WHERE "+SETCODE+"=?"))
//		{
//			pst.setString(1, ed.getId());
//			try (ResultSet rs = pst.executeQuery())
//			{
//				while(rs.next())
//					ed.getTranslations().put(rs.getString(LANGUAGE), rs.getString("translation"));
//			}
//
//		} catch (SQLException e) {
//			logger.error("error getting translation for {}",ed ,e);
//		}
//	}

	@Override
	public String[] getLanguages() {
		List<String> ret = new ArrayList<>();
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("Select DISTINCT "+LANGUAGE+" from cardForeignData");ResultSet rs = pst.executeQuery())
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
	public List<QueryAttribute> loadQueryableAttributs() {
		List<QueryAttribute> ret = new ArrayList<>();

		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("PRAGMA table_info(cards)");ResultSet rs = pst.executeQuery())
		{
			while(rs.next())
			{
				if(rs.getString(NAME).startsWith("is") || rs.getString(NAME).startsWith("has"))
					ret.add(new QueryAttribute(rs.getString(NAME), Boolean.class));
				else if(rs.getString(NAME).equals(SETCODE))
					ret.add(new QueryAttribute(rs.getString(NAME), String.class));
				else if(rs.getString(NAME).equals(COLORS) || rs.getString(NAME).equals(COLOR_IDENTITY) || rs.getString(NAME).equals(COLOR_INDICATOR))
					ret.add(new QueryAttribute(rs.getString(NAME), EnumColors.class));
				else if(rs.getString(NAME).equals(LAYOUT))
					ret.add(new QueryAttribute(rs.getString(NAME), EnumLayout.class));
				else if(rs.getString(NAME).equals(RARITY))
					ret.add(new QueryAttribute(rs.getString(NAME), EnumRarity.class));
				else if(rs.getString(NAME).equals(FRAME_EFFECTS))
					ret.add(new QueryAttribute(rs.getString(NAME), EnumFrameEffects.class));
				else if(rs.getString(NAME).equals(PROMO_TYPE))
					ret.add(new QueryAttribute(rs.getString(NAME), EnumPromoType.class));
				else if(rs.getString(NAME).equals(FINISHES))
					ret.add(new QueryAttribute(rs.getString(NAME), EnumFinishes.class));
				else if(rs.getString(NAME).equals(KEYWORDS))
					ret.add(new QueryAttribute(rs.getString(NAME), MTGKeyWord.class));
				else
					ret.add(new QueryAttribute(rs.getString(NAME), sqlToJavaType(rs.getString("type"))));
			}
			Collections.sort(ret);
			ret.remove(new QueryAttribute(NAME,String.class));
			ret.add(0, new QueryAttribute(NAME,String.class));


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
