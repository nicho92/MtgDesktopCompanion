package org.magic.api.criterias;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.criterias.Criteria.OPERATOR;

public class CriteriaBuilder {
	public enum FORMAT { SQL, JSON};
	
	private FORMAT f;
	private Criteria<?>[] crits;
	
	
	public CriteriaBuilder(FORMAT f, Criteria<?>[] crits)
	{
		this.crits = crits;
		this.f = f;
	}

	public String build()
	{
		if(f.equals(FORMAT.SQL))
			return buildSQL();
		else
			return buildJSON();
	}
	
	private String buildJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	private String buildSQL() {
		
		StringBuilder temp = new StringBuilder();
		
		for(Criteria<?> c : crits)
		{
			temp.append(c.getAtt()).append(" ");
			temp.append(sql(c.getOperator())).append(" ");
			
			if(!c.isList())
			{
				temp.append(c.getFirst());
			}
			else
			{
				temp.append(StringUtils.join(c.getVal(), ","));
			}
			
			temp.append(" AND ");
		}
		
		return temp.toString();
		
	}

	private String sql(OPERATOR operator) {
		
		switch(operator) {
			case EQ : return "=";
			case GREATER : return ">";
			case HAS : return "in";
			case LIKE : return "like";
			case LOWER : return "<";
		}
		
		return "=";
		
		
	}

	@Override
	public String toString() {
		return StringUtils.join(crits, " AND ");
	}
	
}
