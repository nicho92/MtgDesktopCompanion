package org.beta;

import java.io.IOException;
import java.util.Arrays;

import org.magic.api.beans.enums.MTGColor;
import org.magic.api.beans.enums.MTGLayout;
import org.magic.api.criterias.MTGCrit;
import org.magic.api.criterias.MTGCrit.OPERATOR;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.providers.impl.MTGSQLiveProvider;

public class QueryTester {


	

	public static void main(String[] args) throws IOException {
		MTGCrit<String> cmcA = new MTGCrit<>("type",OPERATOR.LIKE,"Creature");
		MTGCrit<Integer> cmcI = new MTGCrit<>("convertedManaCost",OPERATOR.GREATER_EQ,6);
		MTGCrit<MTGColor> cmcC = new MTGCrit<>("colors",OPERATOR.IN,MTGColor.BLACK,MTGColor.RED);
		MTGCrit<MTGLayout> cmcL = new MTGCrit<>("layout",OPERATOR.EQ,MTGLayout.NORMAL);
		MTGCardsProvider prov = new MTGSQLiveProvider();
		prov.init();
		
		
		prov.getQueryableAttributs();
		
		
		prov.searchByCriteria(cmcI,cmcA,cmcC,cmcL).forEach(c->{
			System.out.println(c + " " + c.getCurrentSet() +" " + Arrays.toString(c.getColors().toArray()));
		});
		
	}
}
