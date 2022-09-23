package org.magic.api.criterias.builders;

import org.magic.api.criterias.AbstractQueryBuilder;
import org.magic.api.criterias.MTGCrit;

public class ScryfallCriteriaBuilder extends AbstractQueryBuilder<String> {


	@Override
	public String build(MTGCrit<?>... crits) {


		var temp = new StringBuilder();


		for(MTGCrit<?> c : crits)
		{


			if(c.getType()==Boolean.class)
			{
				temp.append("is:"+c.getAtt());
			}
			else
			{
				//TODO code eq value
			}


		}

		temp.append(" ");


		return temp.toString().trim();
	}

}
