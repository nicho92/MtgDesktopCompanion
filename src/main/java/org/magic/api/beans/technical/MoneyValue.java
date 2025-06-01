package org.magic.api.beans.technical;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

import org.magic.services.tools.UITools;

public class MoneyValue extends Number implements Serializable, Comparable<MoneyValue>{

	private static final long serialVersionUID = 1L;
	private BigDecimal value;
	private Currency currency;
	
	public BigDecimal getValue() {
		return value;
	}
	
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	

	public void setValue(Double value) {
		this.value = BigDecimal.valueOf(value);
	}
	
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	
	public MoneyValue() {
		
	}
	
	@Override
	public String toString() {
		return UITools.formatDouble(value) + " " + currency;
	}
	
	public MoneyValue(Double value, Currency currency) {
		super();
		this.value = BigDecimal.valueOf(value);
		this.currency = currency;
	}
		
	public boolean isPositive()
	{
		return value.doubleValue()>0;
	}
	
	@Override
	public int compareTo(MoneyValue o) {
		if(o==null || value==null)
			return -1;
		
		return value.compareTo(o.getValue());
		
	}

	@Override
	public int intValue() {
			return value.intValue();
	}

	@Override
	public long longValue() {
		return value.longValue();
	}

	@Override
	public float floatValue() {
		return value.floatValue();
	}

	@Override
	public double doubleValue() {
		return value.doubleValue();
	}

	
	
}
