package org.magic.tools;

import java.io.IOException;
import java.util.Properties;

import org.magic.services.logging.MTGLogger;

public class POMReader {

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
		var logger = MTGLogger.getLogger(POMReader.class);
		// try reading the pom.properties file from the jar
		try {
			var p = new Properties();
			try (var is = clazz.getResourceAsStream(pomProperties)) {
				if (is != null) {
					p.load(is);
					version = p.getProperty("version", null);
				}
			}
		} catch (IOException e) {
			version = null;
			logger.warn("Could not read version for {} from file {}",clazz.getName(),pomProperties);
		}

		// using Java API
		if (version == null) {
			var p = clazz.getPackage();
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
