package org.beta;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.beta.MTGJsonPricer.STOCK;
import org.beta.MTGJsonPricer.SUPPORT;
import org.beta.MTGJsonPricer.VENDOR;
import org.magic.api.beans.HistoryPrice;
import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGJsonProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.magic.tools.Chrono;
import org.magic.tools.FileTools;
import org.magic.tools.IDGenerator;
import org.magic.tools.MTG;
import org.magic.tools.UITools;
import org.magic.tools.URLTools;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;


public class MTGJsonPricer {
	public enum SUPPORT {PAPER,MTGO}
	public enum STOCK {RETAIL, BUYLIST}
	public enum VENDOR {CARDKINGDOM,TCGPLAYER,CARDHOARDER,CARDMARKET}

	private static MTGJsonPricer inst;
	
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	private File dataFile = new File(MTGConstants.DATA_DIR,"AllPrices.json");
	private List<Data> caches; 
	
	public MTGJsonPricer() throws IOException {
		caches = new ArrayList<>();
		
		if(!dataFile.exists())
			downloadDataFile();
	}
	
	public static void main(String[] args) throws IOException {
		List<Data> datas = MTGJsonPricer.getInstance().getDatas();
		var exportD = new File("d:/datas.sql");
		var exportP = new File("d:/prices.sql");
		var temp = new StringBuilder();
		for(Data d : datas)
		{
			String jsonnId=d.getMtgjsonId();
			for(PriceEntry pe : d.getPrices())
			{
				
				String k = IDGenerator.generateMD5(d.getMtgjsonId()+pe.getCurrency()+pe.isFoil()+pe.getStock()+pe.getSupport()+pe.getVendor());
				
				temp.append("INSERT INTO data VALUES ('").append(k).append("','").append(jsonnId).append("','").append(pe.getCurrency()).append("',").append(pe.isFoil()?1:0).append(",'").append(pe.getStock()).append("','").append(pe.getSupport()).append("','").append(pe.getVendor()).append("');\n");
				FileTools.appendLine(exportD, temp.toString());
				temp.setLength(0);
				
//				for(Entry<Date, Double> e : pe.getStockPrices().entrySet())
//				{
//					temp.append("INSERT INTO prices VALUES ('").append(k).append("','").append(e.getKey()).append("','").append(e.getValue()).append("');\n");
//				}
//				
//				FileTools.appendLine(exportP, temp.toString());
				temp.setLength(0);
				
			}
			
			
		}
	}
	
	public HistoryPrice<MagicCard> toHistoryPrice(Data d, SUPPORT p, STOCK s, VENDOR v,boolean foil) throws IOException
	{
		var ret = new HistoryPrice<>(MTG.getEnabledPlugin(MTGCardsProvider.class).getCardById(d.getMtgjsonId()));
		PriceEntry priceEntries = d.listPricesByVendor(v).stream().filter(pe->pe.getStock()==s && pe.getSupport()==p && pe.isFoil()==foil).findFirst().orElse(new PriceEntry());
		
		ret.setCurrency(priceEntries.getCurrency());
		ret.setFoil(priceEntries.isFoil());
		ret.setSupport(p.name());
		priceEntries.getStockPrices().forEach(ret::put);
		return ret;
	}
	
	public static MTGJsonPricer getInstance() throws IOException
	{
		if(inst==null)
			inst = new MTGJsonPricer();
		
		return inst;
	}
	
	public List<Data> getDatas() throws IOException
	{
		if(caches.isEmpty())
			buildPrices();
		
		return caches;
		
	}
	
	
	public Data getDataFor(String mtgjsonId) throws IOException
	{
		return getDatas().stream().filter(e->e.getMtgjsonId().equals(mtgjsonId)).findFirst().orElse(new Data());
	}
	
	
	public Meta getVersion()
	{
		try(var reader = new JsonReader(new FileReader(dataFile)))
		{
				reader.beginObject();
				reader.nextName();
				return new Gson().fromJson(reader, Meta.class);
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
		
	public void buildPrices() throws IOException {
		var c = new Chrono();
		c.start();
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
										p.setFoil(reader.nextName().equals("foil"));
										reader.beginObject();
										while(reader.hasNext())
										{
											p.getStockPrices().put(UITools.parseDate(reader.nextName(),"yyyy-MM-dd"), reader.nextDouble());
										} // fin boucle map date/prix
										reader.endObject();
									}//fin boucle Foil/Normal
									
									data.getPrices().add(p);
									reader.endObject();
								}
							}//buylist/retail/Currency
							reader.endObject();	
						}//retailer
						reader.endObject();	
					}//mtgjsonIds
					
					caches.add(data);

					reader.endObject();
				}//fin boucle data
				
			}
			logger.info("Ending caching datas " + c.stop() +"s");
			
	}
	
	private Currency getCurrencyFor(VENDOR v)
	{
		if(v== VENDOR.CARDMARKET)
			return Currency.getInstance("EUR");
		
		return Currency.getInstance("USD");
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
	private Map<Date,Double> stockPrices;
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
		temp.append(vendor).append(" ").append(support).append(" ").append(stock).append(" " ).append(foil?"Foil":"Normal").append(" ").append(currency).append(" ").append(getStockPrices().size());
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
	
	public Map<Date, Double> getStockPrices() {
		return stockPrices;
	}
	
	public void setStockPrices(Map<Date, Double> stockPrices) {
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
		return getPrices().stream().filter(p->p.getVendor()==v).collect(Collectors.toList());
	}
	
	public List<PriceEntry> listPricesBySupport(SUPPORT v)
	{
		return getPrices().stream().filter(p->p.getSupport()==v).collect(Collectors.toList());
	}
	
	public List<PriceEntry> listPricesByStock(STOCK v)
	{
		return getPrices().stream().filter(p->p.getStock()==v).collect(Collectors.toList());
	}
	
	public List<PriceEntry> listPricesByFoil(Boolean v)
	{
		return getPrices().stream().filter(p->p.isFoil()==v).collect(Collectors.toList());
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

