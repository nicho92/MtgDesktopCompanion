package org.magic.services;

import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.commons.lang3.StringUtils;

public class LanguageService {

	private ResourceBundle rbundle;
	
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
				var sb = new StringBuilder();
				for (var k : keys) 
					sb.append(get(k)).append(" ");
				
				return sb.toString();
	}

	public LanguageService() {
		rbundle = ResourceBundle.getBundle(MTGConstants.MESSAGE_BUNDLE, getDefault());
	}

	public LanguageService(Locale l) {
		rbundle = ResourceBundle.getBundle(MTGConstants.MESSAGE_BUNDLE, l);
	}

	public Locale getDefault() {
		return Locale.ENGLISH;
	}

	public Locale[] getAvailableLocale() {
		return new Locale[]{Locale.ENGLISH, Locale.FRENCH, Locale.JAPANESE, Locale.of("pt", "br"),
				Locale.of("nl", "be")};
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
		} catch (Exception _) {
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
