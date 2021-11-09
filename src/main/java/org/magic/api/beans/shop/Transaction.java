package org.magic.api.beans.shop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.magic.api.beans.WebShopConfig;
import org.magic.api.beans.enums.TransactionPayementProvider;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.interfaces.MTGStockItem;

public class Transaction implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id=-1;
	private Date dateCreation;
	private Date datePayment;
	private Date dateSend;
	private Contact contact;
	private String message;
	private List<MTGStockItem> items;
	private String transporter;
	private double shippingPrice;
	private WebShopConfig config;
	private String transporterShippingCode;
	private Currency currency;
	private String sourceShopName;
	private TransactionPayementProvider paymentProvider;
	private TransactionStatus statut;

	
	
	public String getSourceShopName() {
		return sourceShopName;
	}
	
	public void setSourceShopName(String sourceShopName) {
		this.sourceShopName = sourceShopName;
	}

	
	public Transaction() {
		dateCreation = new Date();
		items = new ArrayList<>();
		contact=new Contact();
		statut = TransactionStatus.NEW;
	}
	
	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}



	public Date getDatePayment() {
		return datePayment;
	}



	public void setDatePayment(Date datePayment) {
		this.datePayment = datePayment;
	}



	public Date getDateSend() {
		return dateSend;
	}

	public void setDateSend(Date dateSend) {
		this.dateSend = dateSend;
	}

	public TransactionPayementProvider getPaymentProvider() {
		return paymentProvider;
	}

	public void setPaymentProvider(TransactionPayementProvider paymentProvider) {
		this.paymentProvider = paymentProvider;
	}



	public void setCurrency(Currency currency) {
		this.currency = currency;
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

	public void setStatut(TransactionStatus statut) {
		this.statut = statut;
	}
	public TransactionStatus getStatut() {
		return statut;
	}
	
	public double totalItems()
	{
		return getItems().stream().mapToDouble(e->e.getQte()*e.getPrice()).sum();
	}
	
	public double total()
	{
		return totalItems() + getShippingPrice();
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
	
	public List<MTGStockItem> getItems() {
		return items;
	}
	public void setItems(List<MTGStockItem> items) {
		this.items = items;
	}
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public void setCurrency(String string) {
		currency = Currency.getInstance(string);
	}
	
	public Currency getCurrency() {
		return currency;
	}

}
