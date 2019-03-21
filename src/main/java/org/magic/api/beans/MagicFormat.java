package org.magic.api.beans;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class MagicFormat implements Serializable {

	public enum FORMATS {STANDARD, LEGACY, VINTAGE, MODERN, COMMANDER}
	
	public static String toString(FORMATS f)
	{
		return StringUtils.capitalize(f.name().toLowerCase());
	}
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String format;
	private String legality;

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getLegality() {
		return legality;
	}

	public void setLegality(String legality) {
		this.legality = legality;
	}

	public String toString() {
		return getFormat() + " " + getLegality();
	}

	@Override
	public int hashCode() {
		return getFormat().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (this.getClass() != obj.getClass())
			return false;

		return getFormat().equalsIgnoreCase(((MagicFormat) obj).getFormat());

	}
}