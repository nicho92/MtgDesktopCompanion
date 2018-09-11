package org.magic.sorters;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.MTGComparator;

public class CardsEditionSorter implements MTGComparator<MagicCard> {

	@Override
	public int compare(MagicCard o1, MagicCard o2) {

		if (o1.getCurrentSet().getNumber() != null && o2.getCurrentSet().getNumber() != null && (o1.getCurrentSet().equals(o2.getCurrentSet()))) {
			int n1 = calculate(o1.getCurrentSet().getNumber());
			int n2 = calculate(o2.getCurrentSet().getNumber());
			return n1 - n2;
		}

		// else compare
		int ret = test(o1, o2);
		if (ret == 0)
			ret = name(o1, o2);

		return ret;
	}

	private int test(MagicCard o1, MagicCard o2) {

		if (getWeight(o1) < getWeight(o2))
			return -1;

		if (getWeight(o1) == getWeight(o2))
			return 0;

		return 1;

	}

	private int calculate(String s) {
		String num = s.replaceAll("\\D", "");
		return num.isEmpty() ? 0 : Integer.parseInt(num);
	}

	private int land(MagicCard mc) {
		if (mc.getName().equalsIgnoreCase("Plains"))
			return 8;

		if (mc.getName().equalsIgnoreCase("Island"))
			return 9;

		if (mc.getName().equalsIgnoreCase("Swamp"))
			return 10;

		if (mc.getName().equalsIgnoreCase("Mountain"))
			return 11;

		return 12; // return 12 for forest
	}

	private int name(MagicCard o1, MagicCard o2) {
		return o1.getName().compareTo(o2.getName());
	}

	public int getWeight(MagicCard mc) {

		if (mc.getColors().isEmpty()) {

			if (mc.getTypes().toString().toLowerCase().contains("artifact")) {
				return 6;
			} else if (mc.getTypes().toString().toLowerCase().contains("land")) {
				if (mc.isBasicLand()) {
					return land(mc); // basic land order
				} else {
					return 7; // advanced land
				}
			} else if (!mc.getLayout().equalsIgnoreCase("normal")) {
				return 99;
			} else {
				return -1;
			}

		}

		if (mc.getColors().size() > 1)
			return 5;

		if (mc.getColors().get(0).equalsIgnoreCase("white"))
			return 0;

		if (mc.getColors().get(0).equalsIgnoreCase("blue"))
			return 1;

		if (mc.getColors().get(0).equalsIgnoreCase("black"))
			return 2;

		if (mc.getColors().get(0).equalsIgnoreCase("red"))
			return 3;

		if (mc.getColors().get(0).equalsIgnoreCase("green"))
			return 4;

		return 100;
	}

}
