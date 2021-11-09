package org.magic.api.beans.shop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Tracking implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<TrackingStep> steps;
	private boolean finished;
	private String productName;
	private String number;
	private Date deliveryDate;
	private String trackingUri;
	
	public TrackingStep last()
	{
		if(getSteps().isEmpty())
			return null;
		
		return getSteps().get(getSteps().size()-1);
	}
	
	 public String getTrackingUri() {
		return trackingUri;
	}

	public void setTrackingUri(String trackingUri) {
		this.trackingUri = trackingUri;
	}

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
		steps = new ArrayList<>();
	}
	
	public Tracking(String number2) {
		steps = new ArrayList<>();
		this.number=number2;
	}

	public void addStep(TrackingStep ts)
	{
		steps.add(ts);
	}
	
	public List<TrackingStep> getSteps() {
		return steps;
	}

	
}



