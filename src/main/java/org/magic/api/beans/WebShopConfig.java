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
	private List<String> informations;
	private List<String> links;
	private List<String> slidesLinksImage;
	private Contact contact;
	
	
	
	public WebShopConfig() {
		informations= new ArrayList<>();
		links= new ArrayList<>();
		slidesLinksImage = new ArrayList<>();
		contact=new Contact();
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
	public List<String> getInformations() {
		return informations;
	}
	public void setInformations(List<String> informations) {
		this.informations = informations;
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
	
	
	
}
