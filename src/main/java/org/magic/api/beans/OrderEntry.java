package org.magic.api.beans;

import java.util.Currency;
import java.util.Date;

public class OrderEntry {

	public enum TYPE_ITEM {CARD,BOX,BOOSTER,FULLSET,LOTS}
	public enum TYPE_TRANSACTION {BUY,SELL}
	
	
	private TYPE_ITEM type;
	private String description;
	private Double itemPrice;
	private Double shippingPrice;
	
	private Currency currency;
	private Date transationDate;
	private String seller;
	private String idTransation;
	private TYPE_TRANSACTION typeTransaction;
	private MagicEdition edition;
	private String source;
	private boolean updated;
	
	@Override
	public String toString() {
		return idTransation;
	}
	
	public OrderEntry() {
		transationDate=new Date();
		itemPrice=0.0;
		updated=true;
	}
	
	public boolean isUpdated() {
		return updated;
	}
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public TYPE_TRANSACTION getTypeTransaction() {
		return typeTransaction;
	}
	public void setTypeTransaction(TYPE_TRANSACTION typeTransaction) {
		this.typeTransaction = typeTransaction;
	}
	public TYPE_ITEM getType() {
		return type;
	}
	public void setType(TYPE_ITEM type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Double getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(Double itemPrice) {
		this.itemPrice = itemPrice;
	}
	public Double getShippingPrice() {
		return shippingPrice;
	}
	public void setShippingPrice(Double shippingPrice) {
		this.shippingPrice = shippingPrice;
	}
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	public Date getTransationDate() {
		return transationDate;
	}
	public void setTransationDate(Date transationDate) {
		this.transationDate = transationDate;
	}
	public String getSeller() {
		return seller;
	}
	public void setSeller(String seller) {
		this.seller = seller;
	}
	public String getIdTransation() {
		return idTransation;
	}
	public void setIdTransation(String idTransation) {
		this.idTransation = idTransation;
	}
	public MagicEdition getEdition() {
		return edition;
	}
	public void setEdition(MagicEdition edition) {
		this.edition = edition;
	}
	
	
}
