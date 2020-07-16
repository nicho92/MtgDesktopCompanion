package org.magic.api.criterias;

import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.criterias.MTGCrit.OPERATOR;

public interface MTGQueryBuilder {

	
	public String build(MTGCrit<?>... crits);
	
	

	public static void main(String[] args) {
		MTGCrit<String> cmcE = new MTGCrit<>("setCode",OPERATOR.EQ,"ROE");
		MTGCrit<String> cmcA = new MTGCrit<>("types",OPERATOR.LIKE,"creature");
		MTGCrit<Integer> cmcI = new MTGCrit<>("layout",OPERATOR.GREATER_EQ,2);
		MTGCrit<String> cmcL = new MTGCrit<>("layout",OPERATOR.LIKE,MTGLayout.LEVELER.name());
		
		
		System.out.println(new JsonCriteriaBuilder().build(cmcE,cmcI,cmcA,cmcL));
	}
	
}
