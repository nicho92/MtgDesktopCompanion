package org.magic.api.criterias;

public interface MTGCriteriaConverter<T> {

	public String marshal(T source);

}
