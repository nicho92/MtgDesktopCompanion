package org.beta;

import java.io.IOException;
import java.sql.SQLException;

import org.magic.api.interfaces.MTGGedStorage;
import org.magic.services.MTGControler;
import org.magic.tools.MTG;

public class TEstMain {

	public static void main(String[] args) throws SQLException, IOException {
		MTGControler.getInstance().init();
		
		
		var ged = MTG.getEnabledPlugin(MTGGedStorage.class);
		
		
		ged.listAll().forEach(g->{
			
			System.out.println(g.getClasse() + " " + g.getName());
		});
		

	}

}
