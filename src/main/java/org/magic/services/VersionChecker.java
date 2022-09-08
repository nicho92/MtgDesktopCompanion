package org.magic.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.Logger;
import org.magic.services.logging.MTGLogger;
import org.magic.tools.GithubUtils;

public class VersionChecker {

	private String actualVersion;
	private String onlineVersion;
	private Logger logger = MTGLogger.getLogger(this.getClass());

	
	public String getVersion() {
		var input = getClass().getResourceAsStream(MTGConstants.MTG_DESKTOP_VERSION_FILE);
		try (var read = new BufferedReader(new InputStreamReader(input)))
		{
			var version = read.readLine();

			if (version.startsWith("${"))
				return "0.0";
			else
				return version;
		} catch (IOException e) {
			return "";
		}
	}

	
	
	public VersionChecker() {
		actualVersion = getVersion();
		
		var updatePRL =Boolean.parseBoolean(MTGControler.getInstance().get("notifyPrerelease","false"));
		try {
			GithubUtils.inst().setUpdateToPreRelease(updatePRL);
			onlineVersion = GithubUtils.inst().getVersion();
		} catch (Exception e) {
			onlineVersion = "";
			logger.error(e.getMessage());
		}
	}


	public boolean hasNewVersion() {
		
		try {
			var res = Double.parseDouble(onlineVersion) > Double.parseDouble(actualVersion);
			logger.info("check update:" + actualVersion + " found:" + onlineVersion);
			
			return res;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	public String getOnlineVersion() {
		return onlineVersion;
	}

}
