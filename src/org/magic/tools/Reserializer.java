package org.magic.tools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.dao.impl.HsqlDAO;
import org.magic.api.interfaces.MagicCardsProvider;

public class Reserializer {

	public static void main(String[] args) throws Exception {
		HsqlDAO dao = new HsqlDAO();
		dao.init();
		
		MagicCollection col = new MagicCollection();
		
		col.setName("Library");
		
		
		System.out.println(dao.getCardsFromCollection(col).size());
		
	}
}
