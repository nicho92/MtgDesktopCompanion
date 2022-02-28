import org.magic.api.beans.*;

	 dao.listStocks().each{ c->
		try {
			System.out.println(c);
			MagicCard newC = provider.getCardByNumber(c.getProduct().getCurrentSet().getNumber(), c.getProduct().getEdition());
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
