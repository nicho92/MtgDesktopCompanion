package org.magic.test;

public class CardShack {

	
	String name;
	String img;
	String ed;
	String rarity;
	String price;
	String priceDChange;
	String percentDChange;
	String priceWChange;
	String percentWChange;
	String dailyChange = (priceDChange.startsWith("+")? "+": (priceDChange.startsWith("-")? "-":""));
	String weeklyChange = (priceWChange.startsWith("+")? "+": (priceWChange.startsWith("-")? "-":""));
	
	
	
	
}
