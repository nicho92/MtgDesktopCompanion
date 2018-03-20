package org.magic.api.providers.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.Booster;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.MagicRuling;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.spi.cache.CacheProvider;
import com.jayway.jsonpath.spi.cache.LRUCache;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;


public class MtgjsonProvider extends AbstractCardsProvider{
	
	private File fileSetJsonTemp = new File(confdir,"AllSets-x.json.zip");
	private File fileSetJson = new File(confdir,"AllSets-x.json");
	private File fversion = new File(confdir,"version");
	
	private ReadContext ctx;
	private Map<String,List<MagicCard>> cachedCardEds;
	List<String> currentSet;
	
	
	private List<MagicEdition> eds;
	private String version;
	
	public MtgjsonProvider() {
		super();
		CacheProvider.setCache(new LRUCache(200));
	
		init();
		
		
	}
	
	private InputStream getStreamFromUrl(URL u) throws IOException
	{
	  	URLConnection connection = u.openConnection();
	  	connection.setRequestProperty("User-Agent", getString("USER_AGENT"));
	  	connection.connect();
	  	
	  	return connection.getInputStream();
	}
	
	private void unZipIt(){

	     byte[] buffer = new byte[1024];
	    	
	     try(ZipInputStream zis = new ZipInputStream(new FileInputStream(fileSetJsonTemp)))
	     {
	    	 
	    	ZipEntry ze = zis.getNextEntry();
	    		
	    	while(ze!=null){
	           logger.info(this + " unzip : "+ fileSetJson.getAbsoluteFile());
	           
	           try(FileOutputStream fos = new FileOutputStream(fileSetJson))
	           {
	        	    int len;
	        	    while ((len = zis.read(buffer)) > 0) {
		            	fos.write(buffer, 0, len);
		            }
		            ze = zis.getNextEntry();
	           }
	    	}
	    }catch(IOException ex){
	     logger.error(ex);
	    }
	     
	    	boolean del = FileUtils.deleteQuietly(fileSetJsonTemp);
	    	logger.debug("remove " + fileSetJsonTemp + "=" + del);
 
	   }    
	
	private boolean hasNewVersion()
	{
		String temp ="";
		try{
			try(BufferedReader br=new BufferedReader(new FileReader(fversion)))
			{
				temp =br.readLine();
			}
			
	  	  	logger.info("check new version of " + toString() +" ("+temp+")");
	  	
		InputStreamReader fr = new InputStreamReader( getStreamFromUrl(new URL(getString("URL_VERSION"))),"ISO-8859-1");
  	  	BufferedReader br = new BufferedReader(fr);
  	  	version =  br.readLine();
 
  	  	br.close();
  	  	if(!version.equals(temp))
  	  	{
  	  		logger.info("new version datafile exist ("+version+"). Downloading it");
  	  		return true;
  	  	}
  	 
  	  	logger.info("check new version of " + this + ": up to date");
  	  	return false;
		}
		catch(Exception e)
		{
			version =temp;
			logger.error("Error getting last version " +e);
			return false;
		}
		
		
	}
	
	public void init()
	{
		logger.info("init " + this);
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
			
		  	logger.debug("loading file " + fileSetJson);
		  	  	
			if(!fileSetJson.exists()|| fileSetJson.length()==0)
			{
				logger.info("datafile does not exist. Downloading it");
				FileUtils.copyInputStreamToFile(getStreamFromUrl(new URL(getString("URL_SET_JSON_ZIP"))), fileSetJsonTemp);
				unZipIt();
				FileUtils.copyInputStreamToFile(getStreamFromUrl(new URL(getString("URL_VERSION"))), fversion);
			}
			
			
			if(hasNewVersion())
			{
				FileUtils.copyInputStreamToFile(getStreamFromUrl(new URL(getString("URL_SET_JSON_ZIP"))), fileSetJsonTemp);
				unZipIt();
				FileUtils.copyInputStreamToFile(getStreamFromUrl(new URL(getString("URL_VERSION"))), fversion);
			}
		 
		 cachedCardEds= new HashMap<>();
		 logger.info(this +" : parsing db file");
		 long time1 = System.currentTimeMillis();
		 ctx = JsonPath.parse(fileSetJson);
		 long time2 = System.currentTimeMillis();
		 
		 logger.info(this +" : parsing OK (" + (time2-time1)/1000 + " s)");
		} 
		catch (Exception e1) {
			logger.error(e1);
		}
	}
	
	public MagicCard getCardById(String id) throws IOException {
		return searchCardByCriteria("id", id,null,true).get(0);
	}
	
	public List<MagicCard> searchCardByCriteria(String att,String crit,MagicEdition ed,boolean exact) throws IOException{
		
		String filterEdition=".";
		
		if(ed !=null)
			filterEdition=filterEdition+ed.getId();
		
		
		String jsquery="$"+filterEdition+".cards[?(@."+att+" =~ /^.*"+crit.replaceAll("\\+", " " )+".*$/i)]";
		
		if(exact)
			jsquery="$"+filterEdition+".cards[?(@."+att+" == \""+crit.replaceAll("\\+", " " )+"\")]";
		
		if(att.equalsIgnoreCase("set"))
		{
			if(cachedCardEds.get(crit)!=null)
			{
				logger.debug(crit + " is already in cache. Loading from it");
				return cachedCardEds.get(crit);
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
			jsquery="$"+filterEdition+".cards[?(@."+att+" == "+crit+")]";
		}
		if(att.equalsIgnoreCase("foreignNames")) //TODO : find the good formula
		{
			jsquery="$"+filterEdition+".cards[foreignNames][?(@.name =~ /^.*"+crit+".*$/i)]";
		}
		return search(jsquery,att,crit);
	}
	
	
	
	public List<MagicCard> search(String jsquery,String att,String crit) {
		
		currentSet=new ArrayList<>();
		List<MagicCard> listCards= new ArrayList<>();

		logger.debug("searchCardByCriteria : " + jsquery);
	
		List<Map<String,Object>> cardsElement = ctx.withListeners(fr->{
				if(fr.path().startsWith("$"))
				{
					currentSet.add(fr.path().substring(fr.path().indexOf("$[")+3, fr.path().indexOf("]")-1));
				}
				return null;
		}).read(jsquery,List.class);

		
		
		int indexSet=0;
		for(Map<String,Object> map : cardsElement)
		{
		   MagicCard mc = new MagicCard();
  		   if(map.get("name")!=null)
			   mc.setName(map.get("name").toString());
	 		   mc.setFlippable(false);
	 		   mc.setTranformable(false);
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
	 		   
	 		  
	 		  if(map.get("mciNumber")!=null)
	 			 mc.setMciNumber(String.valueOf(map.get("mciNumber")));
	 		    
	 		  
	 		   if(map.get("originalText")!=null)
	 			 mc.setOriginalText(String.valueOf(map.get("originalText")));
	 		   
	 		   if(map.get("originalType")!=null)
	 			 mc.setOriginalType(String.valueOf(map.get("originalType")));
	 		   
	 		  if(map.get("supertypes")!=null)
	 			   mc.getSupertypes().addAll((List<String>)map.get("supertypes"));
	 		   
	 		   if(map.get("types")!=null)
	 			  mc.getTypes().addAll((List<String>)map.get("types"));
	 		   
	 		   if(map.get("subtypes")!=null)
	 			   mc.getSubtypes().addAll((List<String>)map.get("subtypes"));
	 		  
	 		   if(map.get("variations")!=null)
	 			  mc.getVariations().addAll((List<Integer>)map.get("variations"));
	 		  
	 		   if(map.get("colors")!=null)
	 			   mc.getColors().addAll((List<String>)map.get("colors"));
	 		   
	 		   if(map.get("colorIdentity")!=null)
	 			   mc.getColorIdentity().addAll((List<String>)map.get("colorIdentity"));
	 		  
	 		   if(map.get("watermark")!=null)
	 			  mc.setWatermarks(String.valueOf(map.get("watermark")));
	 		   
	 		   if(map.get("number")!=null)
	 			  mc.setNumber(String.valueOf(map.get("number")));
	 		  
	 		   if(map.get("gathererCode")!=null)
	 			  mc.setGathererCode(String.valueOf(map.get("gathererCode"))); 
	 		   
	 		  if(map.get("reserved")!=null)
	 			  mc.setReserved(Boolean.valueOf(String.valueOf(map.get("reserved")))); 
	 		   
	 			  
	 		  if(map.get("loyalty")!=null)
	 		  {
	 			 try{
	 				 mc.setLoyalty((int)Double.parseDouble(map.get("loyalty").toString()));
	 			 }
	 			 catch(Exception e)
	 			 {
	 				 mc.setLoyalty(0);
	 			 }
	 		  }
	 		   
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
	 			
 				if(mc.getMultiverseid()!=null)			
 					me.setMultiverse_id(String.valueOf(mc.getMultiverseid()));			
 							
	 			mc.getEditions().add(me);
	 			
	 			   
	 			 
		 		   if(me.getRarity().equals("Basic Land")) //mtgjson give null text for basic Land. Modify it for adding mana color
		 		   {
		 			   mc.setText(mc.getOriginalText());
		 			   switch(mc.getName())
		 		   		{
		 		   			case "Plains" : mc.setText("{T} : Add {W} to your mana pool");break;
		 		   			case "Island" : mc.setText("{T} : Add {U} to your mana pool");break;
		 		   			case "Swamp" : mc.setText("{T} : Add {B} to your mana pool");break;
		 		   			case "Mountain" : mc.setText("{T} : Add {R} to your mana pool");break;
		 		   			case "Forest" : mc.setText("{T} : Add {G} to your mana pool");break;
		 		   			default : break;
		 		   		}
		 		   }
	 			
	 			  /*get other sets*/
	 			   if(!me.getRarity().equals("Basic Land") && map.get("printings")!=null)//too much elements, so, remove all re-printings information for basic lands
	 			   {   
		 				   for(String print : (List<String>)map.get("printings"))
			 			   {
			 				   if(!print.equalsIgnoreCase(codeEd))
			 				   {
			 					  MagicEdition meO = getSetById(print);
				 			      if(mc.getMultiverseid()==null)
				 			    	meO.setMultiverse_id(String.valueOf(0));
			 				  	
				 			      initOtherEditionCardsVar(mc, meO);
				 			    
				 			      mc.getEditions().add(meO);
				 			   }
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
	 		   
	 		  if(map.get("names")!=null)
	 		   {
				 ((List)map.get("names")).remove(mc.getName());
				 String rotateName=((List)map.get("names")).get( ((List)map.get("names")).size()-1).toString() ;
	 			 mc.setRotatedCardName(rotateName);
	 			 
	 			 if(mc.getLayout().equals("flip"))
	 				 mc.setFlippable(true);
	 			 
	 		   }
	 		   
	 		 if(mc.getLayout().equals("double-faced") || mc.getLayout().equals("meld") )
 				 mc.setTranformable(true);
 		 
	 		  
	 		  setChanged();
	 		  notifyObservers(mc);
	 		   
	 		  listCards.add(mc);
	 		  
		}
		currentSet.clear();
		
		if(att.equalsIgnoreCase("set"))
			cachedCardEds.put(crit, listCards);
		
		return listCards;
		
	}

	public List<MagicEdition> loadEditions() throws IOException {
		
		String jsquery="$.*";
		
		logger.debug("load editions");
		
		if(eds!=null)
		{
			logger.debug("editions already loaded. return cache");
			return eds;
		}
		
		final List<String> codeEd=new ArrayList<>();
		ctx.withListeners(fr-> {
					
					if(fr.path().startsWith("$"))
						codeEd.add(fr.path().substring(fr.path().indexOf("$[")+3, fr.path().indexOf("]")-1));
					return null;
					
			}).read(jsquery,List.class);
		
		eds = new ArrayList<>();
		for(String codeedition : codeEd)
		{
			eds.add(getSetById(codeedition));
		}
		
		return eds;
		
	}
	
	public MagicEdition getSetById(String id)  {
			MagicEdition me = new MagicEdition();
			if(!id.substring(0, 1).equals("p"))
				id=id.toUpperCase();

					me.setId(id);
					try {
					me.setSet(ctx.read("$."+id+".name",String.class));
					me.setReleaseDate(ctx.read("$."+id+".releaseDate",String.class));
					me.setBorder(ctx.read("$."+id+".border",String.class));
					me.setType(ctx.read("$."+id+".type",String.class));
					
					if(me.getCardCount()==0)
						me.setCardCount(ctx.read("$."+id+".cards", List.class).size());//long !
					
					}
					catch(PathNotFoundException pnfe )
					{
						me.setSet(id);
						me.setReleaseDate("");
						me.setBorder("");
						me.setType("unknow");
						me.setCardCount(0);
					}
					
					
					try{
						me.setMagicCardsInfoCode(ctx.read("$."+id+".magicCardsInfoCode",String.class));
					}
					catch(Exception e)
					{
						MTGLogger.printStackTrace(e);
					}
					
					try{
						me.setMkm_id(ctx.read("$."+id+".mkm_id",Integer.class));
						me.setMkm_name(ctx.read("$."+id+".mkm_name",String.class));
					}
					catch(Exception e)
					{
						MTGLogger.printStackTrace(e);
					}
					
					try{
						me.setOnlineOnly(ctx.read("$."+id+".onlineOnly",Boolean.class));
					}
					catch(Exception e)
					{
						me.setOnlineOnly(false);
					}
					
					
					try
					{
						me.setBooster(ctx.read("$."+id+".booster",List.class));
					}
					catch(PathNotFoundException e)
					{
						MTGLogger.printStackTrace(e);
					}
					
					try
					{
						me.setBlock(ctx.read("$."+id+".block",String.class));
					}
					catch(PathNotFoundException e)
					{
						MTGLogger.printStackTrace(e);
					}
					
					try
					{
					me.setTranslations(ctx.read("$."+id+".translations",Map.class));
					}
					catch(PathNotFoundException e)
					{
						MTGLogger.printStackTrace(e);
					}
		return me;
		
	}

	public String[] getQueryableAttributs() {
		return new String[]{"name","foreignNames","text","artist","type","rarity","flavor","cmc","set","watermark","power","toughness","layout","reserved"/*,"format"*/};
	}

	public String getName() {
		return "MTG Json Provider";
	}

	public String[]  getLanguages() {
		return new String[]{"English","Chinese Simplified","Chinese Traditional","French","German","Italian","Japanese","Korean","Portugese","Russian","Spanish"};
	}
	
	private void initOtherEditionCardsVar(MagicCard mc,MagicEdition me)
	{
		String edCode=me.getId();
		
		if(!edCode.startsWith("p"))
			edCode=edCode.toUpperCase();
		
		String jsquery="";
			jsquery = "$."+edCode+".cards[?(@.name==\""+mc.getName().replaceAll("\\+", " " ).replaceAll("\"", "\\\\\"")+"\")]";
		
		//logger.trace("initOtherEditionVars for " + mc +"("+mc.getEditions().get(0)+") -> " + jsquery);--> error on loading booster
		
		List<Map<String,Object>> cardsElement = null;
		try{
			cardsElement = ctx.read(jsquery,List.class);
		}
		catch(Exception e)
		{
			logger.error("error in " + jsquery,e);
		}
		
		
		if(cardsElement!=null)
			for(Map<String,Object> map : cardsElement)
			{
				try {
					me.setRarity(String.valueOf(map.get("rarity")));
				} catch (Exception e) {
					me.setRarity(mc.getRarity());
				}
				
				try {
					
					me.setNumber(String.valueOf(map.get("number")));
				}
				catch(Exception e)
				{
					logger.trace("initOtherEditionCardsVar number not found");
				}
				
				
				try {
					me.setMkm_id(Integer.parseInt(String.valueOf(map.get("mkm_id"))));
					me.setMkm_name(String.valueOf(map.get("mkm_name")));
				}
				catch(Exception e)
				{
					logger.trace("initOtherEditionCardsVar mkm_id not found");
				}
				
				
				try {
		 			   me.setMultiverse_id(String.valueOf((int)(double)map.get("multiverseid")));
				}catch(Exception e)
				{
					logger.trace("multiverseNotFound for " + me);
				}
			}
	}
	
	public Booster generateBooster(MagicEdition me) {

		logger.debug("opening booster for " + me );
		List<MagicCard> common = new ArrayList<>();
		List<MagicCard> uncommon = new ArrayList<>();
		List<MagicCard> rare= new ArrayList<>();
		List<MagicCard> lands = new ArrayList<>();
		
		Booster b = new Booster();
		
		
			try {
				if(cachedCardEds.get(me.getId())==null)
					cachedCardEds.put(me.getId(), searchCardByCriteria("set", me.getId(), null,true));
				
				
				for(MagicCard mc : cachedCardEds.get(me.getId()))
				{	
					if(mc.getEditions().get(0).getRarity().equalsIgnoreCase("common"))
							common.add(mc);
					
					if(mc.getEditions().get(0).getRarity().equalsIgnoreCase("uncommon"))
							uncommon.add(mc);
					
					if(mc.getEditions().get(0).getRarity().toLowerCase().contains("rare"))
						rare.add(mc);
	
					if(mc.getSupertypes().toString().toLowerCase().contains("basic") && mc.getTypes().toString().toLowerCase().contains("land"))
						lands.add(mc);
				
				}
				Collections.shuffle(lands);
				Collections.shuffle(common);		   
				Collections.shuffle(uncommon);
				Collections.shuffle(rare);
			}
			catch(Exception e)
			{
				logger.error("Error opening booster",e);
			}
			
			List<MagicCard> resList = new ArrayList<>();
			resList.addAll(common.subList(0, 10));
			resList.addAll(uncommon.subList(0, 4));
			resList.add(rare.get(0));
			
			if(!lands.isEmpty())
				resList.addAll(lands.subList(0, 1));
			
			b.setCards(resList);
			b.setEdition(me);
			
		return b;
	}
	
	public MagicCard getCardByNumber(String num, MagicEdition me) throws IOException {
		String jsquery="$."+me.getId().toUpperCase()+".cards[?(@.number == '"+num+"')]";
		logger.debug("search " +jsquery);
			try{
					MagicCard mc = search(jsquery, "number", num).get(0);
					  mc.getEditions().add(me);
					  return mc;
			}
			catch(Exception e)
			{
				logger.error(e);
				return null;
			}
		
		
	}

	public String getVersion() {
		return version;
	}
	
	@Override
	public URL getWebSite() throws MalformedURLException {
		return new URL("http://mtgjson.com/");
	}

	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}

	@Override
	public void initDefault() {
		setProperty("URL_SET_JSON_ZIP", "http://mtgjson.com/json/AllSets-x.json.zip");
		setProperty("URL_VERSION", "http://mtgjson.com/json/version.json");
		setProperty("USER_AGENT", MTGConstants.USER_AGENT);
		
	}
}
