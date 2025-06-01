package org.magic.api.beans;

import java.io.Serializable;
import java.util.Date;

public class MTGRuling implements Serializable {

	private static final long serialVersionUID = 1L;
	private Date date;
	private String text;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
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
		return date + " :" + text + "\n";
	}

}
