package org.magic.api.providers.impl;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.text.StringEscapeUtils;
import org.magic.api.beans.MTGBooster;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardNames;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.MTGFormat;
import org.magic.api.beans.MTGFormat.AUTHORIZATION;
import org.magic.api.beans.MTGKeyWord;
import org.magic.api.beans.MTGRuling;
import org.magic.api.beans.enums.EnumBorders;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.enums.EnumExtra;
import org.magic.api.beans.enums.EnumFinishes;
import org.magic.api.beans.enums.EnumFrameEffects;
import org.magic.api.beans.enums.EnumLayout;
import org.magic.api.beans.enums.EnumPromoType;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.beans.enums.EnumSecurityStamp;
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
	private MultiValuedMap<String, MTGCardNames> mapForeignData = new ArrayListValuedHashMap<>();
	private MultiValuedMap<String, MTGRuling> mapRules = new ArrayListValuedHashMap<>();
	private MultiValuedMap<String, MTGFormat> mapLegalities = new ArrayListValuedHashMap<>();
	private String sqlCardBaseQuery = "SELECT cards.*, cardIdentifiers.* FROM cards, cardIdentifiers WHERE cardIdentifiers.uuid=cards.uuid";

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
	
	private List<String> splitArrayValue(String val)
	{
		return List.of(val.split(",")).stream().map(String::trim).filter(s->!s.isEmpty()).toList();
	}
	
	
	@Override
	public List<MTGBooster> generateBooster(MTGEdition me, EnumExtra typeBooster, int qty) throws IOException {
		
		var list = new ArrayList<MTGBooster>();
		
		//get boosters structures for set
		var itemWeights = new ArrayList<Pair<Integer, Double>>();
		try (var c = pool.getConnection(); var pst = c.prepareStatement("select boosterIndex,boosterWeight from setBoosterContentWeights where setCode=? and boosterName=?"))
		{
			pst.setString(1, me.getId());
			pst.setString(2, typeBooster.getMtgjsonname());
			
			try (ResultSet rs = pst.executeQuery())
			{
				while(rs.next())
					itemWeights.add(new Pair<>(rs.getInt(BOOSTER_INDEX), rs.getDouble(BOOSTER_WEIGHT)));
			}
		}
		catch (SQLException e) {
			logger.error(e);
		}
		
		if(itemWeights.isEmpty())
			throw new IOException("No booster found for " + me.getId() + " / " + typeBooster.getMtgjsonname());
		
		var boosters = new EnumeratedDistribution<>(itemWeights);
		var boosterIndex = boosters.sample(qty,new Integer[qty]);
		logger.debug("pick booster {} with index ={}",typeBooster.getMtgjsonname(), Arrays.asList(boosterIndex));

		
		//get cards pickup chance
		var cardsSheets = new HashMap<String,List<Pair<MTGCard, Double>>>();
		try (var c = pool.getConnection(); var pst = c.prepareStatement("SELECT cards.*, cardIdentifiers.*,setBoosterSheetCards.* FROM cards, cardIdentifiers,setBoosterSheetCards WHERE cards.uuid=setBoosterSheetCards.cardUuid AND cardIdentifiers.uuid=cards.uuid AND setBoosterSheetCards.setCode=?"))
		{
			pst.setString(1, me.getId());
			
			try (ResultSet rs = pst.executeQuery())
			{
				while(rs.next())
					cardsSheets.compute(rs.getString(BOOSTER_SHEET_NAME), (k, v) ->v != null ? v : new ArrayList<>()).add(new Pair<>(generateCardsFromRs(rs,true), rs.getDouble(BOOSTER_CARD_WEIGHT)));
			}
		}
		catch (SQLException e) {
			logger.error(e);
		}
		
		if(cardsSheets.isEmpty())
			throw new IOException("No cardsdatasheet found for " + me.getId() + " / " + typeBooster.getMtgjsonname() + " for index=" + Arrays.asList(boosterIndex));
		
		logger.debug("cards loaded for {}/{}",me.getId(),typeBooster.getMtgjsonname());
		
		//build boosters
		for(int i: boosterIndex) 
		{
				Map<String, Integer> boosterStructure = new HashMap<>();
				try (var c = pool.getConnection(); var pst = c.prepareStatement("select sheetName,sheetPicks from setBoosterContents where setCode=? and boosterName=? and boosterIndex=?"))
				{
					pst.setString(1, me.getId());
					pst.setString(2, typeBooster.getMtgjsonname());
					pst.setInt(3, i);
					
					try (var rs = pst.executeQuery())
					{
						while(rs.next())
							boosterStructure.put(rs.getString(BOOSTER_SHEET_NAME),rs.getInt(BOOSTER_SHEET_PICKS));
					}
					if(boosterStructure.isEmpty())
						throw new IOException("No boosterStructure found for " + me.getId() + " / " + typeBooster + " for index=" + boosterIndex);
					
				}
				catch (SQLException e) {
					logger.error(e);
				}
				
				logger.debug("generating boosters for {}/{} with structure = {}",me.getId(),typeBooster.getMtgjsonname(),boosterStructure);
				
				var booster = new MTGBooster();
					  booster.setEdition(me);
					  booster.setTypeBooster(typeBooster);
					  booster.setBoosterNumber(""+i);
					  notify(booster);
					  
				for(var e : boosterStructure.entrySet()){
					var picker = new EnumeratedDistribution<>(cardsSheets.get(e.getKey())).sample(e.getValue(), new MTGCard[e.getValue()]);
					booster.getCards().addAll(Arrays.asList(picker));
				}
				list.add(booster);
		}
		return list;
	}
	

	@Override
	public List<MTGCard> searchByCriteria(MTGCrit<?>... crits) throws IOException {

		List<MTGCard> cards = new ArrayList<>();
		try (var c = pool.getConnection(); var pst = c.createStatement())
		{
			var sql = getMTGQueryManager().build(crits).toString();
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
	public MTGCard getTokenFor(MTGCard mc, EnumLayout layout) throws IOException {
		try (var c = pool.getConnection(); var pst = c.prepareStatement("select tokens.*, scryfallId,scryfallIllustrationId from tokens,tokenIdentifiers where (relatedCards like ? or name like ? ) and types like ? and setCode like ? and tokenIdentifiers.uuid=tokens.uuid"))
		{
			pst.setString(1, "%"+mc.getName()+"%");
			pst.setString(2, "%"+mc.getName()+"%");
			pst.setString(3, "%"+layout.toPrettyString()+"%");
			pst.setString(4, "%"+mc.getEdition().getId().toUpperCase());
			var rs = pst.executeQuery();

			if(rs.next())
				return generateTokenFromRs(rs,mc.getEdition());
		}
		catch(Exception e)
		{
			throw new IOException(e);
		}
		logger.error("No token found for {} with layout={}", mc,layout);
		return null;
	}

	@Override
	public List<MTGCard> listToken(MTGEdition ed) throws IOException {

		var ret= new ArrayList<MTGCard>();

		try (var c = pool.getConnection(); var pst = c.prepareStatement("select tokens.*, scryfallId,scryfallIllustrationId from tokens,tokenIdentifiers where setCode like ? and tokenIdentifiers.uuid=tokens.uuid"))
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


	private MTGCard generateTokenFromRs(ResultSet rs,MTGEdition ed) throws SQLException {
		var mc = new MTGCard();
			mc.setId(rs.getString(UUID));
			mc.setName(rs.getString(NAME));
			
			if(rs.getString(TEXT)!=null)
				mc.setText(rs.getString(TEXT));
			else
				mc.setText("");
			
			mc.setScryfallId(rs.getString(SCRYFALL_ID));
			mc.setScryfallIllustrationId(rs.getString(SCRYFALL_ILLUSTRATION_ID));
			mc.setFrameVersion(rs.getString(FRAME_VERSION));
			mc.setWatermarks(rs.getString(WATERMARK));
			mc.setTypes(splitArrayValue(rs.getString(TYPES)));
			mc.setPower(rs.getString(POWER));
			mc.setToughness(rs.getString(TOUGHNESS));
			mc.setBorder(EnumBorders.parseByLabel(rs.getString(BORDER_COLOR)));
			mc.setArtist(rs.getString(ARTIST));
			mc.setRarity(EnumRarity.COMMON);
			mc.setLayout(EnumLayout.parseByLabel(rs.getString(LAYOUT)));

			if(rs.getString(SUPERTYPES)!=null)
				mc.setSupertypes(splitArrayValue(rs.getString(SUPERTYPES)));

			if(rs.getString(SUBTYPES)!=null)
				mc.setSubtypes(splitArrayValue(rs.getString(SUBTYPES)));

			if(rs.getString(COLOR_IDENTITY)!=null)
				mc.setColorIdentity(splitArrayValue(rs.getString(COLOR_IDENTITY)).stream().map(EnumColors::colorByCode).toList());

			if( rs.getString(COLORS)!=null)
				mc.setColors(splitArrayValue( rs.getString(COLORS)).stream().map(EnumColors::colorByCode).toList());

			if(rs.getString(KEYWORDS)!=null)
				mc.getKeywords().addAll(splitArrayValue(KEYWORDS).stream().map(s->new MTGKeyWord(s, MTGKeyWord.TYPE.ABILITIES)).toList());
		
				mc.setNumber(rs.getString(NUMBER));
				mc.getEditions().add(getSetById(ed.getId()));




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
	public List<MTGCard> searchCardByCriteria(String att, String crit, MTGEdition ed, boolean exact)throws IOException {


		if(att.equalsIgnoreCase(SET_FIELD))
		{
			att=SETCODE;
			exact=true;
		}

		var temp = new StringBuilder(sqlCardBaseQuery).append(" AND ").append(att.equals("uuid")?"cards.":"").append(att);


		if(exact)
			temp.append(" = ");
		else
			temp.append(" like ");

		temp.append("?");

		if(ed!=null && !ed.getId().isEmpty())
			temp.append(" AND "+SETCODE+" ='").append(ed.getId()).append("'");

		List<MTGCard> cards = new ArrayList<>();
		
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
	public List<MTGCard> listAllCards()throws IOException {
		List<MTGCard> cards = new ArrayList<>();

	try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement(sqlCardBaseQuery ))
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


	private void initRotatedCard(MTGCard mc, String id, String side)
	{
		var sql =sqlCardBaseQuery+" AND cards.uuid = ?" ;
		
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

	private MTGCard generateCardsFromRs(ResultSet rs,boolean load) throws SQLException {
		var mc = new MTGCard();
				mc.setName(rs.getString(NAME));
				mc.setCmc(rs.getInt(CONVERTED_MANA_COST));
				mc.setCost(rs.getString(MANA_COST));
				
				mc.setId(rs.getString(UUID));
				mc.setEdhrecRank(rs.getInt(EDHREC_RANK));
				mc.setFrameVersion(rs.getString(FRAME_VERSION));
				mc.setLayout(EnumLayout.parseByLabel(rs.getString(LAYOUT)));
				mc.setPower(rs.getString(POWER));
				mc.setToughness(rs.getString(TOUGHNESS));
				mc.getRulings().addAll(getRulings(mc.getId()));
				mc.setArtist(rs.getString(ARTIST));
				
				if(rs.getString(FLAVOR_TEXT)!=null)
					mc.setFlavor(StringEscapeUtils.unescapeJava(rs.getString(FLAVOR_TEXT)));
				
				mc.setWatermarks(rs.getString(WATERMARK));
				mc.setAsciiName(rs.getString(ASCII_NAME));
				
				if(rs.getString(TEXT)!=null)
					mc.setText(StringEscapeUtils.unescapeJava(rs.getString(TEXT)));
				else
					mc.setText("");
				
				if(rs.getString(AVAILABILITY)!=null) {
					mc.setArenaCard(rs.getString(AVAILABILITY).contains("arena"));
					mc.setMtgoCard(rs.getString(AVAILABILITY).contains("mtgo"));
				}
				mc.setMkmId(rs.getInt(MCM_ID));
				mc.setMtgArenaId(rs.getInt("mtgArenaId"));
				
				
				mc.setOnlineOnly(rs.getBoolean(IS_ONLINE_ONLY));
				mc.setPromoCard(rs.getBoolean(IS_PROMO));
				mc.setOversized(rs.getBoolean(IS_OVERSIZED));
				mc.setReprintedCard(rs.getBoolean(IS_REPRINT));
				mc.setReserved(rs.getBoolean(IS_RESERVED));
				mc.setFlavorName(rs.getString(FLAVOR_NAME));
				mc.setSide(rs.getString(SIDE)!=null?rs.getString(SIDE):"a");
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
				mc.setSecurityStamp(EnumSecurityStamp.parseByLabel(rs.getString(SECURITYSTAMP)));
				mc.setRebalanced(rs.getBoolean(IS_REBALANCED));
				mc.setTcgPlayerId(rs.getInt(TCGPLAYER_PRODUCT_ID));
				mc.setSignature(rs.getString(SIGNATURE));
				mc.setDefense(rs.getInt(DEFENSE));

				if(rs.getString(FINISHES)!=null)
					mc.getFinishes().addAll(splitArrayValue(rs.getString(FINISHES)).stream().map(EnumFinishes::parseByLabel).toList());
	
				if(rs.getString(FRAME_EFFECTS)!=null)
					mc.getFrameEffects().addAll(splitArrayValue(rs.getString(FRAME_EFFECTS)).stream().map(EnumFrameEffects::parseByLabel).toList());
					
			
				if(rs.getString(PROMO_TYPE)!=null)
					mc.getPromotypes().addAll(splitArrayValue(rs.getString(PROMO_TYPE)).stream().map(EnumPromoType::parseByLabel).toList());
			
				if(rs.getString(KEYWORDS)!=null)
					mc.getKeywords().addAll(splitArrayValue(KEYWORDS).stream().map(s->new MTGKeyWord(s, MTGKeyWord.TYPE.ABILITIES)).toList());
			
				if(rs.getString(COLOR_IDENTITY)!=null)
					mc.setColorIdentity(splitArrayValue(rs.getString(COLOR_IDENTITY)).stream().map(EnumColors::colorByCode).toList());

				if(rs.getString(COLORS)!=null)
					mc.setColors(splitArrayValue(rs.getString(COLORS)).stream().map(EnumColors::colorByCode).toList());

				if(rs.getString(COLOR_INDICATOR)!=null)
					mc.setColorIndicator(splitArrayValue(rs.getString(COLOR_INDICATOR)).stream().map(EnumColors::colorByCode).toList());
				
				if(rs.getString(SUPERTYPES)!=null)
					mc.getSupertypes().addAll(splitArrayValue(rs.getString(SUPERTYPES)));

				if(rs.getString(TYPES)!=null)
					mc.getTypes().addAll(splitArrayValue(rs.getString(TYPES)));
			
				if(rs.getString(SUBTYPES)!=null)
					mc.getSubtypes().addAll(splitArrayValue(rs.getString(SUBTYPES)));
				
				try {
					mc.setLoyalty(Integer.parseInt(rs.getString(LOYALTY)));
				} catch (NumberFormatException e) {
					mc.setLoyalty(0);
				}
				
		
				mc.getForeignNames().addAll(getTranslations(mc));
				mc.getLegalities().addAll(getLegalities(mc.getId()));
				mc.setNumber(rs.getString(NUMBER));
				mc.setMultiverseid(rs.getString(MULTIVERSE_ID));
				
				var set = getSetById(rs.getString(SETCODE));
							 
				mc.getEditions().add(set);
				mc.setEdition(set);

				if(rs.getString("printings")!=null)
					for(String ids : splitArrayValue(rs.getString("printings")))
					{
						if(!ids.equals(set.getId()))
						{
							mc.getEditions().add(getSetById(ids));
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
	public List<MTGEdition> loadEditions() throws IOException {

		List<MTGEdition> eds=new ArrayList<>();
			try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("select * from sets");ResultSet rs = pst.executeQuery())
			{

				while(rs.next())
				{

					var ed = new MTGEdition();
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
								 eds.add(ed);
				}
			}
			catch (SQLException e) {
				throw new IOException(e);
			}
		return eds;
	}

	private void testMkm(MTGEdition ed, ResultSet rs) {


		 try {
			ed.setMkmName(rs.getString(MCM_NAME));
			ed.setMkmid(rs.getInt(MCM_ID));
		} catch (SQLException e) {
			//do nothing
		}
	}

	private List<MTGCardNames> getTranslations(MTGCard mc) {

		var defaultName = new MTGCardNames();
		defaultName.setFlavor(mc.getFlavor());
		try{
			defaultName.setGathererId(Integer.parseInt(mc.getMultiverseid()));
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

		return (List<MTGCardNames>) mapForeignData.get(mc.getId());

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
		
		List<String> formats = new ArrayList<>();
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("PRAGMA table_info(cardLegalities)");ResultSet rs = pst.executeQuery())
		{
			while(rs.next())
			{
				var format = rs.getString("name");
				if(!format.equals("uuid"))
				{
					formats.add(format);
				}

			}
				
		}
		catch (SQLException e) {
			logger.error("error loading legalities",e);
		}
		
		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * FROM cardLegalities"))
		{
			try (ResultSet rs = pst.executeQuery())
			{
				while(rs.next())
				{
					var id = rs.getString(UUID);
					
					for(String f : formats)
					{
						if(rs.getString(f)!=null)
							mapLegalities.put(id, new MTGFormat(f, AUTHORIZATION.valueOf(rs.getString(f).toUpperCase())));
					}
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
							var names = new MTGCardNames();
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
		}
		catch (SQLException e) {
			logger.error(e);

		}

		try (var c = pool.getConnection(); PreparedStatement pst = c.prepareStatement("PRAGMA table_info(cardIdentifiers)");ResultSet rs = pst.executeQuery())
		{
			while(rs.next())
			{
					ret.add(new QueryAttribute(rs.getString(NAME), String.class));
			}
		}
		catch (SQLException e) {
			logger.error(e);

		}

		Collections.sort(ret);
		ret.remove(new QueryAttribute(NAME,String.class));
		ret.add(0, new QueryAttribute(NAME,String.class));
		
		
		
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
