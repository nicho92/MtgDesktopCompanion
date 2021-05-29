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
		return getProductName();
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
	
	public void addStep(TrackingStep ts)
	{
		list.add(ts);
	}
	
}


class TrackingStep
{
	private Date dateStep;
	private String descriptionStep;
	private String code;
	
	
	public Date getDateStep() {
		return dateStep;
	}
	public void setDateStep(Date dateStep) {
		this.dateStep = dateStep;
	}
	public String getDescriptionStep() {
		return descriptionStep;
	}
	public void setDescriptionStep(String descriptionStep) {
		this.descriptionStep = descriptionStep;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	@Override
	public String toString() {
		return getDateStep() +" " + getDescriptionStep();
	}
	
	
}
