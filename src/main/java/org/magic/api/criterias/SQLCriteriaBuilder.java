package org.magic.api.criterias;


import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.table;

import org.jooq.Query;
import org.jooq.Record3;
import org.jooq.SelectWhereStep;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.criterias.MTGCrit.OPERATOR;



public class SQLCriteriaBuilder implements MTGQueryBuilder 
{
	private Query query;
	
	
	public String build(MTGCrit<?>... crits)
	{
			query = select(field("*")).from(table("cards"));
		
			for(MTGCrit<?> c:crits)
			{
				if(c.isList())
				{
					((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).in(c.getVal()));
				}
				else
				{
					
					switch(c.getOperator())
					{
						case EQ : ((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).eq(c.getFirst()));break;
						case GREATER : ((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).greaterThan(c.getFirst()));break;
						case LOWER : ((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).lessThan(c.getFirst()));break;
						case LIKE : ((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).like("%"+c.getFirst()+"%"));break;
						case END_WITH : ((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).like("%"+c.getFirst()));break;
						case START_WITH : ((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).like(c.getFirst()+"%"));break;
						case GREATER_EQ : ((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).greaterOrEqual(c.getFirst()));break;
						case LOWER_EQ : ((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).lessOrEqual(c.getFirst()));break;
						case HAS: ((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).in(c.getVal()));break;
					}
				}
			}
		
		return query.toString();
	}
	
}
