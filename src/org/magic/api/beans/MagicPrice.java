package org.magic.api.beans;

public class MagicPrice implements Comparable<MagicPrice> {

	private Double value;
	private String seller;
	private String url;
	private String currency; 
	private String site;
	
	
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
		return getValue() + " " + getCurrency();
	}
	
	public int compareTo(MagicPrice o) {
		return (int) (getValue()-o.getValue());
	}

	
}
