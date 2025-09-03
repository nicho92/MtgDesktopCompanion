package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.enums.EnumLayout;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.beans.technical.MTGProperty;
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

	
	private static final String QUANTITY = "quantity";
	private static final String NAME = "name";
	private static final String LOYALTY = "loyalty";
	private static final String TOUGHNESS = "toughness";
	private static final String POWER = "power";
	private static final String TEXT = "text";
	private static final String FLAVOR = "flavor";
	private static final String RARITY = "rarity";
	private static final String CMC = "cmc";
	private static final String COST = "cost";
	private static final String SUBTYPES = "subtypes";
	private static final String TYPES = "types";
	private static final String SUPERTYPES = "supertypes";
	
	
	private static final String NEW_CARD_QUERY = "generate a new magic the gathering card in json format ";
	private static final String WITH_THIS_DESCRIPTION = " based on the theme  : ";
	
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
        .addStringProperty(NAME)
        .addProperty(SUPERTYPES, JsonArraySchema.builder().items(JsonStringSchema.builder().build()).build())
        .addProperty(TYPES, JsonArraySchema.builder().items(JsonStringSchema.builder().build()).build())
        .addProperty(SUBTYPES, JsonArraySchema.builder().items(JsonStringSchema.builder().build()).build())
        .addStringProperty(COST)
        .addIntegerProperty(CMC)
        .addEnumProperty(RARITY,List.of(EnumRarity.values()).stream().map(en->en.getName()).toList())
        .addStringProperty(FLAVOR)
        .addStringProperty(TEXT)
        .addIntegerProperty(POWER)
        .addIntegerProperty(TOUGHNESS)
        .addIntegerProperty(LOYALTY)
        .required(NAME, TYPES, COST, TEXT,FLAVOR,RARITY,CMC)
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
			        		        .addStringProperty(NAME)
			        		        .addProperty("mainboard", JsonArraySchema.builder().items(
			        		        		JsonObjectSchema.builder()
			        		        		.addStringProperty(NAME)
			        		        		.addIntegerProperty(QUANTITY)
			        		        		.build()).build())
			        		        .addProperty("sideboard", JsonArraySchema.builder().items(
			        		        		JsonObjectSchema.builder()
			        		        		.addStringProperty(NAME)
			        		        		.addIntegerProperty(QUANTITY)
			        		        		.build()).build())
			        		        .addStringProperty("description")
			        		        .required("description","mainboard")
			                .build())
			           .build())
		        .build();
	}


	@Override
	public MTGCard generateRandomCard(String description, MTGEdition set, String number) throws IOException {
		
		var obj = URLTools.toJson(getEngine(getGeneratedCardFormat()).chat(NEW_CARD_QUERY  +( (description==null || description.isEmpty())?"": WITH_THIS_DESCRIPTION+description))).getAsJsonObject();
		logger.debug("return : {}",obj);
		var mc = new MTGCard();
			
		if(number!=null)
				mc.setNumber(number);
			
			 readJson(mc,obj,set);
		return mc;
	}
	
	@Override
	public MTGDeck generateDeck(String description) throws IOException {
		var obj = URLTools.toJson(getEngine(getGeneratedDeckFormat()).chat(NEW_DECK_QUERY  +( (description==null || description.isEmpty())?"": WITH_THIS_DESCRIPTION+description))).getAsJsonObject();
		
		var d = new MTGDeck();
			d.setDescription(obj.get("description").getAsString());
			d.setName(obj.get(NAME).getAsString());
			
		obj.get("mainboard").getAsJsonArray().forEach(je->{
			
			try {
				var mc = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(je.getAsJsonObject().get(NAME).getAsString(), null,true).get(0);
				d.getMain().put(mc, je.getAsJsonObject().get(QUANTITY).getAsInt());
				notify(mc);
			} catch (Exception _) {
				logger.error("can't find card {}",je.getAsJsonObject());
			}
			
		});
		
		obj.get("sideboard").getAsJsonArray().forEach(je->{
			
			try {
				var mc = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(je.getAsJsonObject().get(NAME).getAsString(), null,true).get(0);
				d.getSideBoard().put(mc, je.getAsJsonObject().get(QUANTITY).getAsInt());
				notify(mc);
			} catch (Exception _) {
				logger.error("can't find card {}",je.getAsJsonObject());
			}
			
		});
		return d;
	}
	
	@Override
	public List<MTGCard> generateSet(String description, MTGEdition set, int qty) throws IOException {
		var obj = URLTools.toJson(getEngine(getGeneratedSetFormat()).chat(NEW_SET_QUERY.replace("X",""+qty)  +( (description==null || description.isEmpty())?"": WITH_THIS_DESCRIPTION+description))).getAsJsonObject();
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
	
	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		var map = super.getDefaultAttributes();
		map.put("TEMPERATURE", MTGProperty.newIntegerProperty("0.7", "You can think of temperature like randomness, with low value is being least random (or most deterministic) and max being most random (least deterministic)", 0, 1));
		map.put("MAX_TOKEN", MTGProperty.newIntegerProperty("2000","Maximum size of the prompt",50,5000));
		map.put("LOG", MTGProperty.newBooleanProperty(FALSE, "enable chat model logger"));
		return map;
	}
	
	

	private void readJson(MTGCard mc, JsonObject obj, MTGEdition set) {
		try {
		  mc.setName(obj.get(NAME).getAsString());
		  mc.setCost(obj.get(COST).getAsString());
		  mc.setColors(EnumColors.parseByManaCost(mc.getCost()));
		  mc.setColorIdentity(EnumColors.parseByManaCost(mc.getCost()));
		  mc.setCmc(obj.get(CMC).getAsInt());
		  mc.setRarity(EnumRarity.rarityByName(obj.get(RARITY).getAsString()));
		  mc.setFlavor(obj.get(FLAVOR).getAsString());
		  mc.setText(obj.get(TEXT).getAsString());
		  
		  obj.get(TYPES).getAsJsonArray().forEach(je->mc.getTypes().add(je.getAsString()));
		  
		  if( obj.get(SUPERTYPES)!=null)
			  obj.get(SUPERTYPES).getAsJsonArray().forEach(je->mc.getSupertypes().add(je.getAsString()));
		  
		  if( obj.get(SUBTYPES)!=null)
			  obj.get(SUBTYPES).getAsJsonArray().forEach(je->mc.getSubtypes().add(je.getAsString()));
		  
		// can be overriden by PictureEditor
		  mc.setLayout(EnumLayout.NORMAL);
		  mc.setFrameVersion("2015"); 
		
		  
		  mc.setId(DigestUtils.sha256Hex(set.getSet()+ mc.getName()));	 
		  mc.setEdition(set);
		  mc.getEditions().add(set);
		  
		  
		  if(obj.get(POWER)!=null && !obj.get(POWER).isJsonNull()) {
			  mc.setPower(obj.get(POWER).getAsString());
		  }
		 
		  if(obj.get(TOUGHNESS)!=null && !obj.get(TOUGHNESS).isJsonNull()) 
			  mc.setToughness(obj.get(TOUGHNESS).getAsString());
		  
		  if(obj.get(LOYALTY)!=null && !obj.get(LOYALTY).isJsonNull()) {
			  mc.setLoyalty(obj.get(LOYALTY).getAsInt());
		  }
		}
		catch(Exception e)
		{
			logger.error(e);
		}

	}
	
}
