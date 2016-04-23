package org.magic.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.providers.impl.MtgjsonProvider;

public class UpdateDB {

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
			
		MtgjsonProvider prov = new MtgjsonProvider();
		List<MagicCard> list = prov.searchCardByCriteria("name", "cheap ass", null);
		
		System.out.println(list.get(0).getText());
	}
}
