package org.magic.game.model.costs;

import java.util.Map;
import java.util.TreeMap;

import org.magic.game.interfaces.Cost;
@Deprecated
public class ManaCost implements Cost {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final Map<String, Integer> mcost;

	@Deprecated
	public ManaCost() {
		mcost = new TreeMap<>();
	}

	@Deprecated
	public void add(String mana, int qty) {
		mcost.computeIfAbsent(mana, _ -> 0);
		mcost.put(mana, mcost.get(mana) + qty);
	}

	@Deprecated
	public void add(String mana) {
		add(mana, 1);
	}

	@Deprecated
	@Override
	public String toString() {

		final var build = new StringBuilder();

		mcost.entrySet().forEach(e -> {
			for (var i = 0; i < e.getValue(); i++) {
			    build.append(e.getKey());
			}
		});
		return build.toString();

	}
}
