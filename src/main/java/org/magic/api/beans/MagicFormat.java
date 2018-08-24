package org.magic.api.beans;

import java.io.Serializable;

public class MagicFormat implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String format;
	String legality;

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