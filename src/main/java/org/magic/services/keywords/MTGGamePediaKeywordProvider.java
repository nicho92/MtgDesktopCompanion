package org.magic.services.keywords;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.magic.api.beans.MTGDocumentation;
import org.magic.api.beans.MTGKeyWord;
import org.magic.api.beans.MTGKeyWord.TYPE;
import org.magic.api.beans.MTGNotification.FORMAT_NOTIFICATION;
import org.magic.tools.URLTools;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class MTGGamePediaKeywordProvider extends AbstractKeyWordsManager {
	
	private static final String SELEC_ABILITIES = "div.mw-category-group a[title]";
	private List<MTGKeyWord> statics;
	private List<MTGKeyWord> activateds;
	private List<MTGKeyWord> triggereds;
	private List<MTGKeyWord> actions;
	
	public static final String BASE_URI="https://mtg.gamepedia.com/";

	
	@Override
	public List<MTGKeyWord> getStaticsAbilities()
	{
		if(statics==null || statics.isEmpty())
			statics=parse("Static",SELEC_ABILITIES,true).stream().map(s->new MTGKeyWord(s, MTGKeyWord.EVENT.STATIC, MTGKeyWord.TYPE.ABILITIES)).collect(Collectors.toList());
		
		return statics;
	}
	
	@Override
	public List<MTGKeyWord> getActivatedAbilities()
	{
		if(activateds==null || activateds.isEmpty())
			activateds = parse("Activated",SELEC_ABILITIES,true).stream().map(s->new MTGKeyWord(s, MTGKeyWord.EVENT.ACTIVATED, MTGKeyWord.TYPE.ABILITIES)).collect(Collectors.toList());
		
		return activateds;
	}
	
	@Override
	public List<MTGKeyWord> getTriggeredAbilities()
	{
		if(triggereds==null || triggereds.isEmpty())
			triggereds = parse("Triggered",SELEC_ABILITIES,true).stream().map(s->new MTGKeyWord(s, MTGKeyWord.EVENT.TRIGGERED, MTGKeyWord.TYPE.ABILITIES)).collect(Collectors.toList());
		
		return triggereds;
	}
	
	@Override
	public List<MTGKeyWord> getKeywordActions()
	{
		if(actions==null || actions.isEmpty())
		{
				actions= parse("Keyword_action","div.crDiv li a",false).stream().map(s->new MTGKeyWord(s, MTGKeyWord.TYPE.ACTION)).collect(Collectors.toList());
				actions.add(new MTGKeyWord("Flip", TYPE.ACTION));
				
		}
		return actions;
	}
	
	
	private List<String> parse(String page,String select,boolean isKeyword)
	{
		
		List<String> list = new ArrayList<>();
			try {
				
				String url = isKeyword ? BASE_URI+"Category:Keywords/"+page : BASE_URI+page;
				
				Document d = URLTools.extractHtml(url);
				list = d.select(select).stream().map(Element::text).collect(Collectors.toList());
				list.remove(page+" ability");
				list.remove("Keyword_action");
				
			} catch (IOException e) {
				logger.error(e);
			}
			return list;
	}
	

	
}
