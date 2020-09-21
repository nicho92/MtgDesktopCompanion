package org.magic.api.criterias;

public interface MTGQueryBuilder<T> {

	
	public T build(MTGCrit<?>... crits);


	public <U> void addConvertor(Class<U> clazz, MTGCriteriaConverter<U> mtgCriteriaConverter);


	public <U> Object getValueFor(U object);

}
