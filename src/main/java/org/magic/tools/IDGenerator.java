package org.magic.tools;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.services.MTGLogger;

public class IDGenerator {

	static Logger logger = MTGLogger.getLogger(IDGenerator.class);

	private IDGenerator() {
	}

	public static String generate(MagicCard mc) {
		try {
			return generate(mc, mc.getCurrentSet());
		} catch (Exception e) {
			return "";
		}
	}

	public static String generate(MagicCard mc, MagicEdition ed) {
		// String id = ed.getId()+mc.getName()+mc.getImageName(); --> mtgjson

		String id = String.valueOf((mc.getName() + ed + ed.getNumber() + ed.getMultiverseid()));
		id = DigestUtils.sha1Hex(id);

		logger.trace("Generate ID for " + (mc.getName() + "|" + ed + "|" + ed.getNumber() + "|" + ed.getMultiverseid())+ "=" + id);

		return id;
	}
}
