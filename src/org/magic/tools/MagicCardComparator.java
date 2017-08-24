package org.magic.tools;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.dao.impl.FileDAO;
import org.magic.api.dao.impl.MysqlDAO;


public class MagicCardComparator implements Comparator<MagicCard> {

	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		
		MysqlDAO dao = new MysqlDAO();
				dao.init();
				
				MagicEdition ed = new MagicEdition();
				ed.setSet("test");
				ed.setId("AKH");
				
		List<MagicCard> list = dao.getCardsFromCollection(new MagicCollection("Library"),ed);
		Collections.sort(list, new MagicCardComparator());
		
		for(MagicCard mc : list)
		{
			System.out.println(mc.getName() +"\t"+mc.getColors()+"\t"+mc.getFullType());
		}
		System.exit(0);
	}
	
	
	@Override
	public int compare(MagicCard o1, MagicCard o2) {
		
//			System.out.println("-"+o1.getEditions().get(0).getNumber()+"-");
//			if(o1.getEditions().get(0).getNumber()!=null && o2.getEditions().get(0).getNumber()!=null)
//				return o1.getEditions().get(0).getNumber().compareTo(o2.getEditions().get(0).getNumber());
//		
			int ret = test(o1,o2);
			
			if(ret==0)
				ret=name(o1,o2);
			
			return ret;
			
		//	sortType(o1,o2);
		//	sortName(o1,o2);
	}



	private int test(MagicCard o1, MagicCard o2) {
		if(calcule(o1)<calcule(o2))
			return -1;
		
		if(calcule(o1)==calcule(o2))
			return 0;
		
		return 1;
			
	}
	
	private boolean isBasic(MagicCard mc)
	{
		String cardName=mc.getName();
		return (cardName.trim().equalsIgnoreCase("Plains")||cardName.trim().equalsIgnoreCase("Island")||cardName.trim().equalsIgnoreCase("Swamp")||cardName.trim().equalsIgnoreCase("Mountain")||cardName.trim().equalsIgnoreCase("Forest"));
			
	}
	
	private int land(MagicCard mc)
	{
		if(mc.getName().equalsIgnoreCase("Plains"))
			return 8;
		
		if(mc.getName().equalsIgnoreCase("Island"))
			return 9;
			
		if(mc.getName().equalsIgnoreCase("Swamp"))
			return 10;
			
		if(mc.getName().equalsIgnoreCase("Mountain"))
			return 11;
			
		//if(mc.getName().equalsIgnoreCase("Forest"))
			return 12;
	}
	
	private int name(MagicCard o1, MagicCard o2) {
		return o1.getName().compareTo(o2.getName());
	}
	
	private int calcule(MagicCard mc)
	{
	
		if(mc.getColors().size()==0)
		{
			if(mc.getTypes().toString().toLowerCase().contains("artifact"))
			{
				return 6;
			}
			else if (mc.getTypes().toString().toLowerCase().contains("land"))
			{
					if(isBasic(mc))
					{
						return land(mc); //basic land order
					}
					else
					{
						return 7; // advanced land
					}
			}
			else
			{
					return -1; //colorless eldrazi spell;
			}

		}
		

		if(mc.getColors().size()>1)
			return 5;	
		
		if(mc.getColors().get(0).equalsIgnoreCase("white"))
			return 0;
		
		if(mc.getColors().get(0).equalsIgnoreCase("blue"))
			return 1;
		
		if(mc.getColors().get(0).equalsIgnoreCase("black"))
			return 2;
		
		if(mc.getColors().get(0).equalsIgnoreCase("red"))
			return 3;
		
		if(mc.getColors().get(0).equalsIgnoreCase("green"))
			return 4;
		
		
		
		return -1;
	}
	

}
