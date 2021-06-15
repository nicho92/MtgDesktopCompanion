package org.magic.api.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.magic.api.exports.impl.WooCommerceExport;

public class Transaction implements Serializable {
	public enum STAT {NEW,IN_PROGRESS,PAYMENT_WAITING, REFUSED,PAID,SENT, CLOSED, CANCELED,CANCELATION_ASK } 
	public enum PAYMENT_PROVIDER {PAYPAL,VIREMENT,CASH,VISA, AMEX} 
	
	private static final long serialVersionUID = 1L;
	private int id=-1;
	private Date dateCreation;
	
	private Date datePayment;
	private Date dateSend;
	private PAYMENT_PROVIDER paymentProvider;

	private List<MagicCardStock> items;
	private Contact contact;
	private String message;
	private STAT statut;
	private String transporter;
	private double shippingPrice;
	private WebShopConfig config;
	private String transporterShippingCode;
	private Currency currency;
	
	public Transaction() {
		dateCreation = new Date();
		items = new ArrayList<>();
		contact=new Contact();
		statut = STAT.NEW;
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



	public PAYMENT_PROVIDER getPaymentProvider() {
		return paymentProvider;
	}



	public void setPaymentProvider(PAYMENT_PROVIDER paymentProvider) {
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

	public void setDateProposition(Date dateProposition) {
		this.dateCreation = dateProposition;
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

	public void setCurrency(String string) {
		currency = Currency.getInstance(string);
	}
	
	public Currency getCurrency() {
		return currency;
	}

	public boolean isWoocommerceAvailable() {
		for(MagicCardStock mcs : getItems())
		{	
			if(mcs.getTiersAppIds(new WooCommerceExport().getName())==null)
				return false;
		}
		
		return true;
	}
	
	
	
}
