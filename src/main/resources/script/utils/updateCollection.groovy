import org.magic.api.beans.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

	var formater = new SimpleDateFormat("yyyy-MM-dd");
	var dateBefore = formater.parse("2022-08-01");
	var col = new MTGCollection("Library");
	

String ed ="MIR";
//dao.listEditionsIDFromCollection(col).each{ ed->
 	 System.out.println("========================================="+ed);
	 dao.listCardsFromCollection(col, new MTGEdition(ed)).stream().filter(mc->mc.getDateUpdated().before(dateBefore)).collect(Collectors.toList()).each{ c->
		try {
			var newC = provider.getCardByScryfallId(c.getScryfallId());
			if(newC!=null)
			{
				dao.updateCard(c,newC, col);
				System.out.println(""+col + ";" + newC.getEdition() + ";" + c + ";OK;" + newC+"\n");
			}
			else
			{
				System.out.println(""+col + ";" + newC.getEdition() + ";" + c + ";NOTFOUND;" + newC+"\n");
			}
		} catch (Exception e) {
			System.out.println(""+col + ";" + newC.getEdition() + ";" + c + ";ERROR;" + e+"\n");
		} 
	 };
	 
//};
