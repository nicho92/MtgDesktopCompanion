package org.magic.services;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.magic.services.logging.MTGLogger;

public class LanguageService {

	private ResourceBundle rbundle;
	private StringBuilder temp;
	protected Logger logger = MTGLogger.getLogger(this.getClass());


	public String get(String k, Object... values) {
		String t = get(k);

		for (var i = 0; i < values.length; i++) {
			t = t.replaceFirst("%" + (i + 1), String.valueOf(values[i]));
		}
		return t;
	}

	public String getCapitalize(String k, Object... values) {
		return StringUtils.capitalize(get(k, values));
	}

	public String combine(String... keys) {
		temp.setLength(0);

		for (String k : keys)
			temp.append(get(k)).append(" ");

		return temp.toString();
	}

	public LanguageService() {
		temp = new StringBuilder();
		rbundle = ResourceBundle.getBundle(MTGConstants.MESSAGE_BUNDLE, getDefault());
	}

	public LanguageService(Locale l) {
		temp = new StringBuilder();
		rbundle = ResourceBundle.getBundle(MTGConstants.MESSAGE_BUNDLE, l);
	}

	public Locale getDefault() {
		return Locale.ENGLISH;
	}
	
	//todo change it for Jdk19
	public Locale[] getAvailableLocale() {
		return new Locale[] { Locale.ENGLISH, Locale.FRENCH, Locale.JAPANESE, new Locale("pt", "br"), new Locale("nl", "be")};
	}

	public void changeLocal(Locale l) {
		if (l != null)
			rbundle = ResourceBundle.getBundle(MTGConstants.MESSAGE_BUNDLE, l);
		else
			rbundle = ResourceBundle.getBundle(MTGConstants.MESSAGE_BUNDLE, getDefault());
	}

	public String get(String key) {
		try {
		return rbundle.getString(key);
		}catch(Exception e)
		{
			logger.trace("no bundle key found for {}",key);
			return key.toLowerCase();
		}
	}

	public String getCapitalize(String key) {
		return StringUtils.capitalize(get(key));
	}

	public String getError() {
		return getCapitalize("ERROR");
	}

}
