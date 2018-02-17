package org.magic.api.beans;

import java.net.URL;

public class MagicNews {

	private int id;
	private String name;
	private String categorie;
	private URL url;
	
	public MagicNews() {
		id=-1;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
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
