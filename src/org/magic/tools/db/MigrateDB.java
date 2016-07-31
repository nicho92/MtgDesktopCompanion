package org.magic.tools.db;

import java.sql.SQLException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.dao.impl.HsqlDAO;
import org.magic.api.dao.impl.MysqlDAO;

public class MigrateDB {

	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		HsqlDAO hsql = new HsqlDAO();
			hsql.init();
		MysqlDAO mysql = new MysqlDAO();
			mysql.init();
			
			
		for(MagicCollection col : hsql.getCollections())
			for(MagicCard mc : hsql.getCardsFromCollection(col))
			{
				mysql.saveCard(mc, col);
			}
		
		
		
	}
	
	
	
	
}
