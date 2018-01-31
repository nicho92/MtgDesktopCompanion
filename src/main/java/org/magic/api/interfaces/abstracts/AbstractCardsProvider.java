package org.magic.api.interfaces.abstracts;

import java.util.Properties;

import org.magic.api.interfaces.MagicCardsProvider;

public abstract class AbstractCardsProvider implements MagicCardsProvider {

	
	protected boolean enable;
	

	@Override
	public Properties getProperties() {
		return null;
	}

	@Override
	public void setProperties(String k, Object value) {
		//do nothing
	}

	@Override
	public Object getProperty(String k) {
		return null;
	}

	@Override
	public void save() {
		//do nothing
	}

	@Override
	public void load() {
		//do nothing
	}

	@Override
	public PLUGINS getType() {
		return PLUGINS.PROVIDER;
	}
	
	@Override
	public boolean isEnable() {
		return enable;
	}

	@Override
	public void enable(boolean t) {
		this.enable=t;
		
	}
	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null)
			return false;
		
		return this.hashCode()==obj.hashCode();
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
