package org.magic.api.beans;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class MTGKeyWord implements Serializable, Comparable<MTGKeyWord>{

	private static final long serialVersionUID = 1L;

	public enum TYPE {
		ABILITIES, ACTION, WORD
	}

	public enum EVENT {
		STATIC, ACTIVATED, TRIGGERED
	}

	private String keyword;
	private String description;
	private EVENT event;
	private TYPE type;
	private String reminder;
	private boolean evergreen=false;

	public MTGKeyWord() {

	}

	public MTGKeyWord(String keyword, TYPE type) {
			this.keyword=keyword;
			this.type=type;
	}

	public MTGKeyWord(String keyword, TYPE type,boolean evergreen) {
		this.keyword=keyword;
		this.type=type;
		this.evergreen=evergreen;
	}

	public MTGKeyWord(String keyword, EVENT event, TYPE type) {
		super();
		this.keyword = keyword;
		this.event = event;
		this.type = type;
	}

	public MTGKeyWord(String keyword, EVENT event, TYPE type, boolean evergreen) {
		super();
		this.keyword = keyword;
		this.event = event;
		this.type = type;
		this.evergreen=evergreen;
	}


	@Override
	public int hashCode() {
		return getKeyword().hashCode();
	}


	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MTGKeyWord k )
			return k.getKeyword().equalsIgnoreCase(getKeyword());
			
			return false;


	}

	public void setEvergreen(boolean evergreen) {
		this.evergreen = evergreen;
	}

	public boolean isEvergreen() {
		return evergreen;
	}

	public void setReminder(String reminder) {
		this.reminder = reminder;
	}

	public String getReminder() {
		return reminder;
	}


	public EVENT getEvent() {
		return event;
	}

	public void setEvent(EVENT event) {
		this.event = event;
	}

	@Override
	public String toString() {
		return StringUtils.capitalize(getKeyword());
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	@Override
	public int compareTo(MTGKeyWord o) {
		
		if(o==null)
			return -1;
		
		return getKeyword().compareTo(o.getKeyword());
	}
	

}
