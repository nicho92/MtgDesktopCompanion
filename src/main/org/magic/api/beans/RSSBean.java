package org.magic.api.beans;

import java.net.URL;

public class RSSBean {

	String name;
	String categorie;
	URL url;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCategorie() {
		return categorie;
	}
	public void setCategorie(String categorie) {
		this.categorie = categorie;
	}
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
	
	public String toString() {
		return getName();
	}
	
}
