package org.magic.api.beans.messages;

import java.awt.Color;
import java.io.IOException;
import java.util.regex.Pattern;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.tools.MTG;

public class TalkMessage extends AbstractMessage{

	private static final long serialVersionUID = 1L;
	private static final String REGEX = "\\{(.*?)\\}";
	private MTGCard mc;
	
	public TalkMessage(String message, Color color) {
		setTypeMessage(MSG_TYPE.TALK);
		setMessage(message);
		setColor(color);
		
		var m = Pattern.compile(REGEX).matcher(message);
		if(m.find())
		{
			var cardName = m.group(1);
			try {
				var ret = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cardName, null, true);
				if(!ret.isEmpty())
					mc=ret.get(0);
				
			} catch (IOException e) {
				logger.error(e);
			}
		}
		
	}
	
	public MTGCard getMagicCard() {
		return mc;
	}
	
	
	
}
