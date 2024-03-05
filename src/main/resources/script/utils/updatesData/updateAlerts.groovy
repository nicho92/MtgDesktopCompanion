import org.magic.api.beans.*;


	 dao.listAlerts().each{ c->
		try {
			var newC = provider.getCardByScryfallId(c.getCard().getScryfallId());
			if(newC!=null)
			{
				System.out.println(newC);
				c.setCard(newC);
				dao.updateAlert(c);
			}
		} catch (Exception e) {
			printf(c + ";ERROR;" + e+"\n");
		} 
	 };
