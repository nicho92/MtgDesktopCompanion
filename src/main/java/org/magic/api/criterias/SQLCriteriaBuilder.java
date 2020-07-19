package org.magic.api.criterias;


import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.table;

import org.jooq.Query;
import org.jooq.Record3;
import org.jooq.SelectWhereStep;



public class SQLCriteriaBuilder extends AbstractQueryBuilder<Query>
{
	@SuppressWarnings("unchecked")
	public Query build(MTGCrit<?>... crits)
	{
		Query query = select(field("*")).from(table("cards"));
		
			for(MTGCrit<?> c:crits)
			{
				if(c.isList())
				{
					((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).in(getValueFor(c.getVal())));
				}
				else
				{
					
					switch(c.getOperator())
					{
						case EQ : ((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).eq(getValueFor(c.getFirst())));break;
						case GREATER : ((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).greaterThan(getValueFor(c.getFirst())));break;
						case LOWER : ((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).lessThan(getValueFor(c.getFirst())));break;
						case LIKE : ((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).like("%"+getValueFor(c.getFirst())+"%"));break;
						case END_WITH : ((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).like("%"+getValueFor(c.getFirst())));break;
						case START_WITH : ((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).like(getValueFor(c.getFirst())+"%"));break;
						case GREATER_EQ : ((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).greaterOrEqual(getValueFor(c.getFirst())));break;
						case LOWER_EQ : ((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).lessOrEqual(getValueFor(c.getFirst())));break;
						case IN: ((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).in(getValueFor(c.getFirst())));break;
					}
				}
			}
		
		return query;
	}

	
	
}
