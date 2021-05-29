package org.magic.api.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Tracking implements Serializable{

	private List<TrackingStep> list;
	private boolean finished;
	private String productName;
	private String number;
	private Date deliveryDate;
	
	 @Override
	public String toString() {
		return getProductName() +"#"+getNumber();
	}
	
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	 
	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getProductName() {
		return productName;
	}
	
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
	public Tracking() {
		list = new ArrayList<>();
	}
	
	public Tracking(String number2) {
		list = new ArrayList<>();
		this.number=number2;
	}

	public void addStep(TrackingStep ts)
	{
		list.add(ts);
	}
	
}



