package org.magic.services.providers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGPrice;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.abstracts.extra.AbstractMTGJsonProvider;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.network.URLTools;
import org.magic.services.providers.MTGJsonPricerProvider.STOCK;
import org.magic.services.providers.MTGJsonPricerProvider.SUPPORT;
import org.magic.services.providers.MTGJsonPricerProvider.VENDOR;
import org.magic.services.tools.Chrono;
import org.magic.services.tools.FileTools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;


public class MTGJsonPricerProvider {
	public enum SUPPORT {PAPER,MTGO}
	public enum STOCK {RETAIL, BUYLIST}
	public enum VENDOR {CARDKINGDOM,TCGPLAYER,CARDHOARDER,CARDMARKET,CARDSPHERE,MANAPOOL}
	
	private Map<VENDOR,JsonArray> caches;
	private static MTGJsonPricerProvider inst;

	protected Logger logger = MTGLogger.getLogger(this.getClass());
	private File dataFile = new File(MTGConstants.DATA_DIR,"AllPrices.json");
	private Gson gson;
	private Integer expireday=5;


	public MTGJsonPricerProvider() throws IOException {

		gson = new JsonExport().getEngine();
		caches = new EnumMap<>(VENDOR.class);
		

		if(!dataFile.exists())
			downloadDataFile();
	}

	public static MTGJsonPricerProvider getInstance() throws IOException
	{
		if(inst==null)
			inst = new MTGJsonPricerProvider();

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

	private void downloadDataFile() throws IOException
	{
		var tmp = new File(MTGConstants.DATA_DIR,"AllPrices.json.zip");
		URLTools.download(AbstractMTGJsonProvider.MTG_JSON_ALL_PRICES_ZIP, tmp);
		FileTools.unZipIt(tmp,dataFile);
	}

	
	private void buildPricesFile(VENDOR v) throws IOException {
		var c = new Chrono();
		c.start();
		JsonArray arr = new JsonArray();
		try(var reader = new JsonReader(new FileReader(dataFile)))
		{

				logger.info("Begin caching datas ");
				reader.beginObject();
				reader.nextName();
				gson.fromJson(reader, Meta.class);
				reader.nextName();//data
				reader.beginObject();
				while(reader.hasNext())
				{
					var data = new Data();
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
									reader.beginObject();
									while(reader.hasNext())
									{
										var p = new PriceEntry();
										p.setVendor(VENDOR.valueOf(vendor.toUpperCase()));
										p.setSupport(support);
										p.setStock(stock);
										p.setCurrency(getCurrencyFor(p.getVendor()));
										p.setFoil(reader.nextName().equalsIgnoreCase("foil"));
										reader.beginObject();
										while(reader.hasNext())
										{
											p.getStockPrices().put(reader.nextName(), reader.nextDouble());
										} // fin boucle map date/prix
										reader.endObject();
											
										if(p.getVendor()==v && !p.getStockPrices().isEmpty())
											data.getPrices().add(p);
										
									}//fin boucle Foil/Normal
									reader.endObject();
									
									if(vendor.toUpperCase().equals(v.name()) && !data.getPrices().isEmpty())
										arr.add(gson.toJsonTree(data));
									
								}
							}//buylist/retail/Currency
							reader.endObject();
						}//retailer
						reader.endObject();
					}//mtgjsonIds
					reader.endObject();
				}//fin boucle data

			}
		
			FileTools.saveFile(new File(MTGConstants.DATA_DIR.getAbsolutePath(),v.name()+".json"),arr.toString());
			logger.info("Ending buildings datas {}s",c.stop());
	}
	
	public JsonArray loadData(VENDOR v) throws IOException
	{
		File f = new File(MTGConstants.DATA_DIR.getAbsolutePath(),v.name()+".json");
		if(f.exists())
		{
			int lastModif = FileTools.daysBetween(f);

			if(lastModif>expireday)
			{
				downloadDataFile();
				logger.info("{} is older than {} days. Will be updated",f.getAbsolutePath(),expireday);
				FileTools.deleteFile(f);
			}
		}
		
		if(caches.get(v)==null)
		{
			logger.debug("Filling cache for {}", v);
			if(!f.exists())
			{
				logger.error("{} doesn't existe. running buildPrices({})",f.getAbsolutePath(),v);
				buildPricesFile(v);
			}
			try(var reader = new FileReader(f)) {
				caches.put(v, JsonParser.parseReader(reader).getAsJsonArray());
			}
			logger.debug("Filling cache for {} done", v);
		}
		return caches.get(v);
	}

	private Currency getCurrencyFor(VENDOR v)
	{
		if(v== VENDOR.CARDMARKET)
			return Currency.getInstance("EUR");

		return Currency.getInstance("USD");
	}
	
	public List<MTGPrice> getPriceFor(MTGCard card, VENDOR v) {
		
		logger.debug("getting price for {} with vendor={}}",card,v);
		
		var ret = new ArrayList<MTGPrice>();
		Data d;
		try {
			d = loadData(v).asList().stream()
					.filter(je->je.getAsJsonObject().get("mtgjsonId").getAsString().equals(card.getId()))
					.map(jo->gson.fromJson(jo, Data.class))
					.findFirst().orElse(null);
		} catch (Exception e) {
			logger.error(e);
			return ret;
		}
		
		if(d==null)
		{
			logger.warn("{} found nothing for {}",v,card);
			return ret;
		}
		
		for(Boolean b : new Boolean[] {true,false}) 
		{
			try {
				var mp = new MTGPrice();
				mp.setCountry("None");
				mp.setCurrency("EUR");
				mp.setCardData(card);
				mp.setSeller(v.name());
				mp.setSite(v.name());
				mp.setFoil(b);
				mp.setValue(d.listPricesByFoil(b).get(0).getStockPrices().lastEntry().getValue());
				ret.add(mp);
				logger.debug("Found {} Price={} Foil={}",card,mp.getValue(),mp.isFoil());
			}
			catch(Exception e)
			{
				logger.error(e);
				logger.error("No price found for {} with foil={}",card,b);
			}
		}

		logger.debug("{} found {} prices",v,ret.size());
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
		temp.append(vendor).append(":").append(support).append(" ").append(stock).append(" " ).append(foil?"Foil":"Normal").append(" ").append(currency).append(" : ").append(stockPrices.lastEntry().getValue());
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
	private String mtgjsonId;
	private List<PriceEntry> prices;

	public Data()
	{
		prices = new ArrayList<>();
	}

	public List<PriceEntry> getPrices() {
		return prices;
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

