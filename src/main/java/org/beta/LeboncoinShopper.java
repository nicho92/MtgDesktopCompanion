package org.beta;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.tools.InstallCert;

public class LeboncoinShopper extends AbstractMagicPricesProvider {

	private static final String ITEM_SUPP = "item_supp";
	private static final String ITEM_INFOS = "item_infos";
	private static final String MAX_RESULT = "MAX_RESULT";
	private static final String LOAD_CERTIFICATE = "LOAD_CERTIFICATE";
	private SimpleDateFormat formatter;



	public LeboncoinShopper() {
		super();
		init();
	}

	
	private void init() {
		formatter = new SimpleDateFormat(getString("DATE_FORMAT"));
		if(getBoolean(LOAD_CERTIFICATE))
		{
			try {
				InstallCert.installCert("leboncoin.fr");
				setProperty(LOAD_CERTIFICATE, "false");
			} catch (Exception e1) {
				logger.error(e1);
			}
		}
	}

	@Override
	public List<MagicPrice> getPrice(MagicEdition me, MagicCard card) throws IOException
	{

		return  new ArrayList<>();
	}

	@Override
	public String getName() {
		return "LeBonCoin";
	}

	@Override
	public void initDefault() {
		setProperty("TITLE_ONLY", "0");
		setProperty("MAX_PAGE", "2");
		setProperty(MAX_RESULT, "30");
		setProperty("URL", "https://www.leboncoin.fr/recherche/?text=%SEARCH%&page=%PAGE%");
		setProperty("DATE_FORMAT", "dd MMMM. H:m");
		setProperty("ROOT_TAG", "section[class=tabsContent block-white dontSwitch]");
		setProperty(LOAD_CERTIFICATE, "false");
	}
	
	@Override
	public STATUT getStatut() {
		return STATUT.ABANDONNED;
	}


	@Override
	public void alertDetected(List<MagicPrice> okz) {
		//do nothing
		
	}


}
