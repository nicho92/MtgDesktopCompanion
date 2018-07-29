package org.beta;

import java.io.IOException;
import java.util.List;

import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.magic.api.beans.MagicCard;
import org.magic.game.model.factories.AbilitiesFactory;
import org.magic.services.MTGControler;

public class TestPredicates {

	
	public static void main(String[] args) throws IOException {
		MTGControler.getInstance().getEnabledCardsProviders().init();
		
		List<MagicCard> list = MTGControler.getInstance().getEnabledCardsProviders().searchCardByCriteria("name", "Sinister Concoction", null, false);

		for(int index=0;index<1;index++) {
			System.out.println(list.get(index));
			System.out.println(AbilitiesFactory.getInstance().getAbilities(list.get(index)));
		}
		
	}
}
