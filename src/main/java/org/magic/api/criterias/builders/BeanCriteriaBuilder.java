package org.magic.api.criterias.builders;

import java.util.function.Predicate;

import org.magic.api.beans.MagicCard;
import org.magic.api.criterias.AbstractQueryBuilder;
import org.magic.api.criterias.MTGCrit;

public class BeanCriteriaBuilder extends AbstractQueryBuilder<Predicate<MagicCard>> {

	@Override
	public Predicate<MagicCard> build(MTGCrit<?>... crits) {
		// TODO Auto-generated method stub
		return null;
	}

}
