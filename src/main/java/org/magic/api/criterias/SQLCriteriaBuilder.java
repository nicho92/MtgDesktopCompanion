package org.magic.api.criterias;


import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.table;

import org.jooq.Query;
import org.jooq.Record3;
import org.jooq.SelectWhereStep;
import org.magic.api.beans.enums.MTGColor;
import org.magic.api.criterias.Criteria.OPERATOR;



public class SQLCriteriaBuilder 
{
	private Query query;
	
	
	public String build(Criteria<?>... crits)
	{
		query = select(field("*"))
                .from(table("cards"));
		
			for(Criteria c:crits)
			{
				if(c.isList())
					((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).in(c.getVal()));
				else
					((SelectWhereStep<Record3<Object, Object, Object>>) query).where(field(c.getAtt()).contains(c.getFirst()));
			}
		
		return query.toString();
	}
	

	public static void main(String[] args) {
		Criteria<Integer> cmcC = new Criteria<>("convertedManaCost",OPERATOR.GREATER,3);
		Criteria<String> cmcA = new Criteria<>("types",OPERATOR.HAS,"creature");
		
		System.out.println(new SQLCriteriaBuilder().build(cmcC,cmcA));
	}
	
	
	
}
