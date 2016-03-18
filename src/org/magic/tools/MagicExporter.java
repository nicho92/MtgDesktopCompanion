package org.magic.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MagicPricesProvider;

public class MagicExporter {

	
	static String exportedProperties[] = new String[]{	"number","name","cost","supertypes","types","subtypes","editions","layout"};
	static String exportedDeckProperties[] = new String[]{	"name","cost","supertypes","types","subtypes","editions"};
	static String exportedPricesProperties[] = new String[]{	"site","seller","value","currency"};
	
	
	//TODO export card prices catalog
	public static void exportPriceCatalog(List<MagicCard> cards, File f,MagicPricesProvider prov) throws Exception
	{
		BufferedWriter bw;
		FileWriter out;
		
		out = new FileWriter(f);
		bw=new BufferedWriter(out);
		
		for(String k : exportedProperties)
			bw.write(k+";");
		for(String k : exportedPricesProperties)
			bw.write(k+";");
		
		bw.write("\n");

		for (MagicCard mc : cards)
		{
		
			for(MagicPrice prices : prov.getPrice(mc.getEditions().get(0),mc))
			{
				for(String k : exportedProperties){
					String val = BeanUtils.getProperty(mc, k);
					if(val==null)
						val="";
					bw.write(val.replaceAll("\n", "")+";");
				}
				
				for(String p : exportedPricesProperties){
					String val = BeanUtils.getProperty(prices,p);
					if(val==null)
						val="";
					bw.write(val.replaceAll("\n", "")+";");
				}
				
				
				bw.close();
				out.close();
				
			}
			bw.write("\n");
		}
		bw.close();
		out.close();
		
		
		
		
	}
	
	
	public static void exportCSV(List<MagicCard> cards, File f) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
			BufferedWriter bw;
			FileWriter out;
				
			out = new FileWriter(f);
			bw=new BufferedWriter(out);
				for(String k : exportedProperties)
					bw.write(k+";");
			
			bw.write("\n");
			
			for (MagicCard mc : cards){
				for(String k : exportedProperties){
					String val = BeanUtils.getProperty(mc, k);
					if(val==null)
						val="";
					bw.write(val.replaceAll("\n", "")+";");
				}
				bw.write("\n");
			}
			bw.close();
			out.close();
		
		
			bw.close();
			out.close();
	}

	public static void export(MagicDeck deck, File f) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException{
		BufferedWriter bw;
		FileWriter out;
		out = new FileWriter(f);
		bw=new BufferedWriter(out);
		
		bw.write("Main list\n");
		
		bw.write("Qte;");
			for(String k : exportedDeckProperties)
				bw.write(k+";");
		
		bw.write("\n");
		
		for (MagicCard mc : deck.getMap().keySet()){
			bw.write(deck.getMap().get(mc)+";");
			for(String k : exportedDeckProperties){
				String val = BeanUtils.getProperty(mc, k);
				if(val==null)
					val="";
				bw.write(val.replaceAll("\n", "")+";");
			}
			bw.write("\n");
		}
		
		bw.write("SideBoard\n");
		bw.write("Qte;");
		for(String k : exportedDeckProperties)
			bw.write(k+";");
	
		bw.write("\n");
		for (MagicCard mc : deck.getMapSideBoard().keySet()){
			bw.write(deck.getMapSideBoard().get(mc)+";");
			for(String k : exportedDeckProperties){
				String val = BeanUtils.getProperty(mc, k);
				if(val==null)
					val="";
				bw.write(val.replaceAll("\n", "")+";");
			}
			bw.write("\n");
		}
		
		bw.close();
		out.close();
	}

}
