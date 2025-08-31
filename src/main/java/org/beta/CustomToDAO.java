package org.beta;

import java.io.IOException;
import java.sql.SQLException;

import org.magic.api.customs.impl.DAOCustomManager;
import org.magic.api.customs.impl.FileCustomManager;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGControler;
import org.magic.services.tools.MTG;

public class CustomToDAO {

	public static void main(String[] args) throws IOException, SQLException {
		
		MTGControler.getInstance();
		MTG.getEnabledPlugin(MTGDao.class).init();
		
		var source = new FileCustomManager();
		var dest = new DAOCustomManager();
		
		
		
		source.listCustomSets().forEach(ed->{
			try {
				dest.saveCustomSet(ed);
				
				source.listCustomsCards(ed).forEach(mc->{
					try {
						dest.saveCustomCard(ed,mc);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				
				
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		
		
		System.exit(0);
	}

}
