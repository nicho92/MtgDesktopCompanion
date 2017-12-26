package org.magic.services;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

public class LanguageService {

	ResourceBundle bundle;
	StringBuffer temp;
	
	private static String BUNDLE = "locales.lang";
	
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
		temp = new StringBuffer();
		bundle = ResourceBundle.getBundle(BUNDLE,getDefault());
	}
	
	public LanguageService(Locale l) {
		temp = new StringBuffer();
		bundle = ResourceBundle.getBundle(BUNDLE,l);
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
			bundle = ResourceBundle.getBundle(BUNDLE,l);
		else
			bundle = ResourceBundle.getBundle(BUNDLE,getDefault());
	}
	
	public String get(String key)
	{
		return bundle.getString(key);
	}
	
	public String getCapitalize(String key)
	{
		return StringUtils.capitalize(get(key));
	}
	

}
