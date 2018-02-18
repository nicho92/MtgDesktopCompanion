package org.magic.api.beans;

import java.net.URL;

public class MagicNews {

	private int id;
	private String name;
	private String categorie;
	private URL url;
	private NEWS_TYPE type;
	
	public enum NEWS_TYPE {RSS,TWITTER};
	
	public MagicNews() {
		id=-1;
	}
	
	public NEWS_TYPE getType() {
		return type;
	}
	
	public void setType(NEWS_TYPE type) {
		this.type = type;
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
