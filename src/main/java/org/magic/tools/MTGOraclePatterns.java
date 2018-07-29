package org.magic.tools;

public enum MTGOraclePatterns {
	
	COST_LIFE_PATTERN ("\\QPay\\E (.*?) \\Qlife\\E"),
	MANA_PATTERN ("\\{(.*?)\\}");
	
	
	
	private String pattern = "";
	
	MTGOraclePatterns(String name){
	    this.pattern = name;
	  }
	
	@Override
	public String toString(){
	    return pattern;
	  }
	
	public String getPattern() {
		return pattern;
	}
	
}