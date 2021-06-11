import org.magic.api.beans.*;


	 dao.listStocks().each{ c->
		try {
			MagicCard newC = provider.getCardById(c.getMagicCard().getId());
			if(newC!=null)
			{
				System.out.println(newC);
				c.setMagicCard(newC);
				dao.saveOrUpdateStock(c);
			}
		} catch (Exception e) {
			printf(c + ";ERROR;" + e+"\n");
		} 
	 };
