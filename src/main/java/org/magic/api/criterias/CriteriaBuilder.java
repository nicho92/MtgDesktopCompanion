package org.magic.api.criterias;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.enums.MTGColor;
import org.magic.api.criterias.Criteria.OPERATOR;

public class CriteriaBuilder {

	List<Criteria<?>> builder;
	
	
	public CriteriaBuilder(Criteria<?> c)
	{
		builder = new ArrayList<>();
		
		builder.add(c);
	}
	
	public void and(Criteria<?> c)
	{
		builder.add(c);
	}

	@Override
	public String toString() {
		return StringUtils.join(builder.toArray(), " AND ");
	}
	
	
	public static void main(String[] args) {
		
		CriteriaBuilder build = new CriteriaBuilder(new Criteria<>("name",OPERATOR.LIKE,"Liliana"));
		build.and(new Criteria<>("color", OPERATOR.HAS, MTGColor.BLACK,MTGColor.GOLD));
		build.and(new Criteria<>("cmc", OPERATOR.GREATER, 3 ));
		
		System.out.println(build.toString());
	}
	
}
