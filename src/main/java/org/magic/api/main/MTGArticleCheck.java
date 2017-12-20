package org.magic.api.main;

import org.magic.servers.impl.PricesCheckerTimer;
import org.magic.services.MTGControler;

public class MTGArticleCheck {

	public static void main(String[] args) throws Exception {

		MTGControler.getInstance().getEnabledDAO().init();
		PricesCheckerTimer serv = new PricesCheckerTimer();
		serv.enableGUINotify(false);
		serv.start();

	}

}
