package org.magic.services;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

public class LanguageService {

	ResourceBundle rbundle;
	StringBuilder temp;
	
	
	
	private static final String BUNDLE = "locales.lang";
	
	public String get(String k,Object... values)
	{
		String t = get(k);
		for(int i=0;i<values.length;i++)
		{
			t=t.replaceFirst("%"+(i+1), String.valueOf(values[i]));
		}
		return t;
	}
	
	public String getCapitalize(String k,Object... values)
	{
		return StringUtils.capitalize(get(k,values));
	}
	
	public String combine(String... keys)
	{
		temp.setLength(0);
		
		for(String k: keys)
			temp.append(get(k)).append(" ");

		return temp.toString();
	}
	
	public LanguageService() {
		temp = new StringBuilder();
		rbundle = ResourceBundle.getBundle(BUNDLE,getDefault());
	}
	
	public LanguageService(Locale l) {
		temp = new StringBuilder();
		rbundle = ResourceBundle.getBundle(BUNDLE,l);
	}
	
	
	public Locale getDefault()
	{
		return Locale.ENGLISH;
	}
	
	public Locale[] getAvailableLocale()
	{
		return new Locale[] {Locale.ENGLISH,Locale.FRENCH};
	}
	
	
	public void changeLocal(Locale l) {
		if(l!=null)
			rbundle = ResourceBundle.getBundle(BUNDLE,l);
		else
			rbundle = ResourceBundle.getBundle(BUNDLE,getDefault());
	}
	
	public String get(String key)
	{
		return rbundle.getString(key);
	}
	
	public String getCapitalize(String key)
	{
		return StringUtils.capitalize(get(key));
	}
	
	
	public String getError()
	{
		return getCapitalize("ERROR");
	}
}
