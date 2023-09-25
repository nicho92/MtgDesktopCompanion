package org.magic.api.criterias.builders;


import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.table;

import org.jooq.Query;
import org.jooq.Record3;
import org.jooq.SelectWhereStep;
import org.magic.api.criterias.AbstractQueryBuilder;
import org.magic.api.criterias.MTGCrit;



public class SQLCriteriaBuilder extends AbstractQueryBuilder<Query>
{
	@Override
	@SuppressWarnings("unchecked")
	public Query build(MTGCrit<?>... crits)
	{
		
		Query query = select(table("cards").asterisk(),table("cardIdentifiers").asterisk() )
							  .from(table("cards"),table("cardIdentifiers"))
							  .where("cardIdentifiers.uuid=cards.uuid");
							 
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
						case IN: ((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).in(getValueFor(c.getVal())));break;
					}
				}
			}
						
		return query;
	}



}
