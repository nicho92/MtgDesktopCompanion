package org.magic.api.criterias;

import java.io.Serializable;

import javax.swing.Icon;

import org.magic.api.interfaces.extra.MTGIconable;
import org.magic.services.MTGConstants;

public class QueryAttribute implements Serializable, Comparable<QueryAttribute>, MTGIconable{

	private static final long serialVersionUID = 1L;
	private String name;
	private Class type;

	public QueryAttribute(String name, Class type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Class getType() {
		return type;
	}
	public void setType(Class type) {
		this.type = type;
	}

	 @Override
	public String toString() {
		return getName();

	}

	@Override
	public int compareTo(QueryAttribute o) {
		return (getName().compareTo(String.valueOf(o)));
	}

	@Override
	public Icon getIcon() {
		return MTGConstants.getIconFor(getType());
	}

}
