package org.magic.api.beans.enums;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.annotations.SerializedName;

public enum MTGPromoType {


	
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
	@SerializedName(alternate = "draculaseries", value = "DRACULASERIES") DRACULASERIES;
	
	

	public String toPrettyString() {
		return StringUtils.capitalize(name().toLowerCase());
	}

	
	public static MTGPromoType parseByLabel(String s)
	{
		try {
			return MTGPromoType.valueOf(s.toUpperCase());
		}
		catch(IllegalArgumentException e)
		{
			return null;
		}
	}
	
}
