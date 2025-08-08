package org.magic.api.beans.enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum EnumCardsPatterns {

	COST_LIFE_PATTERN 	("\\QPay\\E (.*?) \\Qlife\\E"),
	MANA_PATTERN 			("\\{(.*?)\\}"),
	COUNTERS					("(?:[Pp]ut) (a|an|two|three|four|five|six|seven|eight|nine|ten) (.*?) counter[s]? on "),
	ADD_MANA					("(?:[Aa]dd[s]){0,1} ("+MANA_PATTERN+")+|((one|two|three|four|five) mana)"),
	REMINDER					("(?:\\(.+?\\))"),
	TRIGGER_ENTERS_BATTLEFIELD	("(.*?) enters the battlefield"),
	CREATE_TOKEN 			("[Cc]reate[s]? (.*?) token[s]?"),
	CREATE_EMBLEM 			("You get an emblem with (.*?)"),
	RULES_LINE					("^(\\d{1,3})\\.(\\d{1,3})?([a-z])?"),
	LOYALTY_PATTERN		("\\[(.*?)\\][ ]?: (.*?)$"),
	SAGA_PATTERN			("(.*?) — (.*?)$"),
	ROLL_DICE					("then  roll a d(\\d+)"), 
	MANA_COLORS 			("([WUBRG])");
	

	public static final String REGEX_ANY_STRING = "(.*?)";

	private String pattern = "";

	EnumCardsPatterns(String name){
	    this.pattern = name;
	}

	@Override
	public String toString()
	{
		return pattern;
	}

	public String getPattern() {
		return pattern;
	}

	public static Matcher extract(String s , EnumCardsPatterns pat)
	{
		return Pattern.compile(pat.getPattern()).matcher(s);
	}


	public static boolean hasPattern(String s , EnumCardsPatterns pat)
	{
		if(s==null)
			return false;
		
		var p = Pattern.compile(pat.getPattern());
		var m = p.matcher(s);
		return m.find();
	}

}