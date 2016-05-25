package org.magic.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MagicCardsProvider;
import org.magic.api.providers.impl.MtgjsonProvider;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


//TEST
public class NumberUpdater {

	static List<MagicCard> mc;
	
	public static void main(String[] args) throws Exception {
		String unavailableEds[] = {"LEA","LEB","ARN","2ED","ATQ","3ED","FEM","4ED","ICE","CHR","HML","ALL","RQS","MIR","MGB","ITP","5ED","POR","VAN","WTH","TMP","STH","PO2","ATH","BRB","S00","DDQ"};
		
		JsonObject jsObj = new Gson().fromJson(new FileReader(new File(System.getProperty("user.home")+"/magicDeskCompanion/AllSets-x.json")), JsonObject.class);
		MtgjsonProvider prov = new MtgjsonProvider();
		
		for(String ed : unavailableEds)
		//String ed = "LEA";
		{	
			System.out.println("modify " + ed);
			mc = updateNumber(ed, prov);
			JsonArray cards = jsObj.getAsJsonObject(ed).getAsJsonArray("cards");
			for(int i=0;i<cards.size();i++)
			{
				JsonObject card = cards.get(i).getAsJsonObject();
				if(card.get("number") == null)
					card.addProperty("number", getVal(card.getAsJsonPrimitive("id").getAsString()));
			}
			
		}
		FileWriter fw = new FileWriter(new File(System.getProperty("user.home")+"/magicDeskCompanion/AllSets-x.json").getAbsolutePath());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(jsObj.toString());
		bw.close();
		
		System.out.println("end");
		
	}
	
	
	private static String getVal(String id) {
		for(MagicCard c : mc)
			if(c.getId().equals(id))
				return c.getNumber();
		
		return "";
	}


	public static List<MagicCard> updateNumber(String ed,MagicCardsProvider prov) throws Exception {
		
	
		List<MagicCard> editionsCards = prov.searchCardByCriteria("set",ed,null);
		
		int index=1;
		for(MagicCard mc : editionsCards)
			if(mc.getColors().size()==1 && mc.getColors().contains("White"))
				mc.setNumber(String.valueOf(index++));
		
		for(MagicCard mc : editionsCards)
			if(mc.getColors().size()==1 && mc.getColors().contains("Blue"))
				mc.setNumber(String.valueOf(index++));
		
		for(MagicCard mc : editionsCards)
			if(mc.getColors().size()==1 && mc.getColors().contains("Black"))
				mc.setNumber(String.valueOf(index++));
		
		for(MagicCard mc : editionsCards)
			if(mc.getColors().size()==1 && mc.getColors().contains("Red"))
				mc.setNumber(String.valueOf(index++));
		
		for(MagicCard mc : editionsCards)
			if(mc.getColors().size()==1 && mc.getColors().contains("Green"))
				mc.setNumber(String.valueOf(index++));
		
		for(MagicCard mc : editionsCards)
			if(mc.getColors().size()>1)
				mc.setNumber(String.valueOf(index++));
		
		for(MagicCard mc : editionsCards)
			if(mc.getColors().size()==0 && mc.getTypes().get(0).equals("Artifact"))
				mc.setNumber(String.valueOf(index++));
		
		for(MagicCard mc : editionsCards)
			if(mc.getColors().size()==0 && mc.getTypes().get(0).equals("Land"))
				mc.setNumber(String.valueOf(index++));
		
		
		return editionsCards;
	}
}
