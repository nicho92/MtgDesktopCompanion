package org.magic.api.beans.messages;

import java.util.List;
import java.util.stream.Collectors;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.services.MTGControler;

public class SearchAnswerMessage extends AbstractMessage {

	private static final long serialVersionUID = 1L;
	private SearchMessage searchQuery;
	private List<MTGCardStock> resultItems;

	
	public SearchMessage getSearchQuery() {
		return searchQuery;
	}
	
	public List<MTGCardStock> getResultItems() {
		return resultItems;
	}
	
	public SearchAnswerMessage(SearchMessage msg,List<MTGCardStock> ret) {
		setTypeMessage(MSG_TYPE.ANSWER);
		this.searchQuery = msg;
		this.resultItems = ret;
		setMessage("I have ! "+ ret.stream().map(mcs->mcs.getProduct().getName() + " " + mcs.getQte() + " " + mcs.getLanguage() + " " + mcs.getCondition() + " " + mcs.getPrice() + " " + MTGControler.getInstance().getCurrencyService().getCurrentCurrency()).collect(Collectors.joining(System.lineSeparator())));
	}

}
