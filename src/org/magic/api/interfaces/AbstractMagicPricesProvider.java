package org.magic.api.interfaces;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.pricers.impl.EbayPricer;

public abstract class AbstractMagicPricesProvider implements MagicPricesProvider {

	@Override
	public abstract List<MagicPrice> getPrice(MagicEdition me, MagicCard card) throws Exception ;

	private boolean enable=true;
	protected Properties props;

	@Override
	public abstract String getName() ;

	public Properties getProperties() {
		return props;
	}

	@Override
	public void setProperties(String k, Object value) {
		props.put(k,value);
	}

	@Override
	public Object getProperty(String k) {
		return props.get(k);
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
		return this.hashCode()==obj.hashCode();
	}

}
