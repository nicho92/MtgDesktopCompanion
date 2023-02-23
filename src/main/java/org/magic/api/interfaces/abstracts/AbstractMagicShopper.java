package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.RetrievableTransaction;
import org.magic.api.interfaces.MTGShopper;

public abstract class AbstractMagicShopper extends AbstractMTGPlugin implements MTGShopper {

	@Override
	public PLUGINS getType() {
		return PLUGINS.SHOPPER;
	}
	
	

	protected Transaction buildTransaction(RetrievableTransaction rt) {
		 var t = new Transaction();
		 	  t.setDateCreation(rt.getDateTransaction());
		 	  t.setSourceShopName(rt.getSource());
		 	  t.setSourceShopId(rt.getSourceId());
		 	  t.setMessage(rt.getComments());
		 	  return t;
	}


	
	@Override
	public List<Transaction> listTransactions(List<RetrievableTransaction> rt) throws IOException {
		var ret = new ArrayList<Transaction>();
		
		for(RetrievableTransaction selected : rt)
			ret.add(getTransaction(selected));
		
		return ret;
	}
	
}
