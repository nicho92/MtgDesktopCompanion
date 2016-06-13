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
		MagicCardsProvider prov = MagicFactory.getInstance().getEnabledProviders().get(0);
		HsqlDAO dao = new HsqlDAO();
		dao.init();
		
		ResultSet rs = dao.executeQuery("select name,edition,collection from CARDS");
		int count=0;
		
		ResultSet tot = dao.executeQuery("select count(name) from CARDS");
		tot.next();
		int total=tot.getInt(1);
		int res=0;
		
		
		/*while (rs.next())
		{
			String name = rs.getString("name");
			String edition = rs.getString("edition");
			String collection = rs.getString("collection");
			
			MagicEdition ed = new MagicEdition();
			ed.setId(edition);
			List<MagicCard> cards = prov.searchCardByCriteria("name", name, ed);
			int ind=0;

			if(cards.size()>0)
			{	
				if(cards.size()==1)
				{
					res = dao.updateSerializedCard(cards.get(0),edition,collection);
					//System.out.println("Update " + cards.get(0).getName() + " " + edition + " " + collection + " =" + res);
					ind=0;
				}
				else
				{
					res = dao.updateSerializedCard(cards.get(ind),edition,collection);
					//System.out.println("Update " + cards.get(ind).getName() + " " + edition + " " + collection+ " =" + res);
					ind++;
				}
				
				if(res==0)
				{
					System.out.println("erreur " + edition +" " + collection + " " +  cards);
				}
				
			}
			else
			{
				System.out.println("NULL FOR " + name + " " + edition);
			}
			
			count++;
			//System.out.print("\r"+count+"/"+total);
		}*/

		MagicCollection col = new MagicCollection();
		col.setName("Library");
		List<MagicCard> list = dao.getCardsFromCollection(col);
		
		

	}

}
