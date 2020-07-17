package org.magic.api.criterias;

import java.io.IOException;

import org.magic.api.beans.enums.MTGColor;
import org.magic.api.criterias.MTGCrit.OPERATOR;
import org.magic.api.providers.impl.Mtgjson5Provider;

public interface MTGQueryBuilder<T> {

	
	public T build(MTGCrit<?>... crits);
	
	

	public static void main(String[] args) throws IOException {
		MTGCrit<String> cmcA = new MTGCrit<>("type",OPERATOR.LIKE,"Creature");
		MTGCrit<Integer> cmcI = new MTGCrit<>("convertedManaCost",OPERATOR.GREATER_EQ,6);
		MTGCrit<String> cmcC = new MTGCrit<>("colors",OPERATOR.IN,MTGColor.BLACK.getCode());

		
		//$.data..cards[?(@['colors'].indexOf('R') != -1)]
		Mtgjson5Provider prov = new Mtgjson5Provider();
		prov.init();
		prov.searchByCriteria(cmcC).forEach(c->{
			System.out.println(c);
		});
		
	}
	
}
