package org.magic.api.exports.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.beanutils.BeanUtils;
import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.api.interfaces.MagicCardsProvider.STATUT;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGControler;


public class CSVExport extends AbstractCardExport{

	String exportedProperties[] ;
	String exportedDeckProperties[];
	String exportedPricesProperties[];

	
	@Override
	public STATUT getStatut() {
		return STATUT.STABLE;
	}
	

	@Override
	public String getName() {
		return "CSV";
	}

	public CSVExport() {
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("exportedProperties", "number,name,cost,supertypes,types,subtypes,editions");
			props.put("exportedDeckProperties", "name,cost,supertypes,types,subtypes,editions[0].id");
			props.put("exportedPricesProperties", "site,seller,value,currency,language,quality,foil");
			props.put("importDeckCharSeparator", ";");
			save();
		}
	}
	@Override
	public List<MagicCardStock> importStock(File f) throws Exception {
		BufferedReader read = new BufferedReader(new FileReader(f));
		List<MagicCardStock> stock= new ArrayList<MagicCardStock>();
		String line = read.readLine();
		
		line=read.readLine();//skip header
		while(line!=null)
		{
			String part[]= line.split(";");
			MagicCardStock mcs = new MagicCardStock();
			MagicCard mc = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", part[1], null,true).get(0);
				
				for(MagicEdition ed : mc.getEditions())
					if(ed.getSet().equals(part[2]))
					{
						mc.getEditions().add(0, ed);
						break;
					}
			
			
				mcs.setMagicCard(mc);
				mcs.setLanguage(part[3]);
				mcs.setQte(Integer.parseInt(part[4]));
				mcs.setCondition(EnumCondition.valueOf(part[5]));
				mcs.setFoil(Boolean.valueOf(part[6]));
				mcs.setAltered(Boolean.valueOf(part[7]));
				mcs.setSigned(Boolean.valueOf(part[8]));
				mcs.setMagicCollection(new MagicCollection(part[9]));
				mcs.setPrice(Double.valueOf(part[10]));
				try{ 
					mcs.setComment(part[11]);
				}
				catch (ArrayIndexOutOfBoundsException aioob)
				{
					mcs.setComment("");
				}
				mcs.setIdstock(-1);
				mcs.setUpdate(true);
				stock.add(mcs);
			line=read.readLine();
			
		}
		
		read.close();
		return stock;
	}
	
	
	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws Exception {
		FileWriter out= new FileWriter(f);
		BufferedWriter bw=new BufferedWriter(out);
		bw.write("id;Card Name;Edition;Language;Qte;Condition;Foil;Altered;Signed;Collection;Price;Comment\n");
		for(MagicCardStock mcs : stock)
		{
			bw.write(mcs.getIdstock()+";");
			bw.write(mcs.getMagicCard().getName()+";");
			bw.write(mcs.getMagicCard().getEditions().get(0)+";");
			bw.write(mcs.getLanguage()+";");
			bw.write(mcs.getQte()+";");
			bw.write(mcs.getCondition()+";");
			
			bw.write(mcs.isFoil()+";");
			bw.write(mcs.isAltered()+";");
			bw.write(mcs.isSigned()+";");
			
			bw.write(mcs.getMagicCollection()+";");
			bw.write(mcs.getPrice()+";");
			bw.write(mcs.getComment()+";");
			bw.write("\n");
		}
		
		bw.close();
		out.close();
		
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

	@Override
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

	@Override
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
	public MagicDeck importDeck(File f) throws Exception {
		BufferedReader read = new BufferedReader(new FileReader(f));
		MagicDeck deck = new MagicDeck();
		deck.setName(f.getName().substring(0,f.getName().indexOf(".")));
		
		String line = read.readLine();
		
		while(line!=null)
		{
			String part[]= line.split(getProperty("importDeckCharSeparator").toString());
			String name = part[0];
			String qte = part[1];
			String set = part[2];
			
			MagicEdition ed = new MagicEdition();
			ed.setId(set);
			List<MagicCard> list = MTGControler.getInstance().getEnabledProviders().searchCardByCriteria("name", name, ed,true);
			
			deck.getMap().put(list.get(0),Integer.parseInt(qte));
			line=read.readLine();
		}
		
		read.close();
		return deck;
	}
	
	@Override
	public Icon getIcon() {
		return new ImageIcon(CSVExport.class.getResource("/icons/xls.png"));
	}

}
