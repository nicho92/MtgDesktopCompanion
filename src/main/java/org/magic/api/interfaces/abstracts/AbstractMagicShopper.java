package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.shop.Contact;
import org.magic.api.beans.shop.Transaction;
import org.magic.api.beans.technical.RetrievableTransaction;
import org.magic.api.interfaces.MTGDao;
import org.magic.api.interfaces.MTGShopper;
import org.magic.services.MTGControler;
import org.magic.services.network.MTGHttpClient;
import org.magic.services.tools.MTG;

public abstract class AbstractMagicShopper extends AbstractMTGPlugin implements MTGShopper {

	protected MTGHttpClient client;
	private Contact defaultContact;
	
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.SHOPPER;
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	

	protected Transaction buildTransaction(RetrievableTransaction rt) {
		 var t = new Transaction();
		 	  t.setDateCreation(rt.getDateTransaction());
		 	  t.setSourceShopName(rt.getSource());
		 	  t.setSourceShopId(rt.getSourceId());
		 	  t.setMessage(rt.getComments());
		 	  
				if (defaultContact == null)
					defaultContact = MTGControler.getInstance().getWebConfig().getContact();
		 	  
		 	  t.setContact(defaultContact);
		 	  
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
