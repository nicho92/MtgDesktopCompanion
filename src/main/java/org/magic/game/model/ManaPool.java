package org.magic.game.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.game.gui.components.GamePanelGUI;
import org.magic.services.MTGLogger;
import org.magic.tools.MTGOraclePatterns;
import org.utils.patterns.observer.Observable;

public class ManaPool extends Observable implements Serializable {

	private Map<String, Integer> pool;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public ManaPool() {
		pool = new HashMap<>();
		addObserver(GamePanelGUI.getInstance().getManaPoolPanel());
	}

	public int getMana(String color) {
		Integer ret = pool.get(color);

		if (ret == null)
			return 0;

		return ret;

	}

	public void addMana(String mana) {
		addMana(mana, 1);
	}

	public void addMana(String mana, Integer number) {
		try {
			pool.put(mana, pool.get(mana) + number);
			setChanged();
		} catch (NullPointerException e) {
			setMana(mana, number);
		}
	}

	public void setMana(String color, int number) {
		pool.put(color, number);
		setChanged();

	}

	public void useMana(String color, Integer number) {
		try {
			setMana(color, pool.get(color) - number);
		} catch (Exception e) {
			logger.error("error using " + number + " " + color + ". Pool="+pool);
		}
	}

	public void useMana(MagicCard mc) {
		if (mc.getCmc() == null)
			return;

		Pattern p = Pattern.compile(MTGOraclePatterns.MANA_PATTERN.getPattern());
		Matcher m = p.matcher(mc.getCost());

		while (m.find()) {
			String c = m.group();
			useMana(c, 1);
		}
		notifyObservers(this);
	}

	public void clean() {
		pool.values().clear();
	}

	public String toString() {

		StringBuilder build = new StringBuilder();
		for (Entry<String, Integer> key : pool.entrySet())
			for (int i = 0; i < key.getValue(); i++)
				build.append(key.getKey());

		return build.toString();
	}

}
