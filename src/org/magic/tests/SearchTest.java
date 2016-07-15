package org.magic.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.magic.api.providers.impl.MtgjsonProvider;

public class SearchTest {

	
	public static void main(String[] args) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		MtgjsonProvider prov = new MtgjsonProvider();
		
		Map<String,String> c = BeanUtils.describe(prov.searchCardByCriteria("name", "Rune-Tail", null).get(0));
		
		for(String k : c.keySet())
			System.out.println(k+"="+c.get(k));
	}
	
	
}
