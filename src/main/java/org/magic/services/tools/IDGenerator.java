package org.magic.services.tools;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGCard;
import org.magic.services.logging.MTGLogger;

public class IDGenerator {

	static Logger logger = MTGLogger.getLogger(IDGenerator.class);

	private IDGenerator() {
	}

	public static String generate(MTGCard mc) {
		
		try {
		String number=mc.getNumber();
		if(number!=null&&number.isEmpty() )
			number=null;

		var id = String.valueOf((mc.getName() + mc.getCurrentSet() + number + mc.getMultiverseid()));
		id = DigestUtils.sha1Hex(id);

		logger.trace("Generate ID for {}|{}|{}|{}->:{}",mc.getName(),mc.getCurrentSet(),number,mc.getMultiverseid(),id);

		return id;
		
		}catch(Exception e)
		{
			logger.error("Error generating ID for {}",mc,e);
			return "";
		}
		
	}


}
