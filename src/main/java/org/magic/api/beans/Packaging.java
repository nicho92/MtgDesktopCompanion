package org.magic.api.beans;

import java.io.Serializable;

import org.magic.api.beans.enums.EnumItems;

public class Packaging implements Serializable{

	private static final long serialVersionUID = 1L;

	public enum EXTRA { SET, DRAFT, COLLECTOR,THEME,GIFT,VIP}
	
	private EnumItems type;
	private String lang;
	private int num;
	private String url;
	private EXTRA extra;
	private MagicEdition edition;
	
	
	
	public EXTRA getExtra() {
		return extra;
	}
	public void setExtra(EXTRA extra) {
		this.extra = extra;
	}
	
	
	public EnumItems getType() {
		return type;
	}
	public void setType(EnumItems type) {
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
		return getLang()+"-" + (getExtra()!=null?getExtra()+"-":"")+ getNum();
	}

	public void setEdition(MagicEdition me) {
		this.edition=me;
	}
	
	public MagicEdition getEdition() {
		return edition;
	}
	
	
}
