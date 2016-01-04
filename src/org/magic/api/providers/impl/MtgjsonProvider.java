package org.magic.api.providers.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.MagicRuling;
import org.magic.api.interfaces.MagicCardsProvider;

import com.google.gson.JsonElement;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.EvaluationListener;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;


public class MtgjsonProvider implements MagicCardsProvider{

	private String urlSetJson = "http://mtgjson.com/json/AllSets-x.json";
	private String urlVersion = "http://mtgjson.com/json/version.json";
	
	private File fileSetJson = new File(System.getProperty("user.home")+"/magicDeskCompanion/AllSets-x.json");
	private File fversion = new File(System.getProperty("user.home")+"/magicDeskCompanion/version");
	
	private Reader readSet;
	private List<MagicCard> list;
	private ReadContext ctx;
	private Map<String,List<MagicCard>> cacheCard;
	private List<MagicEdition> eds;
	private String version;
	
	
	static final Logger logger = LogManager.getLogger(MtgjsonProvider.class.getName());

	private boolean hasNewVersion()
	{
		try{
		logger.debug("check new version of " + toString());
		InputStreamReader fr = new InputStreamReader( new URL(urlVersion).openStream(),"ISO-8859-1");
  	  	BufferedReader br = new BufferedReader(fr);
  	  	version =  br.readLine();
  	  	br.close();
  	  	if(!version.equals(new BufferedReader(new FileReader(fversion)).readLine()))
  	  		return true;
  	 
  	  	logger.debug("check new version of " + this + ": up to date");
  	  	return false;
		}
		catch(Exception e)
		{
			logger.error("Error getting last version " +e);
			return false;
		}
	}
	
	
	public void init()
	{
		logger.debug("init " + this +" ");
		Configuration.setDefaults(new Configuration.Defaults() {

		    private final JsonProvider jsonProvider = new GsonJsonProvider();
		    private final MappingProvider mappingProvider = new GsonMappingProvider();

		    
		    @Override
		    public JsonProvider jsonProvider() {
		        return jsonProvider;
		    }

		    @Override
		    public MappingProvider mappingProvider() {
		        return mappingProvider;
		    }

		    @Override
		    public Set<Option> options() {
		        return EnumSet.noneOf(Option.class);
		    }
		    
		});
		Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
		
		try 
		{	 
			if(!fileSetJson.exists())
			{
				logger.debug("datafile does not exist. Downloading it");
				FileUtils.copyURLToFile(new URL(urlSetJson), fileSetJson);
				FileUtils.copyInputStreamToFile(new URL(urlVersion).openStream(), fversion);
			}
			
			
			if(hasNewVersion())
			{
				logger.debug("new version datafile exist. Downloading it");
				FileUtils.copyURLToFile(new URL(urlSetJson), fileSetJson);
				FileUtils.copyInputStreamToFile(new URL(urlVersion).openStream(), fversion);
			}
			
		 readSet = new InputStreamReader(new FileInputStream(fileSetJson),"UTF-8");
		 
		 cacheCard= new HashMap<String,List<MagicCard>>();
		 logger.debug("init " + this +" : parsing db file");
		 ctx = JsonPath.parse(fileSetJson);
		 logger.debug("init " + this +" : OK");
		} 
		catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public MagicCard getCardById(String id) throws Exception {
		return searchCardByCriteria("id", id).get(0);
	}
	
	
	
	public List<MagicCard> searchCardByCriteria(String att,String crit) throws IOException {
		
		final List<String> currentSet=new ArrayList<String>();
		
		list= new ArrayList<MagicCard>();

		String jsquery="$..cards[?(@."+att+" =~ /^.*"+crit.replaceAll("\\+", " " )+".*$/i)]";

		
		if(att.equalsIgnoreCase("set"))
		{
			if(cacheCard.get(crit)!=null)
			{
				logger.debug(crit + " is already in cache. Loading from it");
				return cacheCard.get(crit);
			}
			
			
			if(crit.length()==4)
			{
				crit=crit.substring(0, 1)+crit.substring(1).toUpperCase();
				jsquery="$."+crit+".cards";	
			}
			else
			{
				jsquery="$."+crit.toUpperCase()+".cards";	
			}
		}

		if(att.equalsIgnoreCase("multiverseid")|| att.equalsIgnoreCase("cmc"))
		{
			jsquery="$..cards[?(@."+att+" == "+crit+")]";
		}

		logger.debug("searchCardByCriteria : " + jsquery);
	
		List<Map<String,Object>> cardsElement = ctx.withListeners(new EvaluationListener() {
			public EvaluationContinuation resultFound(FoundResult fr) {
				
				if(fr.path().startsWith("$"))
					currentSet.add(fr.path().substring(fr.path().indexOf("$[")+3, fr.path().indexOf("]")-1));
				return null;
			}
		}).read(jsquery,List.class);
		
		int indexSet=0;
		for(Map<String,Object> map : cardsElement)
		{
			
			MagicCard mc = new MagicCard();
	 		   mc.setName(map.get("name").toString());
	 		  
	 		   if(map.get("multiverseid")!=null)
	 			   mc.setMultiverseid((int)(double)map.get("multiverseid"));

	 		   
	 		  mc.setId(String.valueOf(map.get("id")));
	 		   
	 		  mc.setText(String.valueOf(map.get("text")));
	 		   
	 		  if(map.get("cmc")!=null)
	 			  mc.setCmc((int)Double.parseDouble(String.valueOf(map.get("cmc"))));
	 		   
	 		   mc.setPower(String.valueOf(map.get("power")));
	 		   mc.setToughness(String.valueOf(map.get("toughness")));
	 		   mc.setFlavor(String.valueOf(map.get("flavor")));
	 		   mc.setArtist(String.valueOf(map.get("artist")));
	 		   mc.setLayout(String.valueOf(map.get("layout")));
	 		   
	 		   
	 		   if(map.get("types")!=null)
	 			  mc.getTypes().addAll((List<String>)map.get("types"));
	 		   
	 		   if(map.get("subtypes")!=null)
	 			   mc.getSubtypes().addAll((List<String>)map.get("subtypes"));
	 		  
	 		   if(map.get("variations")!=null)
	 			  mc.getVariations().addAll((List<Integer>)map.get("variations"));
	 		  
	 		   if(map.get("colorIdentity")!=null)
	 			   mc.getColorIdentity().addAll((List<String>)map.get("colorIdentity"));
	 		  
	 		   if(map.get("watermark")!=null)
	 			  mc.setWatermarks(String.valueOf(map.get("watermark")));
	 		   
	 		   if(map.get("number")!=null)
	 			  mc.setNumber(String.valueOf(map.get("number")));
	 		   
	 		  if(map.get("loyalty")!=null)
	 			  mc.setLoyalty((int)(double)map.get("loyalty"));
	 		   
	 		  if(map.get("manaCost")!=null)
	 			  mc.setCost(String.valueOf(map.get("manaCost")));
	 		  else
	 			  mc.setCost("");
	 			  
	 		 if(map.get("legalities")!=null) 
	 		 {
	 			 for(Map<String,Object> mapFormats : (List<Map>)map.get("legalities"))
	 			 {
	 				 MagicFormat mf = new MagicFormat();
	 				 	mf.setFormat(String.valueOf(mapFormats.get("format")));
	 				 	mf.setLegality(String.valueOf(mapFormats.get("legality")));
	 				 	mc.getLegalities().add(mf);
	 			 }
	 		 }
	 		  
	 		  
	 		  
 			   if(map.get("rulings")!=null)
 			   {
 				  for(Map<String,Object> mapRules : (List<Map>)map.get("rulings"))
 				  {
 					  MagicRuling mr = new MagicRuling();
 					  			  mr.setDate(String.valueOf(mapRules.get("date")));
 					  			  mr.setText(String.valueOf(mapRules.get("text")));
 					 mc.getRulings().add(mr);
 				  }
 				  
 				  
 			   }
	 		   
	 		   String codeEd;
	 		   if(currentSet.size()==1)
	 			   codeEd=currentSet.get(0);
	 		   else
	 			   codeEd=currentSet.get(indexSet++);
	 			   
 			   MagicEdition me = getSetById(codeEd);
	 			    me.setRarity(String.valueOf(map.get("rarity")));
	 			    me.setNumber(mc.getNumber());
	 			    if(mc.getMultiverseid()==null)
	 			    	me.setMultiverse_id(String.valueOf(0));
	 			    else
	 			    	me.setMultiverse_id(String.valueOf(mc.getMultiverseid()));
	 			
	 			    mc.getEditions().add(me);
	 					
					/*get other sets*/
	 			    
	 			   if(!me.getRarity().equals("Basic Land")) 
	 			   for(String print : (List<String>)map.get("printings"))
	 			   {
	 				   if(!print.equalsIgnoreCase(codeEd)){
	 					  MagicEdition meO = getSetById(print);
		 			    if(mc.getMultiverseid()==null)
		 			    	meO.setMultiverse_id(String.valueOf(0));
		 			    else
		 			    	initEditionVars(mc, meO);
		 			    
		 			    mc.getEditions().add(meO); 
	 				   }
	 			   }
	 			   
	 			   
	 			   
	 			   
	 			   
	 		   MagicCardNames defnames = new MagicCardNames();
	 		  		defnames.setLanguage("English");
	 		 		defnames.setName(mc.getName());
	 		 		if(mc.getMultiverseid()!=null)
	 		 			defnames.setGathererId(mc.getMultiverseid());
	 		 		
		    	mc.getForeignNames().add(defnames);
		    	
	 		   if(map.get("foreignNames")!=null)
	 		   {
	 			  
	 			   for(Map<String,Object> mapNames : (List<Map>)map.get("foreignNames"))
	 			   {
	 				  MagicCardNames fnames = new MagicCardNames();
	 			    	fnames.setLanguage(String.valueOf(mapNames.get("language")));
	 			    	fnames.setName(String.valueOf(mapNames.get("name")));
	 			    	
	 			    	if(mapNames.get("multiverseid")!=null)
	 			    		fnames.setGathererId((int)(double)mapNames.get("multiverseid"));
	 			    	
	 			    	
	 			    mc.getForeignNames().add(fnames);
	 			   }
	 		   }
	 		  list.add(mc);
		}
		currentSet.clear();
		
		if(att.equalsIgnoreCase("set"))
			cacheCard.put(crit, list);
		
		return list;
		
	}

	public List<MagicEdition> searchSetByCriteria(String att,String crit) throws IOException {
		
		String jsquery="";
		if(crit==null)
			jsquery="$.*";
		
		
		logger.debug("get edition with " + att +"="+crit);
		
		if(eds!=null)
		{
			logger.debug("editions already loaded. return cache");
			return eds;
		}
		
		final List<String> codeEd=new ArrayList<String>();
		ctx.withListeners(new EvaluationListener() {
				
				@Override
				public EvaluationContinuation resultFound(FoundResult fr) {
					
					if(fr.path().startsWith("$"))
						codeEd.add(fr.path().substring(fr.path().indexOf("$[")+3, fr.path().indexOf("]")-1));
		
					return null;
				}
			}).read(jsquery,List.class);
		
		eds = new ArrayList<MagicEdition>();

		for(String codeedition : codeEd)
		{
			eds.add(getSetById(codeedition));
		}
		
		return eds;
		
	}
	
	public MagicEdition getSetById(String id)  {
		
		
		MagicEdition me = new MagicEdition();
					me.setId(id);
					me.setSet(ctx.read("$."+id+".name",String.class));
					me.setReleaseDate(ctx.read("$."+id+".releaseDate",String.class));
					me.setBorder(ctx.read("$."+id+".border",String.class));
					me.setType(ctx.read("$."+id+".type",String.class));
					
					if(me.getCardCount()==0)
						me.setCardCount(ctx.read("$."+id+".cards", List.class).size());//long !
					
				
		try{
			me.setBooster(ctx.read("$."+id+".booster",List.class));
		}
		catch(PathNotFoundException ex)
		{	
			//logger.error("no booster definition found for " + id);
		}
		
		return me;
		
	}

	public List<String> getListType() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getListSubTypes() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getQueryableAttributs() {
		return new String[]{"name","text","artist","type","rarity","flavor","cmc","set","watermark","power","toughness","layout"};
	}

	public String toString() {
		return "MTG Json Provider";
	}

	public String[]  getLanguages() {
		return new String[]{"English","Chinese Simplified","Chinese Traditional","French","German","Italian","Japanese","Korean","Portugese","Russian","Spanish"};
	}

	private void initEditionVars(MagicCard mc,MagicEdition me)
	{
		String jsquery="$."+me.getId().toUpperCase()+".cards[?(@.name=~ /^.*"+mc.getName()+".*$/i)]";
		
		try {
			List<Map<String,Object>> cardsElement = ctx.read(jsquery,List.class);
			
			for(Map<String,Object> map : cardsElement)
			{
				me.setRarity(String.valueOf(map.get("rarity")));
				me.setNumber(String.valueOf(map.get("number")));
				 if(map.get("multiverseid")!=null)
		 			   me.setMultiverse_id(String.valueOf((int)(double)map.get("multiverseid")));
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	public List<MagicCard> openBooster(MagicEdition me) {

		logger.debug("opening booster for " + me );
		List<MagicCard> common = new ArrayList<MagicCard>();
		List<MagicCard> uncommon = new ArrayList<MagicCard>();
		List<MagicCard> rare= new ArrayList<MagicCard>();
		
		
			String jsquery="$."+me.getId().toUpperCase()+".cards[?(@.rarity =~ /^.*Common.*$/)]";
			List<Map<String,Object>> cardsElement = ctx.read(jsquery,List.class);
			for(Map<String,Object> map : cardsElement)
			{
				MagicCard mc = new MagicCard();
						  mc.setId(String.valueOf(map.get("id")));
						  mc.setName(String.valueOf(map.get("name")));
						   if(map.get("multiverseid")!=null)
				 			   mc.setMultiverseid((int)(double)map.get("multiverseid"));
						  
						  MagicEdition edition = new MagicEdition();  
						   		edition.setId(me.getId());
						   		initEditionVars(mc, edition);
						   		
						  mc.getEditions().add(edition);
						  
				common.add(mc);
			}
			Collections.shuffle(common);		   
			
			jsquery="$."+me.getId().toUpperCase()+".cards[?(@.rarity =~ /^.*Uncommon.*$/)]";
			cardsElement = ctx.read(jsquery,List.class);
			for(Map<String,Object> map : cardsElement)
			{
				MagicCard mc = new MagicCard();
						  mc.setId(String.valueOf(map.get("id")));
						  mc.setName(String.valueOf(map.get("name")));
						   if(map.get("multiverseid")!=null)
				 			   mc.setMultiverseid((int)(double)map.get("multiverseid"));
						   MagicEdition edition = new MagicEdition();  
					   		edition.setId(me.getId());
					   		initEditionVars(mc, edition);
					   		
					  mc.getEditions().add(edition);
						   
				uncommon.add(mc);
			}
			Collections.shuffle(uncommon);
			
			
			jsquery="$."+me.getId().toUpperCase()+".cards[?(@.rarity =~ /^.*Rare.*$/)]";
			cardsElement = ctx.read(jsquery,List.class);
			for(Map<String,Object> map : cardsElement)
			{
				MagicCard mc = new MagicCard();
						  mc.setId(String.valueOf(map.get("id")));
						  mc.setName(String.valueOf(map.get("name")));
						   if(map.get("multiverseid")!=null)
				 			   mc.setMultiverseid((int)(double)map.get("multiverseid"));
						   
						   MagicEdition edition = new MagicEdition();  
					   		edition.setId(me.getId());
					   		initEditionVars(mc, edition);
					   		
					  mc.getEditions().add(edition);
						   
						   
				rare.add(mc);
			}
			Collections.shuffle(rare);
			
			
			List<MagicCard> resList = new ArrayList<MagicCard>();
			resList.addAll(common.subList(0, 10));
			resList.addAll(uncommon.subList(0, 4));
			resList.add(rare.get(0));
			
		return resList;
	}


	@Override
	public MagicCard getCardByNumber(String num, MagicEdition me) throws Exception {
		String jsquery="$."+me.getId().toUpperCase()+".cards[?(@.number =~ /^.*"+num+".*$/)]";
		List<Map<String,Object>> cardsElement = ctx.read(jsquery,List.class);
		Map<String,Object> map;
		
		String id = "";
		
		if(cardsElement.size()>0)
		{
			map=cardsElement.get(0);
			id = map.get("id").toString();
		}
		else //for old edition, number is at null. So we take his position in 'cards' array
		{
			int parseId=0;
			try{ 
				parseId= Integer.parseInt(num);
				jsquery="$."+me.getId().toUpperCase()+".cards["+(parseId-1)+"]";
				id = ctx.read(jsquery,JsonElement.class).getAsJsonObject().get("id").getAsString();
			}catch(NumberFormatException nfe)
			{
				logger.error("could not parse " + num);
			}
			
		}
		
			MagicCard mc = getCardById(id);
					  mc.getEditions().add(me);
					  return mc;
		
		
	}


	@Override
	public String getVersion() {
		return version;
	}

}
