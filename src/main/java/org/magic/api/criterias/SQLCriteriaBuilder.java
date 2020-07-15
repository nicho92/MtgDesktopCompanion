package org.magic.api.criterias;


import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.table;

import java.util.Arrays;

import org.jooq.Query;
import org.magic.api.criterias.Criteria.OPERATOR;



public class SQLCriteriaBuilder 
{
	private Query query;
	
	
	public String build(Criteria<?>... crits)
	{
		query = select(field("*"))
                	  .from(table("CARDS"));
                
		
		
		
		
	
		return query.toString();
	}
	

	
	
}
