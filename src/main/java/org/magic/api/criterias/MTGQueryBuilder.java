package org.magic.api.criterias;

public interface MTGQueryBuilder<T> {

	
	public T build(MTGCrit<?>... crits);

}
