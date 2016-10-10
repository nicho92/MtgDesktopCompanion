package org.magic.api.beans;

public class MagicPrice implements Comparable<MagicPrice> {

	private Double value;
	private String seller;
	private String url;
	private String currency; 
	private String site;
	private boolean foil;
	private String language;
	private String quality;
	
	
	public String getQuality() {
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
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
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
	public String toString()
	{
		return getSite() +":"  + getValue() + "" + getCurrency();
	}
	
	public int compareTo(MagicPrice o) {
		return (int) (getValue()-o.getValue());
	}

	
}
