package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.magic.api.beans.enums.EnumTransactionDirection;
import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.RetrievableTransaction;
import org.magic.api.interfaces.MTGShopper;
import org.magic.services.MTGControler;
import org.magic.services.network.MTGHttpClient;

public abstract class AbstractMagicShopper extends AbstractMTGPlugin implements MTGShopper {

	protected MTGHttpClient client;
	private Contact defaultContact;
	
	protected abstract Currency getCurrency();

	@Override
	public PLUGINS getType() {
		return PLUGINS.SHOPPER;
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}
	

	protected Transaction buildTransaction(RetrievableTransaction rt) {
		 var t = new Transaction();
		 	  t.setDateCreation(rt.getDateTransaction());
		 	  t.setSourceShopName(rt.getSource());
		 	  t.setSourceShopId(rt.getSourceId());
		 	  t.setMessage(rt.getComments());
		 	  t.setTypeTransaction(EnumTransactionDirection.BUY);
		 	 t.setCurrency(getCurrency());
		 	 
				if (defaultContact == null)
					defaultContact = MTGControler.getInstance().getWebshopService().getWebConfig().getContact();
		 	  
		 	  t.setContact(defaultContact);
		 	  
		 	  return t;
	}

	
	@Override
	public Transaction getTransactionById(String id) throws IOException {
		var opt = listOrders().stream().filter(rt->rt.getSourceId().equals(id)).findFirst();
		
		if(opt.isPresent())
			return getTransaction(opt.get());
		else
			throw new IOException("No Transaction found for id="+id);
	
	}
	

	
	@Override
	public List<Transaction> listTransactions(List<RetrievableTransaction> rt) throws IOException {
		var ret = new ArrayList<Transaction>();
		
		for(RetrievableTransaction selected : rt)
			ret.add(getTransaction(selected));
		
		return ret;
	}
	
}
