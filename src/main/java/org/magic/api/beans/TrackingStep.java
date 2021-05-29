package org.magic.api.beans;

import java.util.Date;

public class TrackingStep
{
	private Date dateStep;
	private String descriptionStep;
	private String code;
	
	public TrackingStep() {
	
	}
	
	
	
	public TrackingStep(Date dateStep, String descriptionStep, String code) {
		super();
		this.dateStep = dateStep;
		this.descriptionStep = descriptionStep;
		this.code = code;
	}



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