package org.magic.api.interfaces;

import java.util.List;

import org.magic.api.beans.GedEntry;

public interface MTGGedStorage extends MTGPlugin{

	public <T> String store(GedEntry<T> entry);
	public <T> GedEntry<T> getEntryById(String id);
	public <T> List<GedEntry<T>> listEntryFor(Class<T> classe);
}
