package org.magic.api.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class BoosterPackContainer implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private transient Map<MagicEdition, Integer> pack;

	public BoosterPackContainer() {
		pack = new HashMap<>();
	}

	public Integer getQty(MagicEdition ed) {
		return pack.get(ed);
	}

	public List<MagicEdition> listEditions() {
		return new ArrayList<>(pack.keySet());
	}

	public void set(MagicEdition ed, int qty) {
		pack.put(ed, qty);
	}

	public void add(MagicEdition ed, int qty) {
		if (pack.get(ed) != null)
			pack.put(ed, pack.get(ed) + qty);
		else
			pack.put(ed, qty);
	}

	public void remove(MagicEdition ed, int qty) {
		int res = pack.get(ed) - qty;
		if (res < 0)
			res = 0;

		pack.put(ed, res);
	}

	public void remove(MagicEdition ed) {
		pack.remove(ed);
	}

	public Map<MagicEdition, Integer> get() {
		return pack;
	}

	public void clear() {
		pack.clear();
	}

	public Set<Entry<MagicEdition, Integer>> getEntries() {
		return pack.entrySet();
	}

	public List<MagicEdition> toList() {
		List<MagicEdition> ret = new ArrayList<>();

		for (Entry<MagicEdition, Integer> e : pack.entrySet()) {
			for (var i = 0; i < e.getValue(); i++)
				ret.add(e.getKey());
		}
		return ret;
	}

	@Override
	public String toString() {
		var temp = new StringBuilder();
		for (Entry<MagicEdition, Integer> e : pack.entrySet())
			temp.append(e.getKey()).append("(").append(e.getValue()).append(")");

		return temp.toString();
	}

	public int size() {
		return pack.size();
	}
}
