package org.magic.api.interfaces.abstracts;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCombo;
import org.magic.api.interfaces.MTGComboProvider;
import org.magic.services.tools.TCache;

public abstract class AbstractComboProvider extends AbstractMTGPlugin implements MTGComboProvider {

	protected TCache<List<MTGCombo>> cache;


	@Override
	public PLUGINS getType() {
		return PLUGINS.COMBO;
	}


	@Override
	public List<MTGCombo> getComboWith(MTGCard mc) {

		try {
			return cache.get(mc.getName(),new Callable<List<MTGCombo>>() {

				@Override
				public List<MTGCombo> call() throws Exception {
					return loadComboWith(mc);
				}
			});
		} catch (ExecutionException e) {
			logger.error("Error loading combo cache for {} : {}",getName(),e);
			return new ArrayList<>();
		}
	}


	public abstract List<MTGCombo> loadComboWith(MTGCard mc);



	protected AbstractComboProvider() {
		cache = new TCache<>("combos");
	}
}
