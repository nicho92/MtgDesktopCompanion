package org.magic.services.providers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.api.mkm.modele.Product;
import org.magic.api.beans.ConverterItem;
import org.magic.api.interfaces.MTGExternalShop;
import org.magic.services.MTGLogger;
import org.magic.tools.FileTools;

public class StockItemConversionManager {

	private List<ConverterItem> conversionsItems;
	private Logger logger = MTGLogger.getLogger(this.getClass());
	private String separator;
	private File file;
	private static StockItemConversionManager inst;
	
	
	public void initFile(File f) throws IOException
	{
		initFile(f, ";");
	}
	
	public static StockItemConversionManager inst()
	{
		if(inst==null)
			inst = new StockItemConversionManager();
		
		
		return inst;
	}
	
	
	private StockItemConversionManager() {
		conversionsItems = new ArrayList<>();
		try {
			initFile(new File("C:\\Users\\Nicolas\\Google Drive\\conversions.csv"));
			} catch (IOException e) {
			logger.error(e);
		}
	}

	public List<ConverterItem> getOutputRefs(String lang, String sourceName, int idSource)
	{
		return conversionsItems.stream().filter(p->(p.getSource().equalsIgnoreCase(sourceName) && p.getLang().equalsIgnoreCase(lang) && p.getInputId()==idSource)).collect(Collectors.toList());
	}

	public List<ConverterItem> getConversionsItems() {
		return conversionsItems;
	}
	
	public void sendItem(Product p, MTGExternalShop input, MTGExternalShop output, String lang) throws IOException
	{
			int ret = output.createProduct(p);
			appendConversion(new ConverterItem(input.getName(),output.getName(),p.getEnName(), lang,p.getIdProduct(), ret));
	}
	
	
	public void initFile(File f,String separator) throws IOException
	{
		this.file = f ;
		this.separator=separator;
		
			conversionsItems.clear();
			var list = Files.readAllLines(f.toPath());
			list.remove(0); // remove title
			list.forEach(s->{
				var arr = s.split(separator);
				
				try {
					conversionsItems.add( new ConverterItem(arr[0],arr[1],arr[2],arr[3],Integer.parseInt(arr[4]) ,Integer.parseInt(arr[5])));
				} catch (Exception e) {
					logger.error(s+"|"+e.getMessage());
				}
			});
			logger.debug("Conversions loaded with " + conversionsItems.size() + " items");
	}
	
	public void appendConversion(ConverterItem c)
	{
		conversionsItems.add(c);
		String s = c.getSource()+separator+c.getDestination()+separator+c.getName()+separator+c.getLang()+separator+c.getInputId()+separator+c.getOutputId();
		try {
			FileTools.appendLine(file, s);
		} catch (IOException e) {
			logger.error(e);
		}
	}
}
