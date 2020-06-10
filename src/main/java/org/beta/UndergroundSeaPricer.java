package org.beta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;

import org.jsoup.nodes.Document;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.AbstractMagicPricesProvider;
import org.magic.tools.URLTools;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;


public class UndergroundSeaPricer extends AbstractMagicPricesProvider {

	private static final String BASE_URL="http://www.usmtgproxy.com/";
	
	public static void main(String[] args) throws IOException {
		new UndergroundSeaPricer().getLocalePrice(null, null);
	}
	
	
	@Override
	public String getName() {
		return "UnderGroundSea";
	}

	@Override
	protected List<MagicPrice> getLocalePrice(MagicEdition me, MagicCard card) throws IOException {
		String month = String.format("%02d",Calendar.getInstance().get(Calendar.MONTH)-1);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String url = BASE_URL + "wp-content/uploads/"+year+"/"+month+"/proxycardslist.html";
		
		
		Document d = URLTools.extractHtml(url);
		AstNode root = new Parser().parse(d.select("script").get(1).html(), "", 1);
		List<MagicPrice> ret = new ArrayList<>();
		
		root.visit(visitedNode -> {
			
			MagicPrice mp = null;
			
			if(visitedNode.getType()==Token.NEW)
			{
				mp = new MagicPrice();
				mp.setSeller(getName());
				mp.setSite(BASE_URL);
				mp.setCurrency(Currency.getInstance("USD"));
				
				logger.debug("---NEW");
			}
			
			if(visitedNode.getType()==Token.EXPR_RESULT)
			{
				String value = visitedNode.toSource();
				
				if(value.startsWith("cardslist") || value.startsWith("createTable") || value.startsWith("window.onload") || value.startsWith("confirmBtn") || value.startsWith("infos"))
					return false;
				
				String content = visitedNode.toSource().substring(visitedNode.toSource().indexOf('.')+1).trim();
				
				if(mp!=null&&content.startsWith("cardName") && content.toLowerCase().contains(card.getName().toLowerCase()))
				{
					ret.add(mp);
				}
				
				
				
				logger.debug(content);
			}
			
			return true;
		});
		System.out.println(ret);
		return ret;
	}

}
