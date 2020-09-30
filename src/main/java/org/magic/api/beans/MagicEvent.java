package org.magic.api.beans;

import java.awt.Color;
import java.net.URL;
import java.util.Date;

public class MagicEvent {

	private Date startDate;
	private Date endDate;
	private String title;
	private EVENT_FORMAT format;
	private String localisation;
	private String description;
	private URL url;
	private int duration;
	private Color color;
	private ROUNDS roundFormat;
	
	
	public enum EVENT_FORMAT { CONSTRUCTED, DRAFT, SEALED}
	public enum ROUNDS { SWISS, DIRECT_ELIMINATION }
	
	
	public void setRoundFormat(ROUNDS roundFormat) {
		this.roundFormat = roundFormat;
	}
	
	public ROUNDS getRoundFormat() {
		return roundFormat;
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public EVENT_FORMAT getFormat() {
		return format;
	}

	public void setFormat(EVENT_FORMAT format) {
		this.format = format;
	}

	public String getLocalisation() {
		return localisation;
	}

	public void setLocalisation(String localisation) {
		this.localisation = localisation;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return getTitle();
	}

}
