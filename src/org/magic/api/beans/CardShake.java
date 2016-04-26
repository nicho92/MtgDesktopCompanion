package org.magic.api.beans;

import java.net.URL;

public class CardShake {

	
	private String name;
	private URL img;
	private String rarity;
	private Double price;
	private double priceDayChange;
	private double percentDayChange;
	private double priceWeekChange;
	private double percentWeekChange;
	private MagicCard card;
	private String ed;
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public URL getImg() {
		return img;
	}
	public void setImg(URL img) {
		this.img = img;
	}
	public String getEd() {
		return ed;
	}
	public void setEd(String ed) {
		this.ed = ed;
	}
	public String getRarity() {
		return rarity;
	}
	public void setRarity(String rarity) {
		this.rarity = rarity;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public double getPriceDayChange() {
		return priceDayChange;
	}
	public void setPriceDayChange(double priceDayChange) {
		this.priceDayChange = priceDayChange;
	}
	public double getPercentDChange() {
		return percentDayChange;
	}
	public void setPercentDChange(double percentDChange) {
		this.percentDayChange = percentDChange;
	}
	public double getPriceWeekChange() {
		return priceWeekChange;
	}
	public void setPriceWeekChange(double priceWeekChange) {
		this.priceWeekChange = priceWeekChange;
	}
	public double getPercentWeekChange() {
		return percentWeekChange;
	}
	public void setPercentWeekChange(double percentWeekChange) {
		this.percentWeekChange = percentWeekChange;
	}
	public MagicCard getCard() {
		return card;
	}
	public void setCard(MagicCard card) {
		this.card = card;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	
}
