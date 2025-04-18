package org.magic.api.beans.enums;

import org.magic.api.interfaces.extra.MTGEnumeration;

import com.google.gson.annotations.SerializedName;

public enum EnumPromoType implements MTGEnumeration{



	@SerializedName(alternate = "alchemy", value = "ALCHEMY") ALCHEMY,
	@SerializedName(alternate = "ampersand", value = "AMPERSAND") AMPERSAND,
	@SerializedName(alternate = "arenaleague", value = "ARENALEAGUE") 		ARENALEAGUE,
	@SerializedName(alternate = "beginnerbox", value = "BEGINNERBOX") 		BEGINNERBOX,
	@SerializedName(alternate = "boosterfun", value = "BOOSTERFUN")   		BOOSTERFUN,
	@SerializedName(alternate = "boxtopper", value = "BOXTOPPER") 	  		BOXTOPPER,
	@SerializedName(alternate = "brawldeck", value = "BRAWLDECK") 	 		BRAWLDECK,
	@SerializedName(alternate = "bringafriend", value = "BRINGAFRIEND") 	 		BRINGAFRIEND,
	@SerializedName(alternate = "bundle", value = "BUNDLE") 		  		BUNDLE,
	@SerializedName(alternate = "buyabox", value = "BUYABOX") 		  		BUYABOX,
	@SerializedName(alternate = "commanderparty", value = "COMMANDERPARTY") COMMANDERPARTY,
	@SerializedName(alternate = "concept", value = "CONCEPT") CONCEPT,
	@SerializedName(alternate = "confettifoil", value = "CONFETTIFOIL") CONFETTIFOIL,
	@SerializedName(alternate = "convention", value = "CONVENTION")   		CONVENTION,
	@SerializedName(alternate = "datestamped", value = "DATESTAMPED")   	DATESTAMPED,
	@SerializedName(alternate = "dossier", value = "DOSSIER") DOSSIER,
	@SerializedName(alternate = "doubleexposure", value = "DOUBLEEXPOSURE") DOUBLEEXPOSURE,
	@SerializedName(alternate = "doublerainbow", value = "DOUBLERAINBOW") DOUBLERAINBOW,
	@SerializedName(alternate = "draculaseries", value = "DRACULASERIES") DRACULASERIES,
	@SerializedName(alternate = "dragonscalefoil", value = "DRAGONSCALEFOIL") DRAGONSCALEFOIL,
	@SerializedName(alternate = "draftweekend", value = "DRAFTWEEKEND")	 	DRAFTWEEKEND,
	@SerializedName(alternate = "duels", value = "DUELS") 					DUELS,
	@SerializedName(alternate = "embossed", value = "EMBOSSED") EMBOSSED,
	@SerializedName(alternate = "event", value = "EVENT") 					EVENT,
	@SerializedName(alternate = "firstplacefoil", value = "FIRSTPLACEFOIL") 	FIRSTPLACEFOIL,
	@SerializedName(alternate = "fnm", value = "FNM") 						FNM,
	@SerializedName(alternate = "fracturefoil", value = "FRACTUREFOIL") 	FRACTUREFOIL,
	@SerializedName(alternate = "galaxyfoil", value = "GALAXYFOIL") GALAXYFOIL,
	@SerializedName(alternate = "gameday", value = "GAMEDAY") 				GAMEDAY,
	@SerializedName(alternate = "gateway", value = "GATEWAY") 				GATEWAY,
	@SerializedName(alternate = "giftbox", value = "GIFTBOX") 				GIFTBOX,
	@SerializedName(alternate = "gilded", value = "GILDED") GILDED,
	@SerializedName(alternate = "glossy", value = "GLOSSY") GLOSSY,
	@SerializedName(alternate = "godzillaseries", value = "GODZILLASERIES") GODZILLASERIES,
	@SerializedName(alternate = "halofoil", value = "HALOFOIL") HALOFOIL,
	@SerializedName(alternate = "imagine", value = "IMAGINE") IMAGINE,
	@SerializedName(alternate = "instore", value = "INSTORE") 				INSTORE,
	@SerializedName(alternate = "intropack", value = "INTROPACK") 			INTROPACK,
	@SerializedName(alternate = "invisibleink", value = "INVISIBLEINK") 			INVISIBLEINK,
	@SerializedName(alternate = "jpwalker", value = "JPWALKER") 			JPWALKER,
	@SerializedName(alternate = "judgegift", value = "JUDGEGIFT") 			JUDGEGIFT,
	@SerializedName(alternate = "league", value = "LEAGUE") 				LEAGUE,
	@SerializedName(alternate = "magnified", value = "MAGNIFIED") 		MAGNIFIED,
	@SerializedName(alternate = "manafoil", value = "MANAFOIL") 		MANAFOIL,
	@SerializedName(alternate = "mediainsert", value = "MEDIAINSERT") 		MEDIAINSERT,
	@SerializedName(alternate = "moonlitland", value = "MOONLITLAND") MOONLITLAND,
	@SerializedName(alternate = "neonink", value = "NEONINK") NEONINK,
	@SerializedName(alternate = "oilslick", value = "OILSLICK") OILSLICK,
	@SerializedName(alternate = "openhouse", value = "OPENHOUSE") 			OPENHOUSE,
	@SerializedName(alternate = "planeswalkerdeck", value = "PLANESWALKERDECK") PLANESWALKERDECK,
	@SerializedName(alternate = "planeswalkerstamped", value = "PLANESWALKERSTAMPED") 			PLANESWALKERSTAMPED,
	@SerializedName(alternate = "plastic", value = "PLASTIC") 			PLASTIC,
	@SerializedName(alternate = "playerrewards", value = "PLAYERREWARDS") 	PLAYERREWARDS,
	@SerializedName(alternate = "playpromo", value = "PLAYPROMO") PLAYPROMO,
	@SerializedName(alternate = "playtest", value = "PLAYTEST") PLAYTEST,
	@SerializedName(alternate = "portrait", value = "PORTRAIT") PORTRAIT,
	@SerializedName(alternate = "poster", value = "POSTER") POSTER,
	@SerializedName(alternate = "premiereshop", value = "PREMIERESHOP") 	PREMIERESHOP,
	@SerializedName(alternate = "prerelease", value = "PRERELEASE") 		PRERELEASE,
	@SerializedName(alternate = "promopack", value = "PROMOPACK") 			PROMOPACK,
	@SerializedName(alternate = "promostamped", value = "PROMOSTAMPED") 	PROMOSTAMPED,
	@SerializedName(alternate = "rebalanced", value = "REBALANCED") REBALANCED,
	@SerializedName(alternate = "rainbow", value = "RAINBOW") 				RAINBOW,
	@SerializedName(alternate = "rainbowfoil", value = "RAINBOWFOIL") 				RAINBOWFOIL,
	@SerializedName(alternate = "raisedfoil", value = "RAISEDFOIL") 				RAISEDFOIL,
	@SerializedName(alternate = "ravnicacity", value = "RAVNICACITY") 				RAVNICACITY,
	@SerializedName(alternate = "release", value = "RELEASE") 				RELEASE,
	@SerializedName(alternate = "resale", value = "RESALE") 				RESALE,
	@SerializedName(alternate = "ripplefoil", value = "RIPPLEFOIL") 	   RIPPLEFOIL,
	@SerializedName(alternate = "schinesealtart", value = "SCHINESEALTART") SCHINESEALTART,
	@SerializedName(alternate = "scroll", value = "SCROLL") SCROLL,
	@SerializedName(alternate = "serialized", value = "SERIALIZED") SERIALIZED,
	@SerializedName(alternate = "setextension", value = "SETEXTENSION") SETEXTENSION,
	@SerializedName(alternate = "setpromo", value = "SETPROMO") 			SETPROMO,
	@SerializedName(alternate = "silverfoil", value = "SILVERFOIL") SILVERFOIL,
	@SerializedName(alternate = "sldbonus", value = "SLDBONUS") SLDBONUS,
	@SerializedName(alternate = "stamped", value = "STAMPED") STAMPED,
	@SerializedName(alternate = "startercollection", value = "STARTERCOLLECTION") STARTERCOLLECTION,
	@SerializedName(alternate = "starterdeck", value = "STARTERDECK") STARTERDECK,
	@SerializedName(alternate = "stepandcompleat", value = "STEPANDCOMPLEAT") STEPANDCOMPLEAT,
	@SerializedName(alternate = "storechampionship", value = "STORECHAMPIONSHIP") STORECHAMPIONSHIP,
	@SerializedName(alternate = "surgefoil", value = "SURGEFOIL") SURGEFOIL,
	@SerializedName(alternate = "textured", value = "TEXTURED") TEXTURED,
	@SerializedName(alternate = "themepack", value = "THEMEPACK") 			THEMEPACK,
	@SerializedName(alternate = "thick", value = "THICK") THICK,
	@SerializedName(alternate = "tourney", value = "TOURNEY") 				TOURNEY,
	@SerializedName(alternate = "upsidedown", value = "UPSIDEDOWN") 				UPSIDEDOWN,
	@SerializedName(alternate = "upsidedownback", value = "UPSIDEDOWNBACK") 				UPSIDEDOWNBACK,
	@SerializedName(alternate = "vault", value = "VAULT") 				VAULT,
	@SerializedName(alternate = "wizardsplaynetwork", value = "WIZARDSPLAYNETWORK") WIZARDSPLAYNETWORK;
	

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
