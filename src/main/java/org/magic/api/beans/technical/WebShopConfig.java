package org.magic.api.beans.technical;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.shop.Contact;

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
	private MagicCardStock topProduct;
	private int maxLastProduct = 4;
	private String currencySymbol;
	private String currencyCode;
	private double percentReduction=0;
	private String googleAnalyticsId;
	private int averageDeliveryTime;
	private String shippingRules;
	private String paypalClientId;
	private URI setPaypalSendMoneyUri;
	private boolean automaticValidation;
	private boolean automaticProduct;
	private boolean enableGed;
	private String iban;
	private String bic;
	private String websiteUrl;
	private boolean sealedEnabled;
	private String extraCss;
	private int productPagination;
	
	
	
	
	public WebShopConfig() {
		delivery= new ArrayList<>();
		links= new ArrayList<>();
		collections = new ArrayList<>();
		slidesLinksImage = new ArrayList<>();
		needcollections = new ArrayList<>();
		contact=new Contact();
	}
	
	public String getExtraCss() {
		return extraCss;
	}
	
	 public void setExtraCss(String extraCss) {
		this.extraCss = extraCss;
	}
	
	
	public boolean isEnableGed() {
		return enableGed;
	}

	public void setEnableGed(boolean enableGed) {
		this.enableGed = enableGed;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getBic() {
		return bic;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}





	public void setPaypalClientId(String paypalClientId) {
		this.paypalClientId = paypalClientId;
	}
	
	public String getPaypalClientId() {
		return paypalClientId;
	}
	
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	
	
	
	public String getCurrencyCode() {
		return currencyCode;
	}
	
	public Currency getCurrency()
	{
		return Currency.getInstance(getCurrencyCode());
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
	
	public void setCurrency(Currency c)
	{
		setCurrencySymbol(c.getSymbol());
		setCurrencyCode(c.getCurrencyCode());
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
	
	
	public void setTopProduct(MagicCardStock topProduct) {
		this.topProduct = topProduct;
	}
	
	
	public MagicCardStock getTopProduct() {
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

	public void setPaypalSendMoneyUri(URI ppuri) {
		this.setPaypalSendMoneyUri = ppuri;
	}
	
	public URI getSetPaypalSendMoneyUri() {
		return setPaypalSendMoneyUri;
	}

	public void setAutomaticValidation(boolean selected) {
		this.automaticValidation=selected;
	}
	
	public boolean isAutomaticValidation() {
		return automaticValidation;
	}

	public boolean isAutomaticProduct() {
		return automaticProduct;
	}
	
	public void setAutomaticProduct(boolean automaticProduct) {
		this.automaticProduct = automaticProduct;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}
	
	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public boolean isSealedEnabled() {
		return sealedEnabled;
	}
	
	public void setSealedEnabled(boolean sealedEnabled) {
		this.sealedEnabled = sealedEnabled;
	}

	public int getProductPagination() {
		return productPagination;
	}
	
	public void setProductPagination(int productPagination) {
		this.productPagination = productPagination;
	}
	
}
