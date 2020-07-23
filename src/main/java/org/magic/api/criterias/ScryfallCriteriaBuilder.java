package org.magic.api.criterias;

public class ScryfallCriteriaBuilder extends AbstractQueryBuilder<String> {
	
	
	public String build(MTGCrit<?>... crits) {
		
		
		StringBuilder temp = new StringBuilder();
		
		
		for(MTGCrit<?> c : crits)
		{
			
			
			if(c.getType()==Boolean.class)
			{
				temp.append("is:"+c.getAtt());
			}
			else
			{
				switch(c.getOperator())
				{
					case END_WITH: break;
					case EQ: break;
					case GREATER: break;
					case GREATER_EQ: break;
					case IN: break;
					case LIKE: break;
					case LOWER: break;
					case LOWER_EQ: break;
					case START_WITH: break;
					default: break;
				}
			}
			
			
		}
		
		temp.append(" ");
		
		
		return temp.toString().trim();
	}

}
