import org.magic.api.beans.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

	SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
	Date dateBefore = formater.parse("2021-08-01");
	
	MagicCollection col = new MagicCollection("Library");
	

//String ed ="PLS";
dao.listEditionsIDFromCollection(col).each{ ed->
 	 System.out.println("========================================="+ed);
	 dao.listCardsFromCollection(col, new MagicEdition(ed)).stream().filter(mc->mc.getDateUpdated().before(dateBefore)).collect(Collectors.toList()).each{ c->
		try {
			MagicCard newC = provider.getCardByNumber(c.getCurrentSet().getNumber(), c.getCurrentSet());
			if(newC!=null)
			{
				dao.updateCard(c,newC, col);
				printf(""+col + ";" + c.getCurrentSet() + ";" + c + ";OK;" + newC+"\n");
			}
			else
			{
				printf(""+col + ";" + c.getCurrentSet() + ";" + c + ";NOTFOUND;" + newC+"\n");
			}
		} catch (Exception e) {
			printf(""+col + ";" + c.getCurrentSet() + ";" + c + ";ERROR;" + e+"\n");
		} 
	 };
	 
};
