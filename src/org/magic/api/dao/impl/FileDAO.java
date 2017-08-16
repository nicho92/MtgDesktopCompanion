package org.magic.api.dao.impl;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
	static final Logger logger = LogManager.getLogger(FileDAO.class.getName());
	  
	
	public MagicCard fileToMagicCard(File f) throws Exception
	{
		return export.fromJson(FileUtils.readFileToString(f), MagicCard.class);
	}
	
	public void magicCardToFile(MagicCard mc , File f) throws Exception
	{
		FileUtils.write(f, export.toJson(mc));
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
		//export=MTGControler.getInstance().loadItem(AbstractCardExport.class, props.getProperty("SERIALIZER"));
		export = new Gson();
		directory=new File(props.getProperty("URL"));
		
		if(!directory.exists())
			directory.mkdir();
		
		new File(directory,"cards").mkdir();
		new File(directory,"alerts").mkdir();
		new File(directory,"stocks").mkdir();
		
		new File(directory,"cards/"+MTGControler.getInstance().get("default-library")).mkdir();
	}

	@Override
	public String getName() {
		return "FileDB";
	}

	@Override
	public void saveCard(MagicCard mc, MagicCollection collection) throws SQLException {
		File f = new File(directory,"cards/"+collection.getName());
		
		if(!f.exists())
			f.mkdir();
			
		f=new File(f,mc.getEditions().get(0).getId());
		if(!f.exists())
			f.mkdir();
		
		f=new File(f,IDGenerator.generate(mc));
		
		try {
			magicCardToFile(mc, f);
		} catch (Exception e) {
			throw new SQLException(e);
		}
		
		
		
	}

	@Override
	public void removeCard(MagicCard mc, MagicCollection collection) throws SQLException {
		File f = new File(directory,"cards/"+collection.getName()+"/"+IDGenerator.generate(mc));
		
		if(f.exists())
			f.delete();

	}
/*
	@Override
	public MagicCard loadCard(String name, MagicCollection collection) throws SQLException {
		
	}*/

	@Override
	public List<MagicCard> listCards() throws SQLException {
		return null;
	}

	@Override
	public int getCardsCount(MagicCollection list, MagicEdition me) throws SQLException {
		return new File(directory,"cards/"+list+"/"+me.getId()).listFiles().length;
	}

	@Override
	public Map<String, Integer> getCardsCountGlobal(MagicCollection c) throws SQLException {
		Map<String, Integer> map = new TreeMap<String, Integer>();
		File eds = new File(directory,"cards/"+c.getName());
		for(File ed : eds.listFiles())
			map.put(ed.getName(), ed.listFiles().length);
		
		return map;
	}

	@Override
	public List<MagicCard> getCardsFromCollection(MagicCollection c) throws SQLException {
		File col = new File(directory,"cards/"+c.getName());
		List<MagicCard> ret = new ArrayList<MagicCard>();
		for(File f : FileUtils.listFilesAndDirs(col, TrueFileFilter.INSTANCE, FileFileFilter.FILE))
		{
			try {
				ret.add(fileToMagicCard(f));
			} catch (Exception e) {
				throw new SQLException(e);
			}
		}
		
		return ret;
	}

	@Override
	public List<MagicCard> getCardsFromCollection(MagicCollection c, MagicEdition me) throws SQLException {
		File col = new File(directory,"cards/"+c.getName());
		
		if(me!=null)
			col = new File(col,me.getId());
		
		logger.debug("list dir : " + col.getAbsolutePath());
		List<MagicCard> ret = new ArrayList<MagicCard>();
		
		for(File f : FileUtils.listFilesAndDirs(col,TrueFileFilter.INSTANCE,FileFileFilter.FILE))
		{
			logger.debug("File found : " + f.getAbsolutePath());
			try {
				if(!f.isDirectory())
					ret.add(fileToMagicCard(f));
			} catch (Exception e) {
				throw new SQLException(e);
			}
		}
		
		return ret;
	}

	@Override
	public List<MagicCollection> getCollectionFromCards(MagicCard mc) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		FileDAO f = new FileDAO();
		f.init();
		f.getEditionsIDFromCollection(new MagicCollection("Library"));
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
			f.delete();

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
		// TODO Auto-generated method stub

	}

	@Override
	public List<MagicCardStock> getStocks(MagicCard mc, MagicCollection col) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveOrUpdateStock(MagicCardStock state) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteStock(List<MagicCardStock> state) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<MagicCardStock> getStocks() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MagicCardAlert> getAlerts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveAlert(MagicCardAlert alert) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAlert(MagicCardAlert alert) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasAlert(MagicCard mc) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDBLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getDBSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void backup(File dir) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAlert(MagicCardAlert alert) throws Exception {
		// TODO Auto-generated method stub

	}

}
