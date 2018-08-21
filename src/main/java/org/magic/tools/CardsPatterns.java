package org.magic.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum CardsPatterns {
	
	COST_LIFE_PATTERN ("\\QPay\\E (.*?) \\Qlife\\E"),
	MANA_PATTERN ("\\{(.*?)\\}"),
	COUNTERS("(?:[Pp]ut) (a|an|two|three|four|five|six|seven|eight|nine|ten) (.*?) counter[s]? on "),
	ADD_MANA("(?:[Aa]dd) "+MANA_PATTERN),
	REMINDER("(?:\\(.+?\\))");
	
	
	private String pattern = "";
	
	CardsPatterns(String name){
	    this.pattern = name;
	}
	
	
	public String getPattern() {
		return pattern;
	}

	
	public boolean hasPattern(String s , CardsPatterns pat)
	{
		Pattern p = Pattern.compile(pat.getPattern());
		Matcher m = p.matcher(s);
		return m.find();
		
	}
	
	
	public static void main(String[] args) {
		
		test("Add {R}");
		test("Add {R} or {B}");
		test("{T}, Sacrifice Ancient Spring: Add {G}{B}.");
		test("Add {U}, {U}, or {B}.");
		test("{T}: Add {U}{U}, {U}{R}, or {R}{R}.");
		test("Add one mana of any color");
		test("Whenever enchanted land is tapped for mana, (its) controller adds an additional {G}{G}.");
	}
	
	public static void test(String s)
	{
		Pattern p = Pattern.compile(REMINDER.getPattern());
		Matcher m = p.matcher(s);
		while(m.find())
			System.out.println(m.group());
	}
	
	
	
}