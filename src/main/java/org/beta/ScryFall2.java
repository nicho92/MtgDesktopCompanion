package org.beta;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.tools.UITools;

import forohfor.scryfall.api.MTGCardQuery;

public class ScryFall2 extends AbstractCardsProvider{

	
	public static void main(String[] args) throws IOException {
		  new ScryFall2().listEditions().forEach(System.out::println);
	      
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MagicCard getCardById(String id, MagicEdition ed) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MagicCard> searchCardByCriteria(String att, String crit, MagicEdition me, boolean exact)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MagicCard getCardByNumber(String id, MagicEdition me) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getLanguages() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getWebSite() throws MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "Scryfall";
	}

	@Override
	protected List<String> loadQueryableAttributs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MagicEdition> loadEditions() throws IOException {
		return MTGCardQuery.getSets().stream().map(e->{
			MagicEdition ed = new MagicEdition();
						 ed.setBlock(e.getBlockName());
						 ed.setCardCount(e.getCardCount());
						 ed.setId(e.getCode());
						 ed.setSet(e.getName());
						 ed.setReleaseDate(UITools.formatDate(e.getReleasedAt()));
			return ed;
			
		}).collect(Collectors.toList());
		
	}
}
