package org.beta;

import java.io.IOException;

import org.magic.api.beans.enums.MTGColor;
import org.magic.api.criterias.MTGCrit;
import org.magic.api.criterias.MTGCrit.OPERATOR;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.providers.impl.MTGSQLiveProvider;

public class QueryTester {


	

	public static void main(String[] args) throws IOException {
		MTGCrit<String> cmcA = new MTGCrit<>("type",OPERATOR.LIKE,"Creature");
		MTGCrit<Integer> cmcI = new MTGCrit<>("convertedManaCost",OPERATOR.GREATER_EQ,6);
		MTGCrit<MTGColor> cmcC = new MTGCrit<>("colors",OPERATOR.IN,MTGColor.BLACK,MTGColor.RED);

		//$.data..cards[?(@['colors'].indexOf('R') != -1)]
		MTGCardsProvider prov = new MTGSQLiveProvider();
		prov.init();
		prov.searchByCriteria(cmcI,cmcA,cmcC).forEach(c->{
			System.out.println(c + " " + c.getCurrentSet());
		});
		
	}
}
