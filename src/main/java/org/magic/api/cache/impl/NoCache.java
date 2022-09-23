package org.magic.api.cache.impl;

import java.awt.image.BufferedImage;

import org.magic.api.beans.MagicCard;
import org.magic.api.interfaces.abstracts.AbstractCacheProvider;

public class NoCache extends AbstractCacheProvider {

	public NoCache() {
		super();
	}


	@Override
	public String getName() {
		return "No Cache";
	}

	@Override
	public BufferedImage getItem(MagicCard mc) {
		return null;
	}

	@Override
	public void put(BufferedImage im, MagicCard mc) {
		// Nothing to do

	}

	@Override
	public void clear() {
		// nothing to do

	}

	@Override
	public String getVersion() {
		return "1";
	}

	@Override
	public long size() {
		return 0;
	}
}
