package org.magic.api.dao.impl;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractMagicDAO;
import org.magic.services.MTGControler;
import org.magic.tools.IDGenerator;

import com.google.gson.Gson;

public class FileDAO extends AbstractMagicDAO {

	private Gson export;
	private File directory;
	  
	
	public <T> T read(Class<T> T, File f) throws Exception
	{
		return export.fromJson(FileUtils.readFileToString(f), T);
	}
	
	public void save(Object o , File f) throws Exception
	{
		FileUtils.write(f, export.toJson(o));
	}
	
	
	public FileDAO() {
		 super();	
	 		if(!new File(confdir, getName()+".conf").exists()){
	 			props.put("URL", MTGControler.CONF_DIR.getAbsolutePath()+"/dao/filesDB");
	 			props.put("SERIALIZER", "json");
	 			save();
	 		}
	}
	
	@Override
	public void init()  {
		export = new Gson();
		directory=new File(props.getProperty("URL"));
		
		if(!directory.exists())
			directory.mkdir();
		
		new File(directory,"cards").mkdir();
		new File(directory,"alerts").mkdir();
		new File(directory,"stocks").mkdir();
		
		new File(directory,"cards/"+MTGControler.getInstance().get("default-library")).mkdir();
		logger.debug("File DAO init");
	}

	@Override
	public String getName() {
		return "FileDB";
	}
	
	private String removeCon(String a)
	{
		if(a.equalsIgnoreCase("con"))
			return a+"_set";
		
		return a;
	}
	

	@Override
	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException {
		File f = new File(directory,"cards/"+collection.getName());
		
		if(!f.exists())
			f.mkdir();
			
		f=new File(f,removeCon(mc.getEditions().get(0).getId()));
		if(!f.exists())
			f.mkdir();
		
		f=new File(f,IDGenerator.generate(mc));
		
		try {
			save(mc, f);
		} catch (Exception e) {
			throw new SQLException(e);
		}
		
		
		
	}

	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		File f = new File(directory,"cards/"+collection.getName()+"/"+removeCon(mc.getEditions().get(0).getId())+"/"+IDGenerator.generate(mc));
		
		File parent = f.getParentFile();
		
		if(f.exists())
			f.delete();

		if(parent.listFiles().length==0)
			parent.delete();
		
	}

	@Override
	public List<MagicCard> listCards() throws SQLException {
		return null;
	}

	@Override
	public int getCardsCount(MagicCollection list, MagicEdition me) throws SQLException {
		File f = new File(directory,"cards/"+list.getName());
		
		if(me!=null)
			f = new File(f,"/"+removeCon(me.getId()));
		
		
		return FileUtils.listFiles(f, null, true).size();
	}

	@Override
	public Map<String, Integer> getCardsCountGlobal(MagicCollection c) throws SQLException {
		Map<String, Integer> map = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
		File eds = new File(directory,"cards/"+c.getName());
		for(File ed : eds.listFiles())
			map.put(removeCon(ed.getName()), ed.listFiles().length);
		
		return map;
	}

	@Override
	public List<MagicCard> getCardsFromCollection(MagicCollection c) throws SQLException {
		return getCardsFromCollection(c, null);
	}

	@Override
	public List<MagicCard> getCardsFromCollection(MagicCollection c, MagicEdition me) throws SQLException {
		File col = new File(directory,"cards/"+c.getName());
		
		if(me!=null)
			col = new File(col,removeCon(me.getId()));
		
		logger.debug("Load " + col);
		
		List<MagicCard> ret = new ArrayList<MagicCard>();
		
		for(File f : FileUtils.listFilesAndDirs(col,TrueFileFilter.INSTANCE,TrueFileFilter.INSTANCE))
		{
			try {
				if(!f.isDirectory())
					ret.add(read(MagicCard.class,f));
			} catch (Exception e) {
				throw new SQLException(e);
			}
		}
		
		return ret;
	}

	@Override
	public List<MagicCollection> getCollectionFromCards(MagicCard mc) throws SQLException {
		
		String id = IDGenerator.generate(mc);
		File f = new File(directory,"cards/");
		List<MagicCollection> ret = new ArrayList<MagicCollection>();
		Collection<File> res = FileUtils.listFiles(f,new NameFileFilter(id),TrueFileFilter.INSTANCE);
		
		for(File result : res)
				ret.add(new MagicCollection(result.getParentFile().getParentFile().getName()));	
	
		return ret;
	}

	
	@Override
	public List<String> getEditionsIDFromCollection(MagicCollection c) throws SQLException {
		File col = new File(directory,"cards/"+c.getName());
		List<String> ret = new ArrayList<String>();
		
		for(File f : col.listFiles())
			ret.add(f.getName());
		
		return ret;
	}

	@Override
	public MagicCollection getCollection(String name) throws SQLException {
		return new MagicCollection(name);
	}

	@Override
	public void saveCollection(MagicCollection c) throws SQLException {
		File f = new File(directory,"cards/"+c.getName());
		
		if(!f.exists())
			f.mkdir();

	}

	@Override
	public void removeCollection(MagicCollection c) throws SQLException {
		File f = new File(directory,"cards/"+c.getName());
		
		
		if(f.exists())
			try {
				FileUtils.deleteDirectory(f);
			} catch (IOException e) {
				throw new SQLException(e);
			}

	}

	@Override
	public List<MagicCollection> getCollections() throws SQLException {
		
		ArrayList<MagicCollection> ret = new ArrayList<MagicCollection>();
		
		for(File f : new File(directory,"cards/").listFiles())
			ret.add(new MagicCollection(f.getName()));
		
		
		return ret;
	}

	@Override
	public void removeEdition(MagicEdition ed, MagicCollection col) throws SQLException {
		File f = new File(directory,"cards/"+col.getName()+"/"+removeCon(ed.getId()));
		
		if(f.exists())
			try {
				FileUtils.deleteDirectory(f);
			} catch (IOException e) {
				throw new SQLException(e);
			}

	}

	@Override
	public List<MagicCardStock> getStocks(MagicCard mc, MagicCollection col) throws SQLException {
		List<MagicCardStock> st = new ArrayList<MagicCardStock>();
		File f = new File(directory,"/stocks");
		for(File fstock : FileUtils.listFiles(f,new WildcardFileFilter("*"+IDGenerator.generate(mc)),TrueFileFilter.INSTANCE))
		{
			try {
				MagicCardStock s = read(MagicCardStock.class, fstock);
				if(s.getMagicCollection().getName().equals(col.getName()))
					st.add(s);
				
			} catch (Exception e) {
				throw new SQLException(e);
			}
		}
		
		
		
		return st;
	}

	@Override
	public void saveOrUpdateStock(MagicCardStock state) throws SQLException {
		
		File f = new File(directory,"/stocks");
		
		if(state.getIdstock()==-1)
			state.setIdstock(f.listFiles().length+1);
			
		f = new File(f,state.getIdstock()+"-"+IDGenerator.generate(state.getMagicCard()));
		try {
			save(state, f);
		} catch (Exception e) {
			throw new SQLException(e);
		}
		

	}

	@Override
	public void deleteStock(List<MagicCardStock> state) throws SQLException {
		
		for(MagicCardStock s : state)
		{ 
			File f = new File(directory,"/stocks/"+s.getIdstock()+"/"+IDGenerator.generate(s.getMagicCard()));
			f.delete();
		}
	}

	@Override
	public List<MagicCardStock> getStocks() throws SQLException {
		List<MagicCardStock> ret = new ArrayList<MagicCardStock>();
		
		for(File f : FileUtils.listFiles(new File(directory,"/stocks"), null, false))
		{
			try {
				ret.add(read(MagicCardStock.class, f));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	@Override
	public List<MagicCardAlert> getAlerts() {
		List<MagicCardAlert> ret = new ArrayList<MagicCardAlert>();
		
		for(File f : FileUtils.listFiles(new File(directory,"/alerts"), null, false))
		{
			try {
				ret.add(read(MagicCardAlert.class, f));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	@Override
	public void saveAlert(MagicCardAlert alert) throws Exception {
		save(alert, new File(directory,"/alerts/"+IDGenerator.generate(alert.getCard())));
	}

	@Override
	public void deleteAlert(MagicCardAlert alert) throws Exception {
			File f = new File(directory,"/alerts/"+IDGenerator.generate(alert.getCard()));
			
			if(f.exists())
				f.delete();
		
	}

	@Override
	public void updateAlert(MagicCardAlert alert) throws Exception {
		saveAlert(alert);

	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean hasAlert(MagicCard mc) {
		return FileUtils.listFiles(new File(directory,"/alerts"),new NameFileFilter(IDGenerator.generate(mc)),TrueFileFilter.INSTANCE).size()>0;
	}

	@Override
	public String getDBLocation() {
		return directory.getAbsolutePath();
	}

	@Override
	public long getDBSize() {
		return FileUtils.sizeOf(directory);
	}

	@Override
	public void backup(File dir) throws Exception {
		FileUtils.copyDirectory(directory, dir);

	}

}
