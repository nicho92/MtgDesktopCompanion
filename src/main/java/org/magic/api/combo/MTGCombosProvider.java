package org.magic.api.combo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.magic.api.beans.MTGCombo;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.abstracts.AbstractComboProvider;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;
import org.magic.tools.URLTools;

public class MTGCombosProvider extends AbstractComboProvider {

	
	private static final String BASE ="https://mtgcombos.com/";
	
	
	public static void main(String[] args) {
		
		MagicCard mc = new MagicCard();
		mc.setName("Black Lotus");
		
		new MTGCombosProvider().getComboWith(mc);
	}
	
	@Override
	public List<MTGCombo> getComboWith(MagicCard mc) {
		List<MTGCombo> ret = new ArrayList<>();
		
		
		try {
			Document d = RequestBuilder.build().url(BASE+"index.php").setClient(URLTools.newClient()).method(METHOD.POST).addContent("search", mc.getName()).addContent("submit", "Search >").toHtml();
			
			System.out.println(d);
			
			
		} catch (IOException e) {
			logger.error(e);
		}
		
		
		return ret;
	}

	@Override
	public String getName() {
		return "MTGCombos";
	}

}
