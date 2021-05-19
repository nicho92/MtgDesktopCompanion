package org.magic.api.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WebShopConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	private String siteTitle;
	private String bannerTitle;
	private String bannerText;
	private String aboutText;
	private List<String> delivery;
	private List<String> links;
	private List<String> slidesLinksImage;
	private Contact contact;
	private List<MagicCollection> collections;
	private List<MagicCollection> needcollections;
	private MagicCard topProduct;
	private int maxLastProduct = 4;
	private String currencySymbol;
	private double percentReduction=0;
	private String googleAnalyticsId;
	private int averageDeliveryTime;
	private String shippingRules;
	
	
	public WebShopConfig() {
		delivery= new ArrayList<>();
		links= new ArrayList<>();
		collections = new ArrayList<>();
		slidesLinksImage = new ArrayList<>();
		needcollections = new ArrayList<>();
		contact=new Contact();
	}
	
	
	public String getShippingRules() {
		return shippingRules;
	}


	public void setShippingRules(String shippingRules) {
		this.shippingRules = shippingRules;
	}


	public void setAverageDeliveryTime(int averageDeliveryTime) {
		this.averageDeliveryTime = averageDeliveryTime;
	}
	
	public int getAverageDeliveryTime() {
		return averageDeliveryTime;
	}
	
	public String getGoogleAnalyticsId() {
		return googleAnalyticsId;
	}
	
	public void setGoogleAnalyticsId(String googleAnalyticsId) {
		this.googleAnalyticsId = googleAnalyticsId;
	}
	
	public List<MagicCollection> getNeedcollections() {
		return needcollections;
	}
	
	public void setNeedcollections(List<MagicCollection> needcollections) {
		this.needcollections = needcollections;
	}
	
	public String getCurrencySymbol() {
		return currencySymbol;
	}
	
	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}
	
	public int getMaxLastProduct() {
		return maxLastProduct;
	}
	
	public void setMaxLastProduct(int maxLastProduct) {
		this.maxLastProduct = maxLastProduct;
	}
	
	
	public void setTopProduct(MagicCard topProduct) {
		this.topProduct = topProduct;
	}
	
	
	public MagicCard getTopProduct() {
		return topProduct;
	}
	
	
	public List<String> getSlidesLinksImage() {
		return slidesLinksImage;
	}
	
	public void setSlidesLinksImage(List<String> slidesLinksImage) {
		this.slidesLinksImage = slidesLinksImage;
	}
	
	
	public String getSiteTitle() {
		return siteTitle;
	}
	public void setSiteTitle(String siteTitle) {
		this.siteTitle = siteTitle;
	}
	public String getBannerTitle() {
		return bannerTitle;
	}
	public void setBannerTitle(String bannerTitle) {
		this.bannerTitle = bannerTitle;
	}
	public String getBannerText() {
		return bannerText;
	}
	public void setBannerText(String bannerText) {
		this.bannerText = bannerText;
	}
	public String getAboutText() {
		return aboutText;
	}
	public void setAboutText(String aboutText) {
		this.aboutText = aboutText;
	}
	
	public List<String> getLinks() {
		return links;
	}
	public void setLinks(List<String> links) {
		this.links = links;
	}
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	public List<MagicCollection> getCollections() {
		return collections;
	}
	public void setCollections(List<MagicCollection> collections) {
		this.collections = collections;
	}

	
	public double getPercentReduction() {
		return percentReduction;
	}
	
	public void setPercentReduction(double percentReduction) {
		this.percentReduction = percentReduction;
	}
	public List<String> getDelivery() {
		return delivery;
	}
	
	public void setDelivery(List<String> delivery) {
		this.delivery = delivery;
	}
}
