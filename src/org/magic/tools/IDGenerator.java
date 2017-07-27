package org.magic.tools;

import org.apache.commons.codec.digest.DigestUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;

public class IDGenerator {

	public static String generate(MagicCard mc, MagicEdition ed)
	{
		//String id = ed.getId()+mc.getName()+mc.getImageName(); --> mtgjson
		String id = String.valueOf((mc.getName()+ed+mc.getNumber()+ed.getMultiverse_id()));
		id = DigestUtils.sha1Hex(id);
		return id;
	}
}
