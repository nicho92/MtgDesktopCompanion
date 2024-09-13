package org.magic.api.beans.messages;

import java.util.List;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.abstracts.AbstractMessage;

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
		setMessage("I have \""+msg.getItem()+ "\" for you "+ msg.getAuthor().getName());
	}

}
