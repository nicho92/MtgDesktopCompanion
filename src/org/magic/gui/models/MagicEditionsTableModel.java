package org.magic.gui.models;

import java.awt.Image;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.gui.renderer.MagicCollectionTableCellRenderer;
import org.magic.services.MTGDesktopCompanionControler;

public class MagicEditionsTableModel extends DefaultTableModel{

	String[] columns = new String[] {"code","edition","cards numbers","date","%","qte","Type","Block", "Online"};
	
	List<MagicEdition> list;

	private Map<MagicEdition,Integer> mapCount;
	HashMap<String, ImageIcon> cache;
	
	int countTotal=0;
	int countDefaultLibrary=0;

	
	public List<MagicEdition> getEditions()
	{
		return list;
	}
	
	public void init(List<MagicEdition> editions ){
		this.list=editions;
		mapCount=new HashMap<MagicEdition,Integer>();
		
		try {
			calculate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void calculate() throws SQLException, Exception {
		
		MagicCollection mc = new MagicCollection();
						mc.setName(MTGDesktopCompanionControler.getInstance().get("default-library"));
					
		Map<String,Integer> temp = MTGDesktopCompanionControler.getInstance().getEnabledDAO().getCardsCountGlobal(mc);
		
		for(MagicEdition me : list)
		{
			mapCount.put(me, (temp.get(me.getId())==null)?0:temp.get(me.getId()));
			
			countDefaultLibrary+=mapCount.get(me);
			initCache(me);
		}
	
		for(MagicEdition me : list)
			countTotal+=me.getCardCount();

	}




	public int getCountTotal() {
		return countTotal;
	}

	public void setCountTotal(int countTotal) {
		this.countTotal = countTotal;
	}

	public int getCountDefaultLibrary() {
		return countDefaultLibrary;
	}

	public void setCountDefaultLibrary(int countDefaultLibrary) {
		this.countDefaultLibrary = countDefaultLibrary;
	}

	public MagicEditionsTableModel() {
		list = new ArrayList<MagicEdition>();
		cache=new HashMap<>();
		
	}
	
	private void initCache(MagicEdition e) {
		try{
		ImageIcon im;
		if(e.getId().startsWith("p"))
		{
			im=new ImageIcon(ImageIO.read(MagicCollectionTableCellRenderer.class.getResource("/res/set/icons/VAN_set.png")));
		}
		else
		{	im = new ImageIcon(ImageIO.read(MagicCollectionTableCellRenderer.class.getResource("/res/set/icons/"+e.getId()+"_set.png")));
		}
		cache.put(e.getId(),im);
		}
		catch(Exception ex)
		{
			cache.put(e.getId(), new ImageIcon());
		}
		
	}

	@Override
	public String getColumnName(int column) {
		return columns[column];
	}
	
	@Override
	public int getRowCount() {
		if(list==null)
			return 0;
		
		return list.size();
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		MagicEdition e =  list.get(row);
		
			if(column==0)
				return cache.get(e.getId());
		
			if(column==1)
				return e;

			if(column==2)
				return e.getCardCount();

			if(column==3)
				return e.getReleaseDate();
			
			if(column==4)
			{
				if(e.getCardCount()>0)
					return (double) mapCount.get(e) / e.getCardCount();
				else
					return  (double) mapCount.get(e) / 1;
			}
			
			if(column==5)
				return mapCount.get(e);
			
			if(column==6)
				return e.getType();

			if(column==7)
				return e.getBlock();
			
			if(column==8)
				return e.isOnlineOnly();
			
			
		return "";
		
			
	}
	
	private ImageIcon getIcon(MagicEdition e) {
		try
		{
			ImageIcon im ;
			if(cache.get(e.getId())==null)
			{
				if(e.getId().startsWith("p"))
					im=new ImageIcon(ImageIO.read(MagicCollectionTableCellRenderer.class.getResource("/res/set/icons/VAN_set.png")).getSubimage(12, 11, 55, 42).getScaledInstance(26, 24, Image.SCALE_SMOOTH));
				else
					im = new ImageIcon(ImageIO.read(MagicCollectionTableCellRenderer.class.getResource("/res/set/icons/"+e.getId()+"_set.png")).getSubimage(12, 11, 55, 42).getScaledInstance(26, 24, Image.SCALE_SMOOTH));
			
				cache.put(e.getId(),im);
				return im;
			}
			else
			{
				return cache.get(e.getId().toString());
			}
		} 
		catch(Exception ex)
		{ 
			return new ImageIcon();
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		switch(columnIndex)
		{
		case 0:return ImageIcon.class;
		case 1 : return MagicEdition.class;
		case 2: return Integer.class;
		case 3 : return String.class;
		case 4 : return double.class;
		case 5 : return Integer.class;
		case 8 : return Boolean.class;
		default : return Object.class;
		}
		
		//return super.getColumnClass(columnIndex);
	}
	
	@Override
	public int getColumnCount() {
		return columns.length;
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
}
