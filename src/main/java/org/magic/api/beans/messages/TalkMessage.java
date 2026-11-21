package org.magic.api.beans.messages;

import java.io.IOException;
import java.util.regex.Pattern;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.abstracts.AbstractMessage;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.services.tools.MTG;

public class TalkMessage extends AbstractMessage {

	private static final long serialVersionUID = 1L;
	private static final String REGEX = "\\{(.*?)\\}";
	private MTGCard mc;

	public TalkMessage(String message) {
		setTypeMessage(MSG_TYPE.TALK);
		setMessage(message);

		final var m = Pattern.compile(REGEX).matcher(message);
		if (m.find()) {
			final var cardName = m.group(1);
			try {
				final var ret = MTG.getEnabledPlugin(MTGCardsProvider.class).searchCardByName(cardName, null, false);
				if (!ret.isEmpty()) {
				    mc = ret.get(0);
				}

			} catch (final IOException e) {
				logger.error(e);
			}
		}

	}

	public MTGCard getAttachement() {
		return mc;
	}

}
