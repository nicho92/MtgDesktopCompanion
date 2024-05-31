package org.magic.api.beans;

import java.io.Serializable;
import java.util.Currency;
import java.util.Date;

import org.magic.api.beans.enums.EnumCardVariation;
import org.magic.api.beans.technical.MoneyValue;

public class CardShake implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String number;
	private String link;
	private MoneyValue price;
	private double priceDayChange=0.0;
	private double percentDayChange;
	private double priceWeekChange;
	private double percentWeekChange;
	private String ed;
	private Date dateUpdate;
	private String providerName;
	private boolean foil;
	private boolean etched;
	private EnumCardVariation cardVariation;


	public CardShake() {
		price = new MoneyValue(0.0,Currency.getInstance("USD"));
		priceDayChange = 0;
		percentDayChange = 0;
		priceWeekChange = 0;
		percentWeekChange = 0;
		
		dateUpdate=new Date();
		foil = false;
		etched = false;
	}

	public CardShake(boolean foil) {
		price =new MoneyValue(0.0,Currency.getInstance("USD"));
		priceDayChange = 0;
		percentDayChange = 0;
		priceWeekChange = 0;
		percentWeekChange = 0;
		dateUpdate=new Date();
		this.foil=foil;
	}

	public void init(double price, double lastDayPrice,double lastWeekPrice) {
		this.price=new MoneyValue(price,Currency.getInstance("USD"));;
		priceDayChange = price-lastDayPrice;


		if(lastDayPrice==0)
			lastDayPrice=1;

		if(lastWeekPrice==0)
			lastWeekPrice=1;


		percentDayChange = ((price-lastDayPrice)/lastDayPrice)/100;

		priceWeekChange = price-lastWeekPrice;
		percentWeekChange = ((price-lastWeekPrice)/lastWeekPrice)/100;


		dateUpdate=new Date();
	}

	public boolean isEtched() {
		return etched;
	}

	public void setEtched(boolean etched) {
		this.etched = etched;
	}

	public EnumCardVariation getCardVariation() {
		return cardVariation;
	}

	public void setCardVariation(EnumCardVariation cardVariation) {
		this.cardVariation = cardVariation;
	}


	public boolean isFoil() {
		return foil;
	}

	public void setFoil(boolean foil) {
		this.foil = foil;
	}



	public Currency getCurrency() {
		return price.getCurrency();
	}




	public void setCurrency(Currency currency) {
		price.setCurrency(currency);
	}




	public Date getDateUpdate() {
		return dateUpdate;
	}

	public void setDateUpdate(Date dateUpdate) {
		this.dateUpdate = dateUpdate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link=link;
	}

	public String getEd() {
		return ed;
	}

	public void setEd(String ed) {
		this.ed = ed;
	}

	public MoneyValue getPrice() {
		return price;
	}
	
	
	
	public void setPrice(Double price) {
		this.price.setValue(price);
	}

	public double getPriceDayChange() {
		return priceDayChange;
	}

	public void setPriceDayChange(double priceDayChange) {
		this.priceDayChange = priceDayChange;
	}

	public double getPercentDayChange() {
		return percentDayChange;
	}

	public void setPercentDayChange(double percentDChange) {
		this.percentDayChange = percentDChange;
	}

	public double getPriceWeekChange() {
		return priceWeekChange;
	}

	public void setPriceWeekChange(double priceWeekChange) {
		this.priceWeekChange = priceWeekChange;
	}

	public double getPercentWeekChange() {
		return percentWeekChange;
	}

	public void setPercentWeekChange(double percentWeekChange) {
		this.percentWeekChange = percentWeekChange;
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String name2) {
		this.providerName=name2;

	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

}
