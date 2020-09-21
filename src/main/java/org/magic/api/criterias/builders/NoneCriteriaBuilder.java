package org.magic.api.criterias.builders;

import org.magic.api.criterias.AbstractQueryBuilder;
import org.magic.api.criterias.MTGCrit;

public class NoneCriteriaBuilder extends AbstractQueryBuilder<String> {

	@Override
	public String build(MTGCrit<?>... crits) {
		return "";
	}

}
