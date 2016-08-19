package org.magic.exports.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MagicDAO;
import org.magic.api.interfaces.MagicPricesProvider;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateNotFoundException;

public class MagicWebSiteGenerator extends Observable{
	
	Template template ;
	Configuration cfg ;
	MagicDAO dao;
	private String dest;
	private List<MagicPricesProvider> pricesProvider;
	private List<MagicCollection> cols;
	
	public MagicWebSiteGenerator(MagicDAO dao,String template,String dest) throws IOException, ClassNotFoundException, SQLException {
		cfg = new Configuration(Configuration.VERSION_2_3_23);
		cfg.setDirectoryForTemplateLoading(new File("./templates"+"/"+template));
		cfg.setDefaultEncoding("UTF-8");
		//cfg.setNumberFormat("#");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER );
		cfg.setObjectWrapper( new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_23).build());
		this.dao=dao;
		this.dest = dest;
		FileUtils.copyDirectory(new File("./templates/"+template), new File(dest),new FileFilter() {
			public boolean accept(File pathname) {
				if(pathname.isDirectory())
					return true;
				
				if(pathname.getName().endsWith(".html"))
					return false;
				
				return true;
			}
		});
	}
	
	//lister la page d'acccueil
	public void generate(List<MagicCollection> cols,List<MagicPricesProvider> providers) throws TemplateException, IOException, SQLException
	{
		
		this.pricesProvider=providers;
		this.cols = cols;

		Template template = cfg.getTemplate("index.html");
			Writer out = new FileWriter(new File(dest+"\\index.htm"));
		
			Map root = new HashMap();
			for(MagicCollection col : cols)
				root.put(col.getName(), dao.getCardsFromCollection(col));
			
			template.process(root, out);
		
		generateCollectionsTemplate();
		
		
	}
	
	

	//lister les editions disponibles
	private void generateCollectionsTemplate() throws IOException, TemplateException, SQLException
	{
		Template template = cfg.getTemplate("page-col.html");
		
		for(MagicCollection col : cols){
			Map rootEd = new HashMap<>();
				rootEd.put("cols", cols);
				rootEd.put("colName", col.getName());
				Set<MagicEdition> eds = new HashSet<MagicEdition>();
				for(MagicCard mc : dao.getCardsFromCollection(col))
				{
					eds.add(mc.getEditions().get(0));
					generateCardsTemplate(mc);
				}
				
				rootEd.put("editions",eds);
				
				
				FileWriter out = new FileWriter(new File(dest+"\\page-col-"+col.getName()+".htm"));
				template.process(rootEd, out);
				
				for(String ed : dao.getEditionsIDFromCollection(col))
				{
					generateEditionsTemplate(eds,col);
				}
				out.close();
				
		}
	}

	//lister les cartes disponibles dans la collection
	private void generateEditionsTemplate(Set<MagicEdition> eds,MagicCollection col) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, SQLException, TemplateException
	{
		Template cardTemplate = cfg.getTemplate("page-ed.html");
		Map rootEd = new HashMap<>();
			rootEd.put("cols",cols);
			rootEd.put("editions",eds);
			rootEd.put("col", col);
			rootEd.put("colName", col.getName());
			FileWriter out = null;
			for(MagicEdition ed : eds)
			{
				rootEd.put("cards", dao.getCardsFromCollection(col, ed));
				rootEd.put("edition", ed);
				out = new FileWriter(new File(dest+"\\page-ed-"+col.getName()+"-"+ed.getId()+".htm"));
				cardTemplate.process(rootEd, out);
			}
			out.close();
	}
	
	
	
	
	int i=0;
	private void generateCardsTemplate(MagicCard mc) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
		Template cardTemplate = cfg.getTemplate("page-card.html");
		
				Map rootEd = new HashMap<>();
				rootEd.put("card", mc);
				rootEd.put("cols", cols);
				
				List<MagicPrice> prices= new ArrayList<MagicPrice>();
				if(pricesProvider.size()>0)
				{
					for(MagicPricesProvider prov : pricesProvider)
					{
						try 
						{
							prices.addAll(prov.getPrice(mc.getEditions().get(0), mc));
						} 
						catch (Exception e) 
						{
							e.printStackTrace();
						}
					}
				}
				rootEd.put("prices", prices);
				FileWriter out = new FileWriter(new File(dest+"\\page-card-"+mc.getId()+".htm"));
				cardTemplate.process(rootEd, out);
				
				setChanged();
				notifyObservers(i++);
				out.close();
		
	}
}

