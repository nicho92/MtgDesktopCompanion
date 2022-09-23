package org.magic.api.beans.shop;

import java.io.Serializable;
import java.util.Date;

public class TrackingStep implements Serializable, Comparable<TrackingStep>
{
	private static final long serialVersionUID = 1L;
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



	@Override
	public int compareTo(TrackingStep o) {
		if(o==null)
			return -1;

		return getDateStep().compareTo(o.getDateStep());
	}


}