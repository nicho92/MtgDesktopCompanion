import org.magic.api.beans.*;

MagicCollection col = new MagicCollection("Library");
String ed ="E01";
//dao.listEditionsIDFromCollection(col).each{ ed->
 	 System.out.println("========================================="+ed);
	 dao.listCardsFromCollection(col, new MagicEdition(ed)).each{ c->
		try {
			MagicCard newC = provider.getCardById(c.getId(), c.getCurrentSet());
			if(newC!=null)
			{
				dao.updateCard(c,newC, col);
				printf(""+col + ";" + c.getCurrentSet() + ";" + c + ";OK;" + newC+"\n");
			}
			else
			{
				printf(""+col + ";" + c.getCurrentSet() + ";" + c + ";NOTFOUND;" + c+"\n");
			}
		} catch (Exception e) {
			printf(""+col + ";" + c.getCurrentSet() + ";" + c + ";ERROR;" + e+"\n");
		} 
	 };
	 
//};
