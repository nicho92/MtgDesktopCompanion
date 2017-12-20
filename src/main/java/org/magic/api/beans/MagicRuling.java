package org.magic.api.beans;

import java.io.Serializable;

public class MagicRuling implements Serializable {

	
	String date;
	String text;
	
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return date +" :" + text +"\n";
	}
	
	
	
}
