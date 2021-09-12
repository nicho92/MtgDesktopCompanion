import org.magic.api.beans.*;


	 dao.listAlerts().each{ c->
		try {
			MagicCard newC = provider.getCardByNumber(c.getCard().getCurrentSet().getNumber(), c.getCard().getCurrentSet());
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
