import org.magic.api.beans.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

	var formater = new SimpleDateFormat("yyyy-MM-dd");
	//var dateBefore = formater.parse("2024-01-24");
	var dateBefore = new Date();
	var col = new MTGCollection("Library");
	

String ed ="10E";
//dao.listEditionsIDFromCollection(col).each{ ed->
 	 System.out.println("========================================="+ed);
	 dao.listStocks(col, new MTGEdition(ed)).stream().filter(mcs->mcs.getDateUpdate().before(dateBefore)).collect(Collectors.toList()).each{ mcs->
		try {
			var newC = provider.getCardByScryfallId(mcs.getProduct().getScryfallId());
			mcs.setProduct(newC);
			dao.saveOrUpdateStock(mcs);
		} catch (Exception e) {
			System.out.println(""+col + ";" + newC.getEdition() + ";" + c + ";ERROR;" + e+"\n");
		} 
	 };
//};
