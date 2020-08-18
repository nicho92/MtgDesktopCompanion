package org.magic.api.criterias;

public class NoneCriteriaBuilder extends AbstractQueryBuilder<String> {

	@Override
	public String build(MTGCrit<?>... crits) {
		return "";
	}

}
