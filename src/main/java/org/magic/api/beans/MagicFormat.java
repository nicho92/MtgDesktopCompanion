package org.magic.api.beans;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class MagicFormat implements Serializable {

	public enum FORMATS {STANDARD, LEGACY, VINTAGE, MODERN, COMMANDER, PAUPER, PIONEER}
	
	public static String toString(FORMATS f)
	{
		return StringUtils.capitalize(f.name().toLowerCase());
	}

	private static final long serialVersionUID = 1L;
	private String format;
	private Boolean legality;

	public MagicFormat() {

	}

	public MagicFormat(String format, Boolean legality)
	{
		this.format=format;
		this.legality=legality;
	}
	
	
	public String getFormat() {
		return format;
	}
	
	public boolean isLegal()
	{
		return legality;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setLegality(Boolean legality) {
		this.legality = legality;
	}

	public String toString() {
		return getFormat() + " " + isLegal();
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

	public void setFormat(FORMATS standard) {
		format = StringUtils.capitalize(standard.name().toLowerCase());
		
	}
}