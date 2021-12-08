 package org.magic.api.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.magic.api.beans.enums.EnumItems;
import org.magic.api.beans.enums.TransactionDirection;
import org.magic.api.beans.shop.Contact;
import org.magic.api.interfaces.MTGStockItem;

public class Announce implements Serializable, Comparable<Announce> {

	public enum STATUS { SOON, PUBLISHED, SOLD, EXPIRED }
	
	private static final long serialVersionUID = 1L;
	private int id=-1;
	private Contact contact;
	private Date creationDate;
	private Date startDate;
	private Date endDate;
	private Double totalPrice;
	private Currency currency;
	private String currencySymbol;
	private String title;
	private String description;
	private List<MTGStockItem> items;
	private TransactionDirection type;
	private boolean updated=false;
	private Double percentReduction;
	private GedEntry<Announce> mainImage ;
	private EnumItems categorie;
	private STATUS status;
	
	
	public Announce() {
		
		items = new ArrayList<>();
		type = TransactionDirection.BUY;
		creationDate = new Date();
		startDate = new Date();
		var c = Calendar.getInstance();
		c.setTime(startDate);
		c.add(Calendar.DAY_OF_MONTH, 15);
		endDate = c.getTime();
		totalPrice=0.0;
		percentReduction=0.0;
		currency = Currency.getInstance(Locale.getDefault());
		
	}
	
	public STATUS getStatus() {
		return status;
	}
	
	public void setStatus(STATUS status) {
		this.status = status;
	}
	
	public EnumItems getCategorie() {
		return categorie;
	}
	
	public void setCategorie(EnumItems categorie) {
		this.categorie = categorie;
	}
	
	public String getCurrencySymbol() {
		return currencySymbol;
	}
	
	
	public GedEntry<Announce> getMainImage() {
		return mainImage;
	}
	
	public void setMainImage(GedEntry<Announce> mainImage) {
		this.mainImage = mainImage;
	}

	

	public Date getCreationDate() {
		return creationDate;
	}


	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}


	public Double getPercentReduction() {
		return percentReduction;
	}

	public void setPercentReduction(Double percentReduction) {
		this.percentReduction = percentReduction;
	}

	public TransactionDirection getType() {
		return type;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof Announce b)
		{
			return b.getId()==this.getId();
		}
		
		return false;
		
	}
	
	public void setType(TransactionDirection type) {
		this.type = type;
	}
	
	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	@Override
	public String toString() {
		return String.valueOf(getId());
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
		currencySymbol = currency.getSymbol();
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<MTGStockItem> getItems() {
		return items;
	}
	public void setItems(List<MTGStockItem> items) {
		this.items = items;
	}


	@Override
	public int compareTo(Announce o) {
		if( o.getId()>getId())
			return 1;
		
		if( o.getId()<getId())
			return -1;
		
		return 0;
		
				
	}
	
	
	
}
