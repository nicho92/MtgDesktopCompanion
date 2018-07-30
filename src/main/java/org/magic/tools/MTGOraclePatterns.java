package org.magic.tools;

public enum MTGOraclePatterns {
	
	COST_LIFE_PATTERN ("\\QPay\\E (.*?) \\Qlife\\E"),
	MANA_PATTERN ("\\{(.*?)\\}"),
	PARENTHESES_PATTERN("\\(.*\\)"),
	COUNTERS("(?:[Pp]ut) (a|an|two|three|four|five|six|seven|eight|nine|ten) (.*?) counter[s]? on "),
	COSTS("");
	
	
	private String pattern = "";
	
	MTGOraclePatterns(String name){
	    this.pattern = name;
	  }
	
	@Override
	public String toString(){
	    return getPattern();
	  }
	
	public String getPattern() {
		return pattern;
	}

	
}