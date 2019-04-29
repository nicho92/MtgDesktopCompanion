package org.magic.api.main;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

public class DAOCardUpdater {
	private static Logger logger = MTGLogger.getLogger(DAOCardUpdater.class);
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException{
		
		MTGDao dao = MTGControler.getInstance().getEnabled(MTGDao.class);
		dao.init();
		
		MTGCardsProvider prov = MTGControler.getInstance().getEnabled(MTGCardsProvider.class);
		prov.init();
		
		
		dao.listCollections().forEach(col->{
			try {
				dao.listEditionsIDFromCollection(col).forEach(set->{
					try {
						dao.listCardsFromCollection(col, new MagicEdition(set)).forEach(c->{
							try {
								
								MagicCard newC = prov.searchCardByCriteria("number", c.getCurrentSet().getNumber(), new MagicEdition(set), true).get(0);
								dao.update(c,newC, col);
								logger.info("Update " + c + " " + c.getCurrentSet() +" "+ col +" DONE");
							} catch (Exception e) {
								logger.error(col + " " + set + " " + c + "->" + e);
							} 
						});
					} catch (SQLException e) {
						logger.error(col + " " + set + "->" + e);
					}
					
					
				});
			} catch (SQLException e) {
				logger.error(e);
			}
			
			
		});

	}

}
