package org.magic.api.beans;

import java.util.Currency;
import java.util.Date;

import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.interfaces.MTGSerializable;


@Deprecated
public class OrderEntry implements MTGSerializable {


	private static final long serialVersionUID = 1L;


	private Integer id=-1;
	private EnumItems type;
	private String description;
	private Double itemPrice=0.0;
	private Double shippingPrice=0.0;
	private Currency currency;
	private Date transactionDate;
	
	private String seller;
	private String idTransation;
	private TransactionDirection typeTransaction;
	private MagicEdition edition;
	private String source;
	private boolean updated;


	@Override
	public String getStoreId() {
		return String.valueOf(getId());
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if(!(obj instanceof OrderEntry))
			return false;


		return getId() ==((OrderEntry)obj).getId();

	}

	@Override
	public String toString() {
		return getIdTransation();
	}

	public OrderEntry() {
		transactionDate=new Date();
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
	public TransactionDirection getTypeTransaction() {
		return typeTransaction;
	}
	public void setTypeTransaction(TransactionDirection typeTransaction) {
		this.typeTransaction = typeTransaction;
	}
	public EnumItems getType() {
		return type;
	}
	public void setType(EnumItems type) {
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

	public void setCurrency(String currency) {
		this.currency = Currency.getInstance(currency);
	}

	public Date getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Date transationDate) {
		this.transactionDate = transationDate;
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
