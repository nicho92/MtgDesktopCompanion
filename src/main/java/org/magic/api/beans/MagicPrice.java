package org.magic.api.beans;

import java.util.Currency;

public class MagicPrice implements Comparable<MagicPrice> {

	private Double value;
	private String seller;
	private String url;
	private Currency currency;
	private String site;
	private boolean foil;
	private String language;
	private String quality;

	private Object shopItem;
	private String country;

	public Object getShopItem() {
		return shopItem;
	}

	public void setShopItem(Object shopItem) {
		this.shopItem = shopItem;
	}

	public String getQuality() {
		if (quality == null)
			return "";

		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public boolean isFoil() {
		return foil;
	}

	public void setFoil(boolean foil) {
		this.foil = foil;
	}

	public String getLanguage() {
		if (language == null)
			return "";
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public Currency getCurrency() {
		return currency;
	}

	
	public void setCurrency(String currencyCode)
	{
		this.currency=Currency.getInstance(currencyCode);
	}
	
	
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public String toString() {
		return getSite() + ":" + getValue() + "" + getCurrency();
	}

	public int compareTo(MagicPrice o) {
		return (int) (getValue() - o.getValue());
	}

	public void setCountry(String c) {
		country = c;

	}

	public String getCountry() {
		return country;
	}
}
