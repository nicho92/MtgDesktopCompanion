package org.magic.services.keywords;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.magic.api.beans.MTGKeyWord;
import org.magic.api.beans.MTGKeyWord.EVENT;
import org.magic.api.beans.MTGKeyWord.TYPE;
import org.magic.tools.URLTools;


public class MTGGamePediaKeywordProvider extends AbstractKeyWordsManager {
	
	private static final String SELEC_ABILITIES = "div.mw-category-group a[title]";
	private List<MTGKeyWord> statics;
	private List<MTGKeyWord> activateds;
	private List<MTGKeyWord> triggereds;
	private List<MTGKeyWord> actions;
	private List<MTGKeyWord> words;
	private List<MTGKeyWord> evergreens;
	
	
	public static final String BASE_URI="https://mtg.gamepedia.com/";

	public List<MTGKeyWord> getEvergreens()
	{
		if(evergreens==null || evergreens.isEmpty())
		{
			evergreens = new ArrayList<>();
			try {
				Document d = URLTools.extractHtml("https://mtg.gamepedia.com/Evergreen");
				
				for(Element e: d.select("table.wikitable tr:has(td)"))
				{
					String name = e.getElementsByTag("td").get(1).text();
					String type = e.getElementsByTag("td").get(2).text();
					
					
					if(type.equalsIgnoreCase("Action"))
						evergreens.add(new MTGKeyWord(name, TYPE.ACTION));
					else if(type.equalsIgnoreCase("Static ability"))
						evergreens.add(new MTGKeyWord(name, EVENT.STATIC,TYPE.ABILITIES));
					else if(type.equalsIgnoreCase("Activated ability"))
						evergreens.add(new MTGKeyWord(name, EVENT.ACTIVATED,TYPE.ABILITIES));
					else if(type.equalsIgnoreCase("Triggered ability"))
						evergreens.add(new MTGKeyWord(name, EVENT.TRIGGERED,TYPE.ABILITIES));
				}
			} catch (IOException e) {
				logger.error("error loading evergreens",e);
			}
		}
		return evergreens;
	}
	
	
	
	@Override
	public List<MTGKeyWord> getStaticsAbilities()
	{
		if(statics==null || statics.isEmpty())
		{
			statics=parse("Static",SELEC_ABILITIES,true).stream().map(s->new MTGKeyWord(s, MTGKeyWord.EVENT.STATIC, MTGKeyWord.TYPE.ABILITIES)).collect(Collectors.toList());
			statics.addAll(getEvergreens().stream().filter(mt->mt.getEvent()==EVENT.STATIC).collect(Collectors.toList()));
		}
		return statics;
	}
	
	@Override
	public List<MTGKeyWord> getActivatedAbilities()
	{
		if(activateds==null || activateds.isEmpty())
		{
			activateds = parse("Activated",SELEC_ABILITIES,true).stream().map(s->new MTGKeyWord(s, MTGKeyWord.EVENT.ACTIVATED, MTGKeyWord.TYPE.ABILITIES)).collect(Collectors.toList());
			activateds.addAll(getEvergreens().stream().filter(mt->mt.getEvent()==EVENT.ACTIVATED).collect(Collectors.toList()));
		}
		
		return activateds;
	}
	
	@Override
	public List<MTGKeyWord> getTriggeredAbilities()
	{
		if(triggereds==null || triggereds.isEmpty())
		{
			triggereds = parse("Triggered",SELEC_ABILITIES,true).stream().map(s->new MTGKeyWord(s, MTGKeyWord.EVENT.TRIGGERED, MTGKeyWord.TYPE.ABILITIES)).collect(Collectors.toList());
			triggereds.addAll(getEvergreens().stream().filter(mt->mt.getEvent()==EVENT.TRIGGERED).collect(Collectors.toList()));
		}
		
		return triggereds;
	}
	
	@Override
	public List<MTGKeyWord> getKeywordActions()
	{
		if(actions==null || actions.isEmpty())
		{
				actions= parse("Keyword_action","div.crDiv li a",false).stream().map(s->new MTGKeyWord(s, MTGKeyWord.TYPE.ACTION)).collect(Collectors.toList());
				actions.addAll(getEvergreens().stream().filter(mt->mt.getType()==TYPE.ACTION).collect(Collectors.toList()));
				actions.add(new MTGKeyWord("Flip", TYPE.ACTION));
		}
		return actions;
	}
	
	@Override
	public List<MTGKeyWord> getWordsAbilities()
	{
		if(words==null || words.isEmpty())
			words= parse("Ability_word","div.div-col li a",false).stream().map(s->new MTGKeyWord(s, MTGKeyWord.TYPE.WORD)).collect(Collectors.toList());

		return words;
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
