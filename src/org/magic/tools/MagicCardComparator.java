package org.magic.tools;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.dao.impl.FileDAO;


public class MagicCardComparator implements Comparator<MagicCard> {

	//String order : white, blue, black, red, green, multicolor, artifact,lands, basic lands(plains, island, swamp, moutain, forest)
	//a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
	
	
	private enum COLORS {White, Blue, Black, Red, Green, Multi, None};
	
	public static void main(String[] args) throws SQLException {
		
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		
		FileDAO dao = new FileDAO();
				dao.init();
				
		List<MagicCard> list = dao.getCardsFromCollection(new MagicCollection("Library"));
		Collections.sort(list, new MagicCardComparator());
		
		for(MagicCard mc : list)
		{
			System.out.println(mc.getName() +"\t"+mc.getColors()+"\t"+mc.getFullType());
		}
		System.exit(0);
	}
	
	
	@Override
	public int compare(MagicCard o1, MagicCard o2) {
		
		if(o1.equals(o2))
			return 0;
		else
			return sortColor(o1,o2);
	//	sortType(o1,o2);
	//	sortName(o1,o2);
	}

	private int sortName(MagicCard o1, MagicCard o2) {
		return 0;
	}

	private int sortType(MagicCard o1, MagicCard o2) {
		return 0;
		
	}

	private int sortColor(MagicCard o1, MagicCard o2) {
		if(col(o1).equals(COLORS.White))
			if(col(o2).equals(COLORS.White))
				return 1;
			else
				return -1;
		
		if(col(o1).equals(COLORS.Blue))
			if(col(o2).equals(COLORS.White)||col(o2).equals(COLORS.Blue))
				return -1;
			else
				return 1;
		
		
		
		return 1;
	}
	
	
	private COLORS col(MagicCard mc)
	{
		if(mc.getColors().size()==0)
			return COLORS.None;
		
		if(mc.getColors().size()>1)
			return COLORS.Multi;
		
		if(mc.getColors().get(0).equalsIgnoreCase("white"))
			return COLORS.White;
		
		if(mc.getColors().get(0).equalsIgnoreCase("blue"))
			return COLORS.Blue;
		
		if(mc.getColors().get(0).equalsIgnoreCase("black"))
			return COLORS.Black;
		
		if(mc.getColors().get(0).equalsIgnoreCase("red"))
			return COLORS.Red;
		
		if(mc.getColors().get(0).equalsIgnoreCase("green"))
			return COLORS.Green;
		
		return COLORS.None;
		
	}
	

}
