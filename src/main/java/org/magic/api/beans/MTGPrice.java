package org.magic.api.beans;

import java.util.Currency;

import org.magic.api.beans.technical.MoneyValue;

public class MTGPrice implements Comparable<MTGPrice> {
	private String seller;
	private String url;
	private String site;
	private boolean foil;
	private String language;
	private String quality;
	private Object shopItem;
	private String country;
	private MTGCard magicCard;
	private int qty = 1;
	private String sellerUrl;
	private MoneyValue priceValue;
	
	public MTGPrice() {
		priceValue = new MoneyValue();
	}
	

	public String getSellerUrl() {
		return sellerUrl;
	}

	public void setSellerUrl(String sellerUrl) {
		this.sellerUrl = sellerUrl;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public int getQty() {
		return qty;
	}

	public MTGCard getMagicCard() {
		return magicCard;
	}

	public void setMagicCard(MTGCard magicCard) {
		this.magicCard = magicCard;
	}

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
		return priceValue.getCurrency();
	}


	public void setCurrency(String currencyCode)
	{
		if(!currencyCode.isEmpty())
			priceValue.setCurrency(Currency.getInstance(currencyCode.toUpperCase()));
	}


	public void setCurrency(Currency currency) {
		priceValue.setCurrency(currency);
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
		return priceValue.doubleValue();
	}

	public void setValue(Double value) {
		priceValue.setValue(value);
	}

	@Override
	public String toString() {
		return String.valueOf(magicCard);
	}

	@Override
	public int compareTo(MTGPrice o) {
		return (int) (getValue() - o.getValue());
	}

	public void setCountry(String c) {
		country = c;

	}

	public String getCountry() {
		return country;
	}
}
