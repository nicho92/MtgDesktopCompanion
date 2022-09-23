package org.magic.api.interfaces;

import java.io.IOException;

public interface MTGCache<U,V> extends MTGPlugin{

	public V getItem(U k);

	public void put(V value, U key) throws IOException;

	public void clear();

	public long size();


}
