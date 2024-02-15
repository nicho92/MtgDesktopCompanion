import org.magic.api.beans.*;

	 dao.listStocks().each{ c->
		try {
			var newC = provider.getCardByScryfallId(c.getProduct().getScryfallId());
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
