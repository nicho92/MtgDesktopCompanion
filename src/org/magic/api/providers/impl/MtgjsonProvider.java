package org.magic.api.providers.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardNames;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat;
import org.magic.api.beans.MagicRuling;
import org.magic.api.interfaces.MagicCardsProvider;

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

	private String urlSetJsonZip = "http://mtgjson.com/json/AllSets-x.json.zip";
	private String urlVersion = "http://mtgjson.com/json/version.json";
	
	private File fileSetJsonTemp = new File(System.getProperty("user.home")+"/magicDeskCompanion/AllSets-x.json.zip");
	private File fileSetJson = new File(System.getProperty("user.home")+"/magicDeskCompanion/AllSets-x.json");
	private File fversion = new File(System.getProperty("user.home")+"/magicDeskCompanion/version");
	
	private List<MagicCard> list;
	private ReadContext ctx;
	private Map<String,List<MagicCard>> cacheCard;
	List<String> currentSet;
	
	
	private List<MagicEdition> eds;
	private String version;
	private boolean enable;
	
	
	static final Logger logger = LogManager.getLogger(MtgjsonProvider.class.getName());

	
	@Override
	public void enable(boolean enabled) {
		this.enable=enabled;
		
	}

	@Override
	public boolean isEnable() {
		return enable;
	}
	
	public MtgjsonProvider() {
		init();
	}
	
	
	private InputStream getStreamFromUrl(URL u) throws IOException
	{
	  	URLConnection connection = u.openConnection();
	  	connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
	  	connection.connect();
	  	
	  	return connection.getInputStream();
	}
	
	
	public void unZipIt(File zipFile){

	     byte[] buffer = new byte[1024];
	    	
	     try{
	     	ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
	    	ZipEntry ze = zis.getNextEntry();
	    		
	    	while(ze!=null){
	           logger.info(this + " unzip : "+ fileSetJson.getAbsoluteFile());
	            FileOutputStream fos = new FileOutputStream(fileSetJson);             
	            int len;
	            while ((len = zis.read(buffer)) > 0) {
	       		fos.write(buffer, 0, len);
	            }
	        		
	            fos.close();   
	            ze = zis.getNextEntry();
	    	}
	    	
	        zis.closeEntry();
	    	zis.close();
	    	fileSetJsonTemp.delete();
	    }catch(IOException ex){
	     logger.error(ex);
	    }
	   }    
	
	private boolean hasNewVersion()
	{
		String temp ="";
		try{
			
			temp = new BufferedReader(new FileReader(fversion)).readLine();
	  	  	logger.info("check new version of " + toString() +" ("+temp+")");
	  	
		InputStreamReader fr = new InputStreamReader( getStreamFromUrl(new URL(urlVersion)),"ISO-8859-1");
  	  	BufferedReader br = new BufferedReader(fr);
  	  	version =  br.readLine();
  	  	
  	  
  	  	
  	  	br.close();
  	  	if(!version.equals(temp))
  	  		return true;
  	 
  	  	logger.info("check new version of " + this + ": up to date ("+version+")");
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
		logger.info("init " + this +" ");
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
		  	  	
			if(!fileSetJson.exists())
			{
				logger.info("datafile does not exist. Downloading it");
				FileUtils.copyInputStreamToFile(getStreamFromUrl(new URL(urlSetJsonZip)), fileSetJsonTemp);
				unZipIt(fileSetJsonTemp);
				FileUtils.copyInputStreamToFile(getStreamFromUrl(new URL(urlVersion)), fversion);
			}
			
			
			if(hasNewVersion())
			{
				logger.info("new version datafile exist. Downloading it");
				FileUtils.copyInputStreamToFile(getStreamFromUrl(new URL(urlSetJsonZip)), fileSetJsonTemp);
				unZipIt(fileSetJsonTemp);
				FileUtils.copyInputStreamToFile(getStreamFromUrl(new URL(urlVersion)), fversion);
			}
			
			
		 
		 cacheCard= new HashMap<String,List<MagicCard>>();
		 logger.info("init " + this +" : parsing db file");
		 ctx = JsonPath.parse(fileSetJson);
		 logger.info("init " + this +" : OK");
		} 
		catch (Exception e1) {
			logger.error(e1);
		}
	}
	
	public MagicCard getCardById(String id) throws Exception {
		return searchCardByCriteria("id", id,null).get(0);
	}
	
	public List<MagicCard> searchCardByCriteria(String att,String crit,MagicEdition ed) throws IOException{
		
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
		
		if(att.equalsIgnoreCase("foreignNames"))
		{
			jsquery="$..cards[*]."+att+"[?(@.name =~ /^.*"+crit+".*$/i)]";
		}
		
		if(ed !=null)
		{
			jsquery="$."+ed.getId()+".cards[?(@."+att+" =~ /^.*"+crit.replaceAll("\\+", " " )+".*$/i)]";
		}
				
		
		return search(jsquery,att,crit);
	}
	
	private List<MagicCard> search(String jsquery,String att,String crit) throws IOException {
		

		currentSet=new ArrayList<String>();
		list= new ArrayList<MagicCard>();

		logger.info("searchCardByCriteria : " + jsquery);
	
		List<Map<String,Object>> cardsElement = ctx.withListeners(new EvaluationListener() {
			public EvaluationContinuation resultFound(FoundResult fr) {
				
				if(fr.path().startsWith("$"))
				{
					//logger.info(fr.path());
					currentSet.add(fr.path().substring(fr.path().indexOf("$[")+3, fr.path().indexOf("]")-1));
				}
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
	 		   
	 		   if(map.get("originalText")!=null)
	 			 mc.setOriginalText(String.valueOf(map.get("originalText")));
	 		   
	 		   if(map.get("originalType")!=null)
	 			 mc.setOriginalType(String.valueOf(map.get("originalType")));
	 		   
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
	 		  
	 		  /* if(map.get("number")==null)
	 		   {
	 			   if(map.get("mciNumber")!=null)
	 			   {
	 				   String mciN = String.valueOf(map.get("mciNumber"));
	 				   if(mciN.lastIndexOf("/")>-1)
	 					   mc.setNumber(mciN.substring(mciN.lastIndexOf("/")+1));
	 				   else
	 					  mc.setNumber(mciN);
	 			   }
	 		   }*/
	 		  
	 		   
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
	 			   if(!me.getRarity().equals("Basic Land"))//too much elements, so, remove all re-printings information
	 			   {   
	 				   if(map.get("printings")!=null)
	 				   for(String print : (List<String>)map.get("printings"))
		 			   {
		 				   if(!print.equalsIgnoreCase(codeEd))
		 				   {
		 					  MagicEdition meO = getSetById(print);
			 			      if(mc.getMultiverseid()==null)
			 			    	meO.setMultiverse_id(String.valueOf(0));
		 					  else
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
		else //TODO : check
			jsquery="$..(@."+att+" =~ /^.*"+crit.replaceAll("\\+", " " )+".*$/i)]";
		
		logger.info("get edition with " + att +"="+crit);
		
		if(eds!=null)
		{
			logger.info("editions already loaded. return cache");
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
					
					/*try
					{
						me.setRarity(ctx.read("$."+id+".rarity",String.class));
					}
					catch(Exception e)
					{
						logger.error(id + " rarity not found " + e); 
					}*/
					
					if(me.getCardCount()==0)
						me.setCardCount(ctx.read("$."+id+".cards", List.class).size());//long !
					
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
					{}
					
					try
					{
						me.setBlock(ctx.read("$."+id+".block",String.class));
					}
					catch(PathNotFoundException e)
					{}
					
					try
					{
					me.setTranslations(ctx.read("$."+id+".translations",Map.class));
					}
					catch(PathNotFoundException e)
					{}
	
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
		return new String[]{"name","foreignNames","text","artist","type","rarity","flavor","cmc","set","watermark","power","toughness","layout"};
	}

	public String toString() {
		return "MTG Json Provider";
	}

	public String[]  getLanguages() {
		return new String[]{"English","Chinese Simplified","Chinese Traditional","French","German","Italian","Japanese","Korean","Portugese","Russian","Spanish"};
	}

	
	//TODO : reforge this function
	private void initOtherEditionCardsVar(MagicCard mc,MagicEdition me)
	{
		String edCode=me.getId();
		
		if(!edCode.startsWith("p"))
			edCode=edCode.toUpperCase();
		
		String jsquery="$."+edCode+".cards[?(@.name=~ /^.*"+mc.getName()+".*$/i)]";
		logger.debug("initOtherEditionVars" + jsquery);
		
		List<Map<String,Object>> cardsElement = null;
		try{
			cardsElement = ctx.read(jsquery,List.class);
		}
		catch(Exception e)
		{
			logger.error(e);
		}
		
		
		if(cardsElement!=null)
			for(Map<String,Object> map : cardsElement)
			{
				try {
					me.setRarity(String.valueOf(map.get("rarity")));
				} catch (Exception e) {
					logger.error("initOtherEditionCardsVar rarity not found");
					me.setRarity(mc.getRarity());
				}
				
				try {
					me.setNumber(String.valueOf(map.get("number")));
				}
				catch(Exception e)
				{
					logger.error("initOtherEditionCardsVar number not found");
					me.setNumber(mc.getNumber());
				}
				
				
				try {
		 			   me.setMultiverse_id(String.valueOf((int)(double)map.get("multiverseid")));
				}catch(Exception e)
				{
					logger.debug("multiverseNotFound " + e);
					me.setMultiverse_id(String.valueOf(mc.getMultiverseid()));
				}
			}
	}
	
	public List<MagicCard> openBooster(MagicEdition me) {

		logger.info("opening booster for " + me );
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
						   		initOtherEditionCardsVar(mc, edition);
						   		
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
					   		initOtherEditionCardsVar(mc, edition);
					   		
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
					   		initOtherEditionCardsVar(mc, edition);
					   		
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
	
	public MagicCard getCardByNumber(String num, MagicEdition me) throws Exception {
		String jsquery="$."+me.getId().toUpperCase()+".cards[?(@.number == '"+num+"')]";
		logger.debug("search " +jsquery);
			try{
					MagicCard mc = search(jsquery, "number", num).get(0);//getCardById(id);
					  //me.setNumber(String.valueOf(parseId-1));
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



}
