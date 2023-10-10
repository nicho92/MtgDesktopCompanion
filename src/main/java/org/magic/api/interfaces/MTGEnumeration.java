package org.magic.api.interfaces;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.magic.services.logging.MTGLogger;

public interface MTGEnumeration{
	Logger logger = MTGLogger.getLogger(MTGEnumeration.class);
	
	default String toPrettyString() {
		return StringUtils.capitalize(name().toLowerCase());
	}

	String name();
	
}
