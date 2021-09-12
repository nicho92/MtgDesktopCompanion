import org.magic.api.beans.*;


	 dao.listStocks().each{ c->
		try {
			MagicCard newC = provider.getCardByNumber(c.getProduct().getCurrentSet().getNumber(), c.getEdition());
			if(newC!=null)
			{
				System.out.println(newC);
				c.setProduct(newC);
				dao.saveOrUpdateCardStock(c);
			}
		} catch (Exception e) {
			printf(c + ";ERROR;" + e+"\n");
		} 
	 };
