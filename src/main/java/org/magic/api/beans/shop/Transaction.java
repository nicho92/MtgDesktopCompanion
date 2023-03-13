package org.magic.api.beans.shop;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.beans.enums.TransactionPayementProvider;
import org.magic.api.beans.enums.TransactionStatus;
import org.magic.api.beans.technical.WebShopConfig;
import org.magic.api.interfaces.MTGSerializable;
import org.magic.api.interfaces.MTGStockItem;

public class Transaction implements MTGSerializable, Comparable<Transaction> {
	private static final long serialVersionUID = 1L;
	private Long id=-1L;
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
	private double reduction;
	private Currency currency;
	private String sourceShopName;
	private String sourceShopId;
	
	
	private TransactionPayementProvider paymentProvider;
	private TransactionStatus statut;
	private TransactionDirection typeTransaction;
	

	@Override
	public String getStoreId() {
		return String.valueOf(getId());
	}
	

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
		typeTransaction=TransactionDirection.SELL;
	}
	

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public void setTypeTransaction(TransactionDirection typeTransaction) {
		this.typeTransaction = typeTransaction;
	}

	public TransactionDirection getTypeTransaction() {
		return typeTransaction;
	}


	public String getSourceShopId() {
		return sourceShopId;
	}


	public void setSourceShopId(String sourceShopId) {
		this.sourceShopId = sourceShopId;
	}


	public Date getDatePayment() {
		return datePayment;
	}

	public void setDatePayment(Date datePayment) {
		this.datePayment = datePayment;
	}

	public void setReduction(double reduction) {
		this.reduction = reduction;
	}
	
	public double getReduction() {
		return reduction;
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
		return (totalItems() + getShippingPrice()) -getReduction();
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

	public Long getId() {
		return id;
	}

	public void setId(int id) {
		setId(Long.valueOf(id));
	}

	public void setId(Long id) {
		this.id=id;

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
	
	@Override
	public int compareTo(Transaction o) {
		if( o.getId()>getId())
			return 1;

		if( o.getId()<getId())
			return -1;

		return 0;
	}

}
