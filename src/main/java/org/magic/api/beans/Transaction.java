package org.magic.api.beans;

import java.util.Date;
import java.util.List;

public class Transaction {
	
	private int id;
	private Date dateProposition;
	private List<MagicCardStock> items;
	private Contact contact;
	private String message;
	
	public Transaction() {
		dateProposition = new Date();
	}
	
	public Transaction(int id) {
		dateProposition = new Date();
		this.id=id;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDateProposition() {
		return dateProposition;
	}
	public void setDateProposition(Date dateProposition) {
		this.dateProposition = dateProposition;
	}
	public List<MagicCardStock> getItems() {
		return items;
	}
	public void setItems(List<MagicCardStock> proposition) {
		this.items = proposition;
	}
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}

	
}
