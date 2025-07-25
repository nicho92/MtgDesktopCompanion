package org.magic.api.sorters;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.enums.EnumColors;
import org.magic.api.beans.enums.EnumLayout;
import org.magic.api.interfaces.extra.MTGComparator;

public class CardsEditionSorter implements MTGComparator<MTGCard> {


	@Override
	public String toString() {
		return "Edition Sorter";
	}

	@Override
	public int compare(MTGCard o1, MTGCard o2) {

		try {

		boolean o1NullNumber=StringUtils.isEmpty(o1.getNumber());
		boolean o2NullNumber=StringUtils.isEmpty(o2.getNumber());
		
		if (!o1NullNumber && !o2NullNumber && (o1.getEdition().equals(o2.getEdition()))) {
			int n1 = calculate(o1.getNumber());
			int n2 = calculate(o2.getNumber());
			return n1 - n2;
		}

		// else compare
		int ret = test(o1, o2);
		if (ret == 0)
			ret = name(o1, o2);

		return ret;

		}
		catch(Exception _)
		{
			return 0;
		}

	}

	private int test(MTGCard o1, MTGCard o2) {

		if (getWeight(o1) < getWeight(o2))
			return -1;

		if (getWeight(o1) == getWeight(o2))
			return 0;

		return 1;

	}

	private int calculate(String s) {
		var num = s.replaceAll("\\D", "");
		return num.isEmpty() ? 0 : Integer.parseInt(num);
	}

	private int land(MTGCard mc) {
		if (mc.getName().equalsIgnoreCase("Plains"))
			return 9;

		if (mc.getName().equalsIgnoreCase("Island"))
			return 10;

		if (mc.getName().equalsIgnoreCase("Swamp"))
			return 11;

		if (mc.getName().equalsIgnoreCase("Mountain"))
			return 12;

		return 13; // return for forest
	}

	private int name(MTGCard o1, MTGCard o2) {
		return o1.getName().compareTo(o2.getName());
	}

	@Override
	public int getWeight(MTGCard mc) {
			
		
		if(mc.isShowCase())
			return 90;
		else if(mc.isBorderLess())
			return 91;
		else if(mc.isExtendedArt())
			return 92;
		else if (mc.isRetro())
			return 93;
		
		
		
		if (mc.getColors().isEmpty()) 
		{
			if (mc.isArtifact()) {
				return 7;
			} else if (mc.isLand()) {
				if (mc.isBasicLand()) {
					return land(mc); // basic land order
				} else {
					return 8; // advanced land
				}
			} else if (mc.getLayout()!=EnumLayout.NORMAL) {
				return 99;
			} else {
				return -1;
			}
		}
		

		return EnumColors.determine(mc.getColors()).getPosition();

	}

}
