package org.magic.tools.db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.providers.impl.MtgjsonProvider;
import org.magic.services.MagicFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


//TEST
public class NumberUpdater {

	static List<MagicCard> mc;
	public static String unavailableEds[] = {"LEA","LEB","ARN","2ED","ATQ","3ED","FEM","4ED","ICE","CHR","HML","ALL","RQS","VIS","MIR","MGB","ITP","5ED","POR","VAN","WTH","TMP","STH","PO2","ATH","BRB","S00","DDQ","CED","CEI","DKM"};
	
	public static void main(String[] args) {
		try {
			MagicFactory.getInstance().getEnabledProviders() .init();
			NumberUpdater.update("VIS");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void update(String ed) throws Exception {
		
		if(MagicFactory.getInstance().getEnabledProviders() instanceof MtgjsonProvider)
		{
				JsonObject jsObj = new Gson().fromJson(new FileReader(new File(MagicFactory.CONF_DIR,"AllSets-x.json")), JsonObject.class);
			
				//for(String ed : unavailableEds)
				{	
					mc = updateNumber(ed);
					JsonArray cards = jsObj.getAsJsonObject(ed).getAsJsonArray("cards");
					for(int i=0;i<cards.size();i++)
					{
						JsonObject card = cards.get(i).getAsJsonObject();
						if(card.get("number") == null)
							card.addProperty("number", getVal(card.getAsJsonPrimitive("id").getAsString()));
					}
					
				}
				FileWriter fw = new FileWriter(new File(MagicFactory.CONF_DIR,"AllSets-x.json").getAbsolutePath());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(jsObj.toString());
				bw.close();
		}
		else
		{
			throw new Exception("Provider should be MtgjsonProvider");
		}
	

	}
	
	
	private static boolean isBasic(MagicCard mc)
	{
		if(mc.getName().equals("Plains")||mc.getName().equals("Island")||mc.getName().equals("Swamp")||mc.getName().equals("Mountain")||mc.getName().equals("Forest"))
			return true;
			else
			return false;
	}
	
	private static String getVal(String id) {
		for(MagicCard c : mc)
			if(c.getId().equals(id))
				return c.getNumber();
		
		return "";
	}


	private static List<MagicCard> updateNumber(String ed) throws Exception {
		
	
		List<MagicCard> editionsCards = MagicFactory.getInstance().getEnabledProviders().searchCardByCriteria("set",ed,null);
		
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
			if(mc.getColors().size()==0 && mc.getTypes().get(0).equals("Land") && !isBasic(mc))
				mc.setNumber(String.valueOf(index++));

		for(MagicCard mc : editionsCards)
			if(mc.getColors().size()==0 && mc.getTypes().get(0).equals("Land") && mc.getName().equals("Plains"))
				mc.setNumber(String.valueOf(index++));

		for(MagicCard mc : editionsCards)
			if(mc.getColors().size()==0 && mc.getTypes().get(0).equals("Land") && mc.getName().equals("Island"))
				mc.setNumber(String.valueOf(index++));

		for(MagicCard mc : editionsCards)
			if(mc.getColors().size()==0 && mc.getTypes().get(0).equals("Land") && mc.getName().equals("Swamp"))
				mc.setNumber(String.valueOf(index++));

		for(MagicCard mc : editionsCards)
			if(mc.getColors().size()==0 && mc.getTypes().get(0).equals("Land") && mc.getName().equals("Mountain"))
				mc.setNumber(String.valueOf(index++));

		for(MagicCard mc : editionsCards)
			if(mc.getColors().size()==0 && mc.getTypes().get(0).equals("Land") && mc.getName().equals("Forest"))
				mc.setNumber(String.valueOf(index++));

		
		
		
		return editionsCards;
	}
}
