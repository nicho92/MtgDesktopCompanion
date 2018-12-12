package org.magic.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.magic.services.MTGLogger;

public class FileTools {

	private static Logger logger = MTGLogger.getLogger(FileTools.class);

	private FileTools() {	}
	
	
	public static void unZipIt(File src,File dst) {
 		byte[] buffer = new byte[1024];
 		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(src))) {
 			ZipEntry ze = zis.getNextEntry();
 			while (ze != null) {
				logger.info("unzip : " + src.getAbsoluteFile());
 				try (FileOutputStream fos = new FileOutputStream(dst)) {
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					ze = zis.getNextEntry();
				}
			}
		} catch (IOException ex) {
			logger.error(ex);
		}
 		boolean del = FileUtils.deleteQuietly(src);
		logger.debug("removing " + src + "=" + del);
 	}
}
