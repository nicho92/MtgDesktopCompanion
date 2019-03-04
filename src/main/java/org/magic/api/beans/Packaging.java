package org.magic.api.beans;

import java.io.Serializable;

public class Packaging implements Serializable{

	public enum TYPE { BOX, BOOSTER, STARTER,BUNDLE,BANNER,CONSTRUCTPACK,PRERELEASEPACK}
	
	private TYPE type;
	private String lang;
	private int num;
	private String url;
	private MagicEdition edition;
	
	
	public Packaging() {
		
	}

	public TYPE getType() {
		return type;
	}
	public void setType(TYPE type) {
		this.type = type;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public String toString() {
		return getLang()+"-" + getNum();
	}

	public void setEdition(MagicEdition me) {
		this.edition=me;
	}
	
	public MagicEdition getEdition() {
		return edition;
	}
	
	
}
