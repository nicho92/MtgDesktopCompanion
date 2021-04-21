package org.beta;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.beta.MTGJsonPricer.STOCK;
import org.beta.MTGJsonPricer.SUPPORT;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class MTGJsonPricer {
	public enum SUPPORT {PAPER,MTGO}
	public enum STOCK {RETAIL, BUYLIST}
	
	
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	public static void main(String[] args) throws JsonParseException, IOException {
		File f = new File("C:\\Users\\Nicolas\\Google Drive\\test.json");
	//	File f = new File("D:\\Téléchargements\\AllPrices.json");
		MTGControler.getInstance();
		new MTGJsonPricer().buildPrice(f).forEach(System.out::println);
	}
	
	
	
	
	private List<Data> buildPrice(File f) throws IOException {
		
		List<Data> ret = new ArrayList<>();
		
		
		try(var reader = new JsonReader(new FileReader(f)))
		{

				reader.beginObject();
				reader.nextName();
				Meta m = new Gson().fromJson(reader, Meta.class);
				logger.debug(m);
				
				reader.nextName();//data
				reader.beginObject(); 
				
				while(reader.hasNext())
				{	
					var data = new Data();
					data.setMtgjsonId(reader.nextName());
					reader.beginObject();
					
					while(reader.hasNext())
					{
						data.setSupport(SUPPORT.valueOf(reader.nextName().toUpperCase()));
						reader.beginObject();
						
						while(reader.hasNext())
						{
							String retailer = reader.nextName();
							reader.beginObject();
							
							while(reader.hasNext())
							{
								var p = new Prices();
								p.setRetailer(retailer);
								String currency =null;
								STOCK stock  = null;
								if(reader.peek()==JsonToken.NAME)
								{
									String val = reader.nextName();
									if(val.equals("currency"))
									{
										currency = reader.nextString();
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
										p.setFoil(reader.nextName().equals("foil"));
										
										reader.beginObject();
										while(reader.hasNext())
										{
											p.getStockPrices().put(reader.nextName(), reader.nextDouble());
										} // fin boucle map date/prix
										reader.endObject();
									}//fin boucle Foil/Normal
									reader.endObject();
									
								}
								
								if(currency!=null)
								{
									p.setCurrency(currency);
									p.setStock(stock);
									data.getPrices().add(p);
								}
							
								
								
								
							}//fin de boucle buylist/retail/Currency
							reader.endObject();	
							
							
						}//fin boucle retailer;
						reader.endObject();	
						
					}//fin boucle mtgjsonIds
					ret.add(data);
					if(reader.peek()==JsonToken.NAME)
						System.out.println(reader.nextName());
					else
						reader.endObject();
				}//fin boucle data
				
			}
		
			return ret;
		
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


class Prices
{
	private boolean foil;
	private Map<String,Double> stockPrices;
	private Currency currency;
	private STOCK stock;
	private String retailer;
	
	
	
	public String getRetailer() {
		return retailer;
	}

	public void setRetailer(String retailer) {
		this.retailer = retailer;
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
		temp.append(retailer).append(" ").append(stock).append(" " ).append(foil?"Foil":"Normal").append(" ").append(currency).append(getStockPrices().size());
//		for(Entry<String, Double> p : getStockPrices().entrySet())
//			temp.append("\n\t").append(p);
		
		
		return temp.toString();
	}
	
	public Prices() {
		stockPrices = new HashMap<>();
	}
	
	public boolean isFoil() {
		return foil;
	}
	public void setFoil(boolean foil) {
		this.foil = foil;
	}
	
	public Map<String, Double> getStockPrices() {
		return stockPrices;
	}
	
	public void setStockPrices(Map<String, Double> stockPrices) {
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
	private SUPPORT support;
	private List<Prices> prices;
	
	public Data()
	{
		prices = new ArrayList<>();
	}
	
	public List<Prices> getPrices() {
		return prices;
	}
	
	public void setPrices(List<Prices> prices) {
		this.prices = prices;
	}
	

	@Override
	public String toString() {
		return mtgjsonId + " " + support + " " + prices ;
	}

	public String getMtgjsonId() {
		return mtgjsonId;
	}
	public void setMtgjsonId(String mtgjsonId) {
		this.mtgjsonId = mtgjsonId;
	}
	public SUPPORT getSupport() {
		return support;
	}
	public void setSupport(SUPPORT support) {
		this.support = support;
	}

}

