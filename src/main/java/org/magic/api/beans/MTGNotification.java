package org.magic.api.beans;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

public class MTGNotification implements Serializable {

	private static final long serialVersionUID = 1L;
	public enum FORMAT_NOTIFICATION {HTML,TEXT,MARKDOWN}
	public enum MESSAGE_TYPE{ERROR,WARNING,INFO,NONE}

	private Date date;
	private String sender;
	private String message;
	private String title;
	private MESSAGE_TYPE type;
	private Exception exception;
	private File file;




	public MTGNotification() {
		date = new Date();
		type=MESSAGE_TYPE.INFO;
	}

	public MTGNotification(String title,Exception e)
	{
		this.message=e.getMessage();
		this.title=title;
		this.exception=e;
		date = new Date();
		type=MESSAGE_TYPE.ERROR;
	}

	public MTGNotification(String title,String msg,MESSAGE_TYPE t)
	{
		this.message=msg;
		this.title=title;
		date = new Date();
		type=t;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Exception getException() {
		return exception;
	}


	@Override
	public String toString() {
		return getDate() + ":"+ getMessage();
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
	public MESSAGE_TYPE getType() {
		return type;
	}
	public void setType(MESSAGE_TYPE type) {
		this.type = type;
	}





}
