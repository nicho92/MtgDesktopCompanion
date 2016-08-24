package org.magic.api.exports.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Observable;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.beanutils.BeanUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.CardExporter;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.gui.DeckBuilderGUI;
import org.magic.services.MagicFactory;


public class CSVExport extends AbstractCardExport{

	String exportedProperties[] ;
	String exportedDeckProperties[];
	String exportedPricesProperties[];

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getName() {
		return "CSV";
	}

	public CSVExport() {
		super();
		if(!new File(MagicFactory.CONF_DIR, "exp-"+getName()+".conf").exists()){
			props.put("exportedProperties", "number,name,cost,supertypes,types,subtypes,editions");
			props.put("exportedDeckProperties", "name,cost,supertypes,types,subtypes,editions");
			props.put("exportedPricesProperties", "site,seller,value,currency,language,quality,foil");
			save();
		}
	}

	//TODO export card prices catalog
	public void exportPriceCatalog(List<MagicCard> cards, File f,MagicPricesProvider prov) throws Exception
	{
		BufferedWriter bw;
		FileWriter out;

		out = new FileWriter(f);
		bw=new BufferedWriter(out);

		exportedProperties=getProperty("exportedProperties").toString().split(",");
		exportedPricesProperties=getProperty("exportedPricesProperties").toString().split(",");
		
		for(String k : exportedProperties)
			bw.write(k+";");
		for(String k : exportedPricesProperties)
			bw.write(k+";");

		bw.write("\n");
		int i =0;
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
				bw.write("\n");
			}
			setChanged();
			notifyObservers(i++);
		}
		bw.close();
		out.close();
	}

	public void export(List<MagicCard> cards, File f) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
		BufferedWriter bw;
		FileWriter out;
		exportedProperties=getProperty("exportedProperties").toString().split(",");
		
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
	}

	public void export(MagicDeck deck, File f) throws IOException{
		
		exportedDeckProperties=getProperty("exportedDeckProperties").toString().split(",");
		
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
				String val=null;
				try {
					val = BeanUtils.getProperty(mc, k);
				} catch (Exception e) {
					e.printStackTrace();
				}
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
				String val=null;
				try {
					val = BeanUtils.getProperty(mc, k);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(val==null)
					val="";
				bw.write(val.replaceAll("\n", "")+";");
			}
			bw.write("\n");
		}

		bw.close();
		out.close();
	}


	@Override
	public String getFileExtension() {
		return ".csv";
	}


	@Override
	public MagicDeck importDeck(File f) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Icon getIcon() {
		return new ImageIcon(CSVExport.class.getResource("/res/xls.png"));
	}

}
