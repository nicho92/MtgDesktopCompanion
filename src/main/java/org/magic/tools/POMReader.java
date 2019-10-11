package org.magic.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.magic.services.MTGLogger;

public class POMReader {
	private static Logger logger = MTGLogger.getLogger(POMReader.class);

	private POMReader() {
		// prevent instances
	}

	/**
	 * reads the version property from the property file.
	 *
	 * @param clazz a class in the package (note: must be part of the artifact).
	 * @param pomProperties resource path to the pom.properties file inside the jar.
	 * @return the version string or null if it was not possible to read.
	 */
	public static synchronized String readVersionFromPom(Class<?> clazz, String pomProperties) {
		String version = null;

		// try reading the pom.properties file from the jar
		try {
			Properties p = new Properties();
			try (InputStream is = clazz.getResourceAsStream(pomProperties)) {
				if (is != null) {
					p.load(is);
					version = p.getProperty("version", null);
				}
			}
		} catch (IOException e) {
			version = null;
			logger.warn("Could not read version for "+clazz.getName()+" from file "+ pomProperties);
		}

		// using Java API
		if (version == null) {
			Package p = clazz.getPackage();
			if (p != null) {
				version = p.getImplementationVersion();
				if (version == null) {
					version = p.getSpecificationVersion();
				}
			}
		}

		return version;
	}
}
