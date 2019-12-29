package org.magic.tools;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;

public class FilesTools {

	protected static Logger logger = MTGLogger.getLogger(FilesTools.class);

	
	public static void saveFile(File f,String data) throws IOException
	{
		logger.debug("saving file " + f);
		FileUtils.write(f, data,MTGConstants.DEFAULT_ENCODING);
	}
	
	
	
}
