package org.magic.services.providers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.api.mkm.tools.MkmConstants;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.abstracts.extra.AbstractMTGJsonProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.magic.services.network.URLTools;
import org.magic.services.providers.MTGJsonPricer.STOCK;
import org.magic.services.providers.MTGJsonPricer.SUPPORT;
import org.magic.services.providers.MTGJsonPricer.VENDOR;
import org.magic.tools.Chrono;
import org.magic.tools.FileTools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;


public class MTGJsonPricer {
	public enum SUPPORT {PAPER,MTGO}
	public enum STOCK {RETAIL, BUYLIST}
	public enum VENDOR {CARDKINGDOM,TCGPLAYER,CARDHOARDER,CARDMARKET}
	private List<Data> caches;
	private static MTGJsonPricer inst;
	
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	private File dataFile = new File(MTGConstants.DATA_DIR,"AllPrices.json");
	private Gson gson;
	private Integer expireday=5;
	
	
	public MTGJsonPricer() throws IOException {
	
		gson = new GsonBuilder().setPrettyPrinting().create();
		
		
		if(!dataFile.exists())
			downloadDataFile();
	}
	
	public static MTGJsonPricer getInstance() throws IOException
	{
		if(inst==null)
			inst = new MTGJsonPricer();
		
		return inst;
	}
	
	
	public Meta getVersion()
	{
		try(var reader = new JsonReader(new FileReader(dataFile)))
		{
				reader.beginObject();
				reader.nextName();
				return gson.fromJson(reader, Meta.class);
		} catch (Exception e) {
			logger.error(e);
			return null;
		} 
	}
	
	public void downloadDataFile() throws IOException
	{
		var tmp = new File(MTGConstants.DATA_DIR,"AllPrices.json.zip");
		URLTools.download(AbstractMTGJsonProvider.MTG_JSON_ALL_PRICES_ZIP, tmp);
		FileTools.unZipIt(tmp,dataFile);
	}
		
	public void buildPrices(VENDOR v) throws IOException {
		var c = new Chrono();
		c.start();
		JsonArray arr = new JsonArray();
		try(var reader = new JsonReader(new FileReader(dataFile)))
		{
				
				logger.info("Begin caching datas ");
				reader.beginObject();
				reader.nextName();
				Meta m = new Gson().fromJson(reader, Meta.class);
				logger.debug(m);
				reader.nextName();//data
				reader.beginObject(); 
				while(reader.hasNext())
				{	
					var data = new Data();
					data.setMeta(m);
					data.setMtgjsonId(reader.nextName());
					reader.beginObject();
					String vendor = null;
					STOCK stock = null;
					SUPPORT support = null;
					
					while(reader.hasNext())
					{
						support = SUPPORT.valueOf(reader.nextName().toUpperCase());
						reader.beginObject();
						
						while(reader.hasNext())
						{
							vendor = reader.nextName();
							reader.beginObject();
							
							while(reader.hasNext())
							{
								if(reader.peek()==JsonToken.NAME)
								{
									String val = reader.nextName();
									if(val.equals("currency"))
									{
										reader.nextString();
									}
									else {
										stock = STOCK.valueOf(val.toUpperCase());	
									}
								}
								else
								{
									var p = new PriceEntry();
											p.setVendor(VENDOR.valueOf(vendor.toUpperCase()));
											p.setSupport(support);
											p.setStock(stock);
											p.setCurrency(getCurrencyFor(p.getVendor())); 
									
									
									reader.beginObject();
									while(reader.hasNext())
									{
										p.setFoil(reader.nextName().equalsIgnoreCase("foil"));
										reader.beginObject();
										while(reader.hasNext())
										{
											p.getStockPrices().put(reader.nextName(), reader.nextDouble());
										} // fin boucle map date/prix
										reader.endObject();
									}//fin boucle Foil/Normal
									
									if(p.getVendor()==v)
									{
										data.getPrices().add(p);
										arr.add(gson.toJsonTree(data));
									}
									reader.endObject();
								}
							}//buylist/retail/Currency
							reader.endObject();	
						}//retailer
						reader.endObject();	
					}//mtgjsonIds
					reader.endObject();
				}//fin boucle data
				
			}
			writeFile(arr,v);
			logger.info("Ending buildings datas " + c.stop() +"s");
			
	}
	
	private void writeFile(JsonArray arr,VENDOR v) {
			try {
				FileTools.saveFile(new File(MTGConstants.DATA_DIR.getAbsolutePath(),v.name()+".json"),arr.toString());
			} catch (IOException e) {
				logger.error(e);
			}
	}
	
	public List<Data> loadData(VENDOR v) throws IOException
	{
		File f = new File(MTGConstants.DATA_DIR.getAbsolutePath(),v.name()+".json");
		
		
		if(caches==null)
			caches = new ArrayList<>();
		
		
		if(f.exists())
		{
			int lastModif = FileTools.daysBetween(f);
			
			if(lastModif>expireday)
			{
				downloadDataFile();
				logger.info(f.getAbsolutePath() + " is older of " + expireday + " days. Will be updated");
				FileTools.deleteFile(f);
			}
			
		}
		
		
		
		if(caches.isEmpty())
		{
			if(!f.exists())
			{
				logger.error(f.getAbsolutePath() + " doesn't existe. running buildPrices("+v+")");
				buildPrices(v);
			}
			
			JsonArray el = JsonParser.parseReader(new FileReader(f)).getAsJsonArray();
			el.forEach(e->caches.add(gson.fromJson(e,Data.class)));
		}
		return caches;
	}
	
	private Currency getCurrencyFor(VENDOR v)
	{
		if(v== VENDOR.CARDMARKET)
			return Currency.getInstance("EUR");
		
		return Currency.getInstance("USD");
	}

	public List<MagicPrice> getPriceFor(MagicCard card) {
		List<MagicPrice> ret = new ArrayList<>();
		Data d;
		try {
			d = loadData(VENDOR.CARDMARKET).stream().filter(i->i.getMtgjsonId().equals(card.getId())).findFirst().orElse(null);
		} catch (Exception e) {
			logger.error(e);
			return ret;
		}
		if(d==null)
		{
			logger.debug("MTGJson found nothing");
			return ret;
		}
			
		
		for(Boolean b : new Boolean[] {true,false}) {
			try {
				var mp = new MagicPrice();
				mp.setCountry("None");
				mp.setCurrency("EUR");
				mp.setMagicCard(card);
				mp.setSeller("Mkm");
				mp.setSite("Mkm");
				mp.setUrl(MkmConstants.MKM_SITE_URL);
				mp.setFoil(b);
				mp.setValue(d.listPricesByFoil(b).get(0).getStockPrices().lastEntry().getValue());
				ret.add(mp);
			}
			catch(IndexOutOfBoundsException e)
			{
				//do nothing
			}
		}
		
		logger.debug("MTGJson found " + ret.size() + " prices");
		return ret;
	}

	public void expirationDay(Integer maxday) {
		if(maxday!=null)
			this.expireday=maxday;
		
	}
}

class Meta
{
	private String date;
	private String version;
	
	@Override
	public String toString() {
		return getDate() + " " + getVersion();
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
} 


class PriceEntry
{
	private boolean foil;
	private NavigableMap<String,Double> stockPrices;
	private Currency currency;
	private STOCK stock;
	private VENDOR vendor;
	private SUPPORT support;
	
	public SUPPORT getSupport() {
		return support;
	}

	public void setSupport(SUPPORT support) {
		this.support = support;
	}

	public VENDOR getVendor() {
		return vendor;
	}

	public void setVendor(VENDOR v) {
		this.vendor = v;
	}

	public void setStock(STOCK stock) {
		this.stock = stock;
	}
	
	 public STOCK getStock() {
		return stock;
	}
	
	
	@Override
	public String toString() {
		var temp = new StringBuilder();
		temp.append(vendor).append(":")
			.append(support).append(" ")
			.append(stock).append(" " ).append(foil?"Foil":"Normal").append(" ")
			.append(currency).append(" ");
		return temp.toString();
	}
	
	public PriceEntry() {
		stockPrices = new TreeMap<>();
	}
	
	public boolean isFoil() {
		return foil;
	}
	public void setFoil(boolean foil) {
		this.foil = foil;
	}
	
	public NavigableMap<String, Double> getStockPrices() {
		return stockPrices;
	}
	
	public void setStockPrices(NavigableMap<String, Double> stockPrices) {
		this.stockPrices = stockPrices;
	}
	
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	
	public void setCurrency(String currency) {
		this.currency = Currency.getInstance(currency);
	}
	
	
	public Currency getCurrency() {
		return currency;
	}
}


class Data
{
	private Meta meta;
	private String mtgjsonId;
	private List<PriceEntry> prices;
	
	
	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public Data()
	{
		prices = new ArrayList<>();
	}
	
	public List<PriceEntry> getPrices() {
		return prices;
	}
	
	public List<PriceEntry> listPricesByVendor(VENDOR v)
	{
		return getPrices().stream().filter(p->p.getVendor()==v).toList();
	}
	
	public List<PriceEntry> listPricesBySupport(SUPPORT v)
	{
		return getPrices().stream().filter(p->p.getSupport()==v).toList();
	}
	
	public List<PriceEntry> listPricesByStock(STOCK v)
	{
		return getPrices().stream().filter(p->p.getStock()==v).toList();
	}
	
	public List<PriceEntry> listPricesByFoil(Boolean v)
	{
		return getPrices().stream().filter(p->p.isFoil()==v).toList();
	}
	
	@Override
	public String toString() {
		var temp = new StringBuilder();
		temp.append(mtgjsonId).append(":");
		
		for(PriceEntry p : getPrices())
			temp.append("\n\t").append(p);
			
		return temp.toString();
	}

	public String getMtgjsonId() {
		return mtgjsonId;
	}
	public void setMtgjsonId(String mtgjsonId) {
		this.mtgjsonId = mtgjsonId;
	}
	
}

