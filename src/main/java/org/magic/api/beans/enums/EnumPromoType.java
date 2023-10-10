package org.magic.api.beans.enums;

import org.magic.api.interfaces.MTGEnumeration;

import com.google.gson.annotations.SerializedName;

public enum EnumPromoType implements MTGEnumeration{



	@SerializedName(alternate = "arenaleague", value = "ARENALEAGUE") 		ARENALEAGUE,
	@SerializedName(alternate = "boosterfun", value = "BOOSTERFUN")   		BOOSTERFUN,
	@SerializedName(alternate = "boxtopper", value = "BOXTOPPER") 	  		BOXTOPPER,
	@SerializedName(alternate = "brawldeck", value = "BRAWLDECK") 	 		BRAWLDECK,
	@SerializedName(alternate = "bundle", value = "BUNDLE") 		  		BUNDLE,
	@SerializedName(alternate = "buyabox", value = "BUYABOX") 		  		BUYABOX,
	@SerializedName(alternate = "convention", value = "CONVENTION")   		CONVENTION,
	@SerializedName(alternate = "datestamped", value = "DATESTAMPED")   	DATESTAMPED,
	@SerializedName(alternate = "draftweekend", value = "DRAFTWEEKEND")	 	DRAFTWEEKEND,
	@SerializedName(alternate = "duels", value = "DUELS") 					DUELS,
	@SerializedName(alternate = "event", value = "EVENT") 					EVENT,
	@SerializedName(alternate = "fnm", value = "FNM") 						FNM,
	@SerializedName(alternate = "gameday", value = "GAMEDAY") 				GAMEDAY,
	@SerializedName(alternate = "gateway", value = "GATEWAY") 				GATEWAY,
	@SerializedName(alternate = "giftbox", value = "GIFTBOX") 				GIFTBOX,
	@SerializedName(alternate = "godzillaseries", value = "GODZILLASERIES") GODZILLASERIES,
	@SerializedName(alternate = "instore", value = "INSTORE") 				INSTORE,
	@SerializedName(alternate = "intropack", value = "INTROPACK") 			INTROPACK,
	@SerializedName(alternate = "jpwalker", value = "JPWALKER") 			JPWALKER,
	@SerializedName(alternate = "judgegift", value = "JUDGEGIFT") 			JUDGEGIFT,
	@SerializedName(alternate = "league", value = "LEAGUE") 				LEAGUE,
	@SerializedName(alternate = "mediainsert", value = "MEDIAINSERT") 		MEDIAINSERT,
	@SerializedName(alternate = "openhouse", value = "OPENHOUSE") 			OPENHOUSE,
	@SerializedName(alternate = "planeswalkerstamped", value = "PLANESWALKERSTAMPED") 			PLANESWALKERSTAMPED,
	@SerializedName(alternate = "playerrewards", value = "PLAYERREWARDS") 	PLAYERREWARDS,
	@SerializedName(alternate = "premiereshop", value = "PREMIERESHOP") 	PREMIERESHOP,
	@SerializedName(alternate = "prerelease", value = "PRERELEASE") 		PRERELEASE,
	@SerializedName(alternate = "promopack", value = "PROMOPACK") 			PROMOPACK,
	@SerializedName(alternate = "promostamped", value = "PROMOSTAMPED") 	PROMOSTAMPED,
	@SerializedName(alternate = "release", value = "RELEASE") 				RELEASE,
	@SerializedName(alternate = "setpromo", value = "SETPROMO") 			SETPROMO,
	@SerializedName(alternate = "themepack", value = "THEMEPACK") 			THEMEPACK,
	@SerializedName(alternate = "tourney", value = "TOURNEY") 				TOURNEY,
	@SerializedName(alternate = "wizardsplaynetwork", value = "WIZARDSPLAYNETWORK") WIZARDSPLAYNETWORK,
	@SerializedName(alternate = "draculaseries", value = "DRACULASERIES") DRACULASERIES,
	@SerializedName(alternate = "starterdeck", value = "STARTERDECK") STARTERDECK,
	@SerializedName(alternate = "planeswalkerdeck", value = "PLANESWALKERDECK") PLANESWALKERDECK,
	@SerializedName(alternate = "serialized", value = "SERIALIZED") SERIALIZED,
	@SerializedName(alternate = "halofoil", value = "HALOFOIL") HALOFOIL,
	@SerializedName(alternate = "thick", value = "THICK") THICK,
	@SerializedName(alternate = "textured", value = "TEXTURED") TEXTURED,
	@SerializedName(alternate = "stamped", value = "STAMPED") STAMPED,
	@SerializedName(alternate = "rebalanced", value = "REBALANCED") REBALANCED,
	@SerializedName(alternate = "alchemy", value = "ALCHEMY") ALCHEMY,
	@SerializedName(alternate = "stepandcompleat", value = "STEPANDCOMPLEAT") STEPANDCOMPLEAT,
	@SerializedName(alternate = "doublerainbow", value = "DOUBLERAINBOW") DOUBLERAINBOW,
	@SerializedName(alternate = "galaxyfoil", value = "GALAXYFOIL") GALAXYFOIL,
	@SerializedName(alternate = "confettifoil", value = "CONFETTIFOIL") CONFETTIFOIL,
	@SerializedName(alternate = "surgefoil", value = "SURGEFOIL") SURGEFOIL,
	@SerializedName(alternate = "concept", value = "CONCEPT") CONCEPT,
	@SerializedName(alternate = "setextension", value = "SETEXTENSION") SETEXTENSION,
	@SerializedName(alternate = "gilded", value = "GILDED") GILDED,
	@SerializedName(alternate = "commanderparty", value = "COMMANDERPARTY") COMMANDERPARTY,
	
	@SerializedName(alternate = "neonink", value = "NEONINK") NEONINK;
	

	public static EnumPromoType parseByLabel(String s)
	{
		try {
			return EnumPromoType.valueOf(s.trim().toUpperCase());
		}
		catch(Exception e)
		{
			logger.warn("EnumPromoType {} is not found", s);
			return null;
		}
	}

}
