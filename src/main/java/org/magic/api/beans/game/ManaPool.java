package org.magic.api.beans.game;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.enums.EnumCardsPatterns;
import org.magic.services.logging.MTGLogger;
import org.utils.patterns.observer.Observable;

public class ManaPool extends Observable implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, Integer> pool;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public ManaPool() {
		pool = new HashMap<>();

	}

	public int getMana(String color) {
		Integer ret = pool.get(color);

		if (ret == null)
			return 0;

		return ret;

	}

	public void setMana(String color, int number) {
		pool.put(color, number);
		setChanged();

	}

	public void useMana(String color, Integer number) {
		try {
			setMana(color, pool.get(color) - number);
		} catch (Exception _) {
			logger.error("error using {} {}. Pool={}",number,color,pool);
		}
	}

	public void useMana(MTGCard mc) {
		if (mc.getCmc() == null)
			return;

		var p = Pattern.compile(EnumCardsPatterns.MANA_PATTERN.getPattern());
		var m = p.matcher(mc.getCost());

		while (m.find()) {
			String c = m.group();
			useMana(c, 1);
		}
		notifyObservers(this);
	}

	public void clean() {
		pool.values().clear();
	}

	@Override
	public String toString() {

		var build = new StringBuilder();
		for (Entry<String, Integer> key : pool.entrySet())
			for (var i = 0; i < key.getValue(); i++)
				build.append(key.getKey());

		return build.toString();
	}

}
