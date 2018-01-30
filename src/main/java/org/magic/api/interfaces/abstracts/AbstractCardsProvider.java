package org.magic.api.interfaces.abstracts;

import java.util.Properties;

import org.magic.api.interfaces.MagicCardsProvider;

public abstract class AbstractCardsProvider implements MagicCardsProvider {

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
}
