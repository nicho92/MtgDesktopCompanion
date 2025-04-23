package org.magic.api.beans;

import javax.swing.Icon;

import org.magic.api.interfaces.extra.MTGIconable;
import org.magic.api.interfaces.extra.MTGSerializable;
import org.magic.services.MTGConstants;

public class MTGCollection implements MTGSerializable, MTGIconable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String name;

	public MTGCollection() {

	}

	public MTGCollection(String name) {
		this.name = name;
	}

	public void setName(String string) {
		this.name = string;

	}

	@Override
	public String toString() {
		return getName();
	}

	public String getName() {
		return name;
	}

	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_TAB_BACK;
	}
	

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public String getStoreId() {
		return getName();
	}


	@Override
	public boolean equals(Object obj) {

		if(!(obj instanceof MTGCollection))
			return false;

		return ((MTGCollection)obj).getName().equalsIgnoreCase(getName());
	}
	

	

}
