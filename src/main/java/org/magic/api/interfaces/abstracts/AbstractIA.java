package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.enums.EnumLayout;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGIA;
import org.magic.services.network.URLTools;
import org.magic.services.tools.MTG;

import com.google.gson.JsonObject;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.chat.request.json.JsonArraySchema;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.chat.request.json.JsonSchemaElement;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;

public abstract class AbstractIA extends AbstractMTGPlugin implements MTGIA {

	private static final String NEW_CARD_QUERY = "generate a new magic the gathering card in json format ";
	private static final String NEW_SET_QUERY = "generate a new magic the gathering set of X cards in json format ";
	private static final String NEW_DECK_QUERY = "generate a magic the gathering deck in json format ";
	
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.IA;
	}

	public abstract ChatModel getEngine(ResponseFormat format);
	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}

	private JsonSchemaElement getCardElement() {
		return JsonObjectSchema.builder()
        .addStringProperty("name")
        .addProperty("supertypes", JsonArraySchema.builder().items(JsonStringSchema.builder().build()).build())
        .addProperty("types", JsonArraySchema.builder().items(JsonStringSchema.builder().build()).build())
        .addProperty("subtypes", JsonArraySchema.builder().items(JsonStringSchema.builder().build()).build())
        .addStringProperty("cost")
        .addIntegerProperty("cmc")
        .addEnumProperty("rarity",List.of(EnumRarity.values()).stream().map(en->en.getName()).toList())
        .addStringProperty("flavor")
        .addStringProperty("text")
        .addIntegerProperty("cmc")
        .addIntegerProperty("power")
        .addIntegerProperty("toughness")
        .addIntegerProperty("loyalty")
        .required("name", "types", "cost", "text","flavor","rarity","cmc")
        .build();
	}
	
	private ResponseFormat getGeneratedCardFormat()
	{
		return ResponseFormat.builder()
					        .type(ResponseFormatType.JSON)
					        .jsonSchema(JsonSchema.builder()
					                .name("MTG Card")
						                .rootElement(getCardElement())
					                .build())
					        .build();
	}
	
	private ResponseFormat getGeneratedSetFormat() {
		return ResponseFormat.builder()
		        .type(ResponseFormatType.JSON)
		        .jsonSchema(JsonSchema.builder()
		                .name("Set")
			                .rootElement(JsonObjectSchema.builder()
			        		        .addProperty("cards", JsonArraySchema.builder().items(getCardElement()).build())
			                .build())
		                .build())
		        .build();
	}
	
	private ResponseFormat getGeneratedDeckFormat()
	{
		return ResponseFormat.builder()
		        .type(ResponseFormatType.JSON)
		        .jsonSchema(JsonSchema.builder()
		                .name("MTG Deck")
			                .rootElement(JsonObjectSchema.builder()
			        		        .addStringProperty("name")
			        		        .addProperty("mainboard", JsonArraySchema.builder().items(
			        		        		JsonObjectSchema.builder()
			        		        		.addStringProperty("name")
			        		        		.addIntegerProperty("quantity")
			        		        		.build()).build())
			        		        .addProperty("sideboard", JsonArraySchema.builder().items(
			        		        		JsonObjectSchema.builder()
			        		        		.addStringProperty("name")
			        		        		.addIntegerProperty("quantity")
			        		        		.build()).build())
			        		        .addStringProperty("description")
			                .build())
		                .build())
		        .build();
	}


	@Override
	public MTGCard generateRandomCard(String description, MTGEdition set, String number) throws IOException {
		
		var obj = URLTools.toJson(getEngine(getGeneratedCardFormat()).chat(NEW_CARD_QUERY  +( (description==null || description.isEmpty())?"": " with this description  : "+description))).getAsJsonObject();
		logger.debug("return : {}",obj);
		var mc = new MTGCard();
			
		if(number!=null)
				mc.setNumber(number);
			
			 readJson(mc,obj,set);
		return mc;
	}
	
	@Override
	public MTGDeck generateDeck(String description) throws IOException {
		var obj = URLTools.toJson(getEngine(getGeneratedDeckFormat()).chat(NEW_DECK_QUERY  +( (description==null || description.isEmpty())?"": " with this description  : "+description))).getAsJsonObject();
		
		var d = new MTGDeck();
			d.setDescription(obj.get("description").getAsString());
			d.setName(obj.get("name").getAsString());
			
		obj.get("mainboard").getAsJsonArray().forEach(je->{
			
			try {
				var mc = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(je.getAsJsonObject().get("name").getAsString(), null,true).get(0);
				d.getMain().put(mc, je.getAsJsonObject().get("quantity").getAsInt());
				notify(mc);
			} catch (Exception e) {
				logger.error("can't find card {}",je.getAsJsonObject());
			}
			
		});
		
		obj.get("sideboard").getAsJsonArray().forEach(je->{
			
			try {
				var mc = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(je.getAsJsonObject().get("name").getAsString(), null,true).get(0);
				d.getSideBoard().put(mc, je.getAsJsonObject().get("quantity").getAsInt());
				notify(mc);
			} catch (Exception e) {
				logger.error("can't find card {}",je.getAsJsonObject());
			}
			
		});
		return d;
	}
	
	@Override
	public List<MTGCard> generateSet(String description, MTGEdition set, int qty) throws IOException {
		var obj = URLTools.toJson(getEngine(getGeneratedSetFormat()).chat(NEW_SET_QUERY.replace("X",""+qty)  +( (description==null || description.isEmpty())?"": " with this description  : "+description))).getAsJsonObject();
		var list = new ArrayList<MTGCard>();
		int number = 1;
		
		for(var je : obj.get("cards").getAsJsonArray())
		{ 
			var mc = new MTGCard();
				readJson(mc,je.getAsJsonObject(),set);
				mc.setNumber(String.valueOf(number++));
				list.add(mc);
		}
		return list;
	}
	

	private void readJson(MTGCard mc, JsonObject obj, MTGEdition set) {
		try {
		  mc.setName(obj.get("name").getAsString());
		  mc.setCost(obj.get("cost").getAsString());
		  mc.setColors(EnumColors.parseByManaCost(mc.getCost()));
		  mc.setColorIdentity(EnumColors.parseByManaCost(mc.getCost()));
		  mc.setCmc(obj.get("cmc").getAsInt());
		  mc.setRarity(EnumRarity.rarityByName(obj.get("rarity").getAsString()));
		  mc.setFlavor(obj.get("flavor").getAsString());
		  mc.setText(obj.get("text").getAsString());
		  
		  obj.get("types").getAsJsonArray().forEach(je->mc.getTypes().add(je.getAsString()));
		  
		  if( obj.get("supertypes")!=null)
			  obj.get("supertypes").getAsJsonArray().forEach(je->mc.getSupertypes().add(je.getAsString()));
		  
		  if( obj.get("subtypes")!=null)
			  obj.get("subtypes").getAsJsonArray().forEach(je->mc.getSubtypes().add(je.getAsString()));
		  
		  mc.setLayout(EnumLayout.NORMAL);
		  mc.setFrameVersion("2015");
		  mc.setId(DigestUtils.sha256Hex(set.getSet()+ mc.getName()));	 
		  mc.setEdition(set);
		  mc.getEditions().add(set);
		  
		  
		  if(obj.get("power")!=null && !obj.get("power").isJsonNull()) {
			  mc.setPower(obj.get("power").getAsString());
		  }
		 
		  if(obj.get("toughness")!=null && !obj.get("toughness").isJsonNull()) 
			  mc.setToughness(obj.get("toughness").getAsString());
		 
		  
		
		  
		  
		  if(obj.get("loyalty")!=null && !obj.get("loyalty").isJsonNull()) {
			  mc.setLoyalty(obj.get("loyalty").getAsInt());
		  }
		}
		catch(Exception e)
		{
			logger.error(e);
		}

	}
	
}
