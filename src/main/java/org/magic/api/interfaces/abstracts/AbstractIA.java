package org.magic.api.interfaces.abstracts;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.enums.EnumLayout;
import org.magic.api.beans.enums.EnumRarity;
import org.magic.api.interfaces.MTGIA;
import org.magic.services.network.URLTools;

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

	private static final String NEW_CARD_QUERY = "generate a new magic card in json format";
	
	@Override
	public PLUGINS getType() {
		return PLUGINS.IA;
	}

	public abstract ChatModel getEngine(ResponseFormat format);
	
	@Override
	public STATUT getStatut() {
		return STATUT.BETA;
	}
	
	protected ResponseFormat getGeneratedCardFormat()
	{
		return ResponseFormat.builder()
					        .type(ResponseFormatType.JSON)
					        .jsonSchema(JsonSchema.builder()
					                .name("MTG Card")
					                .rootElement(getCardFormat())
					                .build())
					        .build();
	}
	
	protected ResponseFormat getGeneratedDeckFormat()
	{
		return ResponseFormat.builder()
					        .type(ResponseFormatType.JSON)
					        .jsonSchema(JsonSchema.builder()
					                .name("deck")
					                .rootElement(getCardFormat())
					                .build())
					        .build();
	}

	private JsonSchemaElement getCardFormat() {
		return JsonObjectSchema.builder()
		        .addStringProperty("name")
		        .addProperty("types", JsonArraySchema.builder().items(JsonStringSchema.builder().build()).build())
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
	

	@Override
	public MTGCard generateRandomCard(String description, MTGEdition set, String number) throws IOException {
		
		var obj = URLTools.toJson(getEngine(getGeneratedCardFormat()).chat(NEW_CARD_QUERY  +( (description==null || description.isEmpty())?"": " with this description  : "+description))).getAsJsonObject();
		logger.debug("return : {}",obj);
		var mc = new MTGCard();
			 mc.setLayout(EnumLayout.NORMAL);
			 mc.setEdition(set);
			 mc.getEditions().add(set);
			 mc.setNumber(number);
			 readJson(mc,obj);
		return mc;
	}
	
	
	
	
	private void readJson(MTGCard mc, JsonObject obj) {
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
		  
		  if(obj.get("power")!=null && !obj.get("power").isJsonNull()) {
			  mc.setPower(obj.get("power").getAsString());
			  mc.setToughness(obj.get("toughness").getAsString());
		  }
		  
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
