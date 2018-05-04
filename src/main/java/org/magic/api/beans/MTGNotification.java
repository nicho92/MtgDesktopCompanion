package org.magic.api.beans;

import java.awt.TrayIcon.MessageType;
import java.util.Date;

public class MTGNotification {

	public enum FORMAT_NOTIFICATION {EMAIL,TEXT,MARKDOWN};
	
	
	private FORMAT_NOTIFICATION format;
	private Date date;
	private String sender;
	private String message;
	private String title;
	private MessageType type;
	
	public MTGNotification() {
		date = new Date();
		type=MessageType.INFO;
	}
	
	@Override
	public String toString() {
		return getDate() + ":"+ getMessage();
	}
	
	
	public FORMAT_NOTIFICATION getFormat() {
		return format;
	}
	public void setFormat(FORMAT_NOTIFICATION format) {
		this.format = format;
	}
	public Date getDate() {
		return date;
	}
	
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public MessageType getType() {
		return type;
	}
	public void setType(MessageType type) {
		this.type = type;
	}
	
	
	
	
	
}
