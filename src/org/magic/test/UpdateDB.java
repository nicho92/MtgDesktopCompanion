package org.magic.test;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.dao.impl.HsqlDAO;
import org.magic.api.dao.impl.MysqlDAO;

public class UpdateDB {

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		HsqlDAO dao = new HsqlDAO();
			dao.init();
		MysqlDAO daom = new MysqlDAO();
			daom.init();
		
		for(MagicCollection col : dao.getCollections())
			for(MagicCard mc : dao.getCardsFromCollection(col))
			{
				try {
					daom.saveCard(mc, col);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
	}
}
