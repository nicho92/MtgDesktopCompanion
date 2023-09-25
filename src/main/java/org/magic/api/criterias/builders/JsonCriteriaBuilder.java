package org.magic.api.criterias.builders;
import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.magic.api.criterias.AbstractQueryBuilder;
import org.magic.api.criterias.MTGCrit;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.Predicate;


public class JsonCriteriaBuilder extends AbstractQueryBuilder<Filter> {


	@Override
	public Filter build(MTGCrit<?>... crits) {

		List<Predicate> l = new ArrayList<>();

		for(MTGCrit<?> c : crits)
		{

			switch(c.getOperator())
			{
				case EQ: l.add(where(c.getAtt()).eq(getValueFor(c.getFirst())));break;
				case GREATER:l.add(where(c.getAtt()).gt(getValueFor(c.getFirst())));break;
				case GREATER_EQ:l.add(where(c.getAtt()).gte(getValueFor(c.getFirst())));break;
				case LIKE:l.add(where(c.getAtt()).regex(Pattern.compile("/^.*"+getValueFor(c.getFirst()+".*$/i"))));break;
				case LOWER:l.add(where(c.getAtt()).lt(getValueFor(c.getFirst())));break;
				case LOWER_EQ:l.add(where(c.getAtt()).lte(getValueFor(c.getFirst())));break;
				case START_WITH:break;
				case IN:l.add(where(c.getAtt()).in(getValueFor(c.getVal())));break;
				case NOT:l.add(where(c.getAtt()).noneof(getValueFor(c.getVal())));break;
				case END_WITH :break;
			}
		}
		return filter(l);
	}

}
