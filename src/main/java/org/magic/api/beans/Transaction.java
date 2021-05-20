package org.magic.api.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Transaction implements Serializable {
	public enum STAT {NEW,IN_PROGRESS,ACCEPTED,REFUSED,PAID,SENT, CLOSED, CANCELED } 
	
	private static final long serialVersionUID = 1L;
	private int id=-1;
	private Date dateProposition;
	private List<MagicCardStock> items;
	private Contact contact;
	private String message;
	private STAT statut;
	private String transporter;
	private double shippingPrice;
	private WebShopConfig config;
	private String transporterShippingCode;
	
	public Transaction() {
		dateProposition = new Date();
		items = new ArrayList<>();
		contact=new Contact();
		statut = STAT.NEW;
	}
	
	
	public String getTransporterShippingCode() {
		return transporterShippingCode;
	}


	public void setTransporterShippingCode(String transporterShippingCode) {
		this.transporterShippingCode = transporterShippingCode;
	}


	public void setConfig(WebShopConfig config) {
		this.config = config;
	}
	
	public WebShopConfig getConfig() {
		return config;
	}
	
	
	
	public String getTransporter() {
		return transporter;
	}




	public void setTransporter(String transporter) {
		this.transporter = transporter;
	}




	public double getShippingPrice() {
		return shippingPrice;
	}




	public void setShippingPrice(double shippingPrice) {
		this.shippingPrice = shippingPrice;
	}




	public void setStatut(STAT statut) {
		this.statut = statut;
	}
	public STAT getStatut() {
		return statut;
	}
	
	
	public double total()
	{
		return getItems().stream().mapToDouble(e->e.getQte()*e.getPrice()).sum();
	}
	
	
	@Override
	public String toString() {
		return String.valueOf(getId());
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
