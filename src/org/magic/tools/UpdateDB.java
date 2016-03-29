package org.magic.tools;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.providers.impl.MtgjsonProvider;
import org.magic.db.HsqlDAO;

public class UpdateDB {

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		HsqlDAO dao = new HsqlDAO();
		MtgjsonProvider prov = new MtgjsonProvider();
						prov.init();
		
		
		MagicCollection col = new MagicCollection();
						col.setName("Needed");
						

		  for(MagicCard mc : dao.getCardsFromCollection(col))
			{
			
		  	try {
					MagicCard temp = prov.getCardById(mc.getId());
					dao.update(temp,mc.getEditions().get(0),col);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	

	}
}
