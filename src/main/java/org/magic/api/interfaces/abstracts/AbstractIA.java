package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.enums.EnumLayout;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.MTGIA;
import org.magic.services.MTGControler;
import org.magic.services.network.URLTools;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public abstract class AbstractIA extends AbstractMTGPlugin implements MTGIA {

	
	protected static final String SET_QUERY = "Tell me more about MTG set : ";
	protected static final String CARD_QUERY = "Tell me more about MTG card ";
	protected static final String DECK_QUERY = "Build a magic the gathering deck with this cards : ";
	protected static final String NEW_CARD_QUERY = "generate a new magic card in json format";
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.IA;
	}
	
	protected MTGCard parseIaCardSuggestion(JsonObject obj) {

		var mc = new MTGCard();
			 mc.setLayout(EnumLayout.NORMAL);
		
			 
			 try {
				 mc.setName(read(obj,"name").getAsString());
			 }
			 catch(Exception _)
			 {
				 //do nothing
			 }
			 
			 
			 try {
				 mc.setText(read(obj,"text","oracleText","oracle_text").getAsString());
			 }
			 catch(Exception _)
			 {
				 //do nothing				 
			 }
			 
			 
			
			 try {
				 var rarity = read(obj,"rarity").getAsString();
				 if(rarity.toLowerCase().contains("mythic"))
					 mc.setRarity(EnumRarity.MYTHIC);
				 else if(rarity.toLowerCase().contains("rare"))
					 mc.setRarity(EnumRarity.RARE);
				 else if(rarity.toLowerCase().contains("unco"))
					 mc.setRarity(EnumRarity.UNCOMMON);
				 else
					 mc.setRarity(EnumRarity.COMMON);
				 
			 }
			 catch(Exception _)
			 {
				 //do nothing
			 }
			 
			 try {
				 mc.setFlavor(read(obj,"flavor","flavorText","flavor_text").getAsString());
			 }
			 catch(Exception _)
			 {
				 //do nothing
			 }
			 
			 
			 
			 
			 if(read(obj,"type","type_line").isJsonPrimitive())
			 {
				 mc.getTypes().add(read(obj,"type","type_line").getAsString());
			 }
			 
			
				 try {
					 read(obj,"types","type_line").getAsJsonArray().forEach(je->mc.getTypes().add(je.getAsString()));
				 }catch(Exception _)
				 {
					 //do nothing
				 }
			 
			 try {
				 read(obj,"supertypes").getAsJsonArray().forEach(je->mc.getSupertypes().add(je.getAsString()));
			 }
			 catch(Exception _)
			 {
				 //do nothing
			 }
			 try {
				 read(obj,"subtypes").getAsJsonArray().forEach(je->mc.getSubtypes().add(je.getAsString()));
			 }
			 catch(Exception _)
			 {
				 //do nothing
			 }
			 
			 try {
			 	 mc.setPower(read(obj,"power").getAsString());
				 mc.setToughness(read(obj,"toughness").getAsString());
			 }	
			 catch(Exception _)
			 {
				 //do nothing
			 }
			 
			 try {
			 	 mc.setLoyalty(read(obj,"loyalty").getAsInt());
			}	
			 catch(Exception _)
			 {
				 //do nothing
			 }
			 
			 
			 if(!mc.isLand()) {
				 mc.setCost(read(obj,"manaCost","mana_cost","cost").getAsString());
			 }
			 
			 
			 mc.setColors(EnumColors.parseByManaCost(mc.getCost()));
			 
		return mc;
	}
	
	private JsonElement read(JsonObject obj, String... atts)
	{
		
		for(String att: atts)
		{
			if(obj.get(att)!=null && !obj.get(att).isJsonNull())
				return obj.get(att);
			
		}
		
		return JsonNull.INSTANCE;
	}
	
	@Override
	public String suggestDeckWith(List<MTGCard> cards) throws IOException {
		if(cards.isEmpty())
			throw new IOException("You should add some cards before asking IA");
		
		return ask(DECK_QUERY + cards.stream().map(MTGCard::getName).collect(Collectors.joining("/")));
	}

	
	@Override
	public String describe(MTGEdition ed) throws IOException {
		if(ed ==null)
			throw new IOException("You should select a set before calling IA");
		
		return  ask( SET_QUERY+" \"" + ed.getSet() +"\" in "+MTGControler.getInstance().getLocale().getDisplayLanguage(Locale.US));
	}
	
	@Override
	public String describe(MTGCard card) throws IOException {
		if(card ==null)
			throw new IOException("You should select a card before calling IA");
		
		return  ask( CARD_QUERY+" \"" + card.getName() +"\" in "+MTGControler.getInstance().getLocale().getDisplayLanguage(Locale.US));
	}

	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var map = super.getDefaultAttributes();
		map.put("SYSTEM_MSG", new MTGProperty("You are a helpful assistant that generate Magic the gathering card in json format.","contextual prompt for the chatbot"));
		return map;
	}
		
	@Override
	public MTGCard generateRandomCard(String description) throws IOException {
		
		var ret = ask(NEW_CARD_QUERY  +( (description==null || description.isEmpty())?"": " with this description  : "+description));
			
		if(ret==null)
			return null;
		
		ret = StringUtils.substringBetween(ret,"\u0060\u0060\u0060");
		
		var obj = URLTools.toJson(ret).getAsJsonObject();
		return parseIaCardSuggestion(obj);
	}
	
}
