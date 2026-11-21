package org.magic.game.model.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.enums.EnumCardsPatterns;
import org.magic.game.interfaces.AbstractCounter;
import org.magic.game.model.counters.BonusCounter;
import org.magic.game.model.counters.ItemCounter;

public class CountersFactory {

	private static CountersFactory instance;

	public static CountersFactory getInstance() {
		if (instance == null) {
		    instance = new CountersFactory();
		}

		return instance;
	}

	private CountersFactory() {
	}

	public List<ItemCounter> createItemCounter(MTGCard mc) {

		if (mc.getText() == null) {
		    return new ArrayList<>();
		}

		final ArrayList<ItemCounter> arr = new ArrayList<>();
		final var p = Pattern.compile(EnumCardsPatterns.COUNTERS.getPattern());
		final var m = p.matcher(mc.getText());
		while (m.find()) {
			final String value = m.group(2);
			if (!value.contains("/")) {
				final var bonus = new ItemCounter(value);
				arr.add(bonus);
			}
		}

		return arr;
	}

	public List<AbstractCounter> createCounter(MTGCard mc) {
		final ArrayList<AbstractCounter> arr = new ArrayList<>();

		final var p = Pattern.compile(EnumCardsPatterns.COUNTERS.getPattern());
		final var m = p.matcher(mc.getText());
		while (m.find()) {
			final String value = m.group(2);
			if (value.contains("/")) {
				final String[] splitedValue = value.split("/");
				final var power = Integer.parseInt(splitedValue[0].replace("\\+", ""));
				final var toughness = Integer.parseInt(splitedValue[1].replace("\\+", ""));
				final var bonus = new BonusCounter(power, toughness);
				arr.add(bonus);
			} else {
				final var item = new ItemCounter(value);
				arr.add(item);
			}
		}

		return arr;

	}

}
