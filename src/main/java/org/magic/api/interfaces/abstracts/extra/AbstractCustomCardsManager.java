package org.magic.api.interfaces.abstracts.extra;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGEdition;
import org.magic.api.interfaces.CustomCardsManager;
import org.magic.api.interfaces.abstracts.AbstractCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractMTGPlugin;
import org.magic.api.sorters.CardsEditionSorter;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.BeanTools;
import org.magic.services.tools.CryptoUtils;

public abstract class AbstractCustomCardsManager extends AbstractMTGPlugin implements CustomCardsManager{

	protected Logger logger = MTGLogger.getLogger(this.getClass());

	@Override
	public PLUGINS getType() {
		return PLUGINS.CUSTOM;
	}

	protected abstract void saveCustomCard(MTGCard mc) throws IOException;

	
	@Override
	public void saveCustomSet(MTGEdition ed, List<MTGCard> cards) {
		
		cards.forEach(mc->{
			try {
				deleteCustomCard(mc);
				saveCustomCard(ed, mc);
				saveCustomSet(ed);
				notify(mc);
				
			} catch (IOException e) {
				logger.error(e);
			}
		});
	}
	
	@Override
	public void saveCustomCard(MTGEdition me, MTGCard mc) throws IOException {
		
		mc.setEdition(me);
		
		if (mc.getId() == null)
			mc.setId(CryptoUtils.sha256Hex(Instant.now().toEpochMilli()+ me.getSet() + mc.getName()));

		AbstractCardsProvider.postTreatmentCard(mc);
		
		saveCustomCard(mc);
		
	}
	



	@Override
	public void rebuild(MTGEdition ed) throws IOException {
		var cards = listCustomsCards(ed);
		ed.setCardCount(cards.size());
		ed.setCardCountOfficial((int)cards.stream().filter(mc->mc.getSide().equals("a")).count());
		ed.setCardCountPhysical(ed.getCardCountOfficial());
		
		cards.forEach(mc->{
			mc.getEditions().clear();
			try {
				mc.getEditions().add(BeanTools.cloneBean(ed));
				mc.setEdition(ed);
				mc.setNumber(null);
			} catch (Exception e) {
				logger.error(e);
			} 
		});
		Collections.sort(cards,new CardsEditionSorter());

		for(var i=0;i<cards.size();i++)
			cards.get(i).setNumber(String.valueOf((i+1)));

		saveCustomSet(ed,cards);
	}


}
