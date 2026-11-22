package org.magic.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.MTGCard;
import org.magic.api.pictures.impl.ScryFallPicturesProvider;
import org.magic.api.providers.impl.ScryFallProvider;
import org.magic.services.tools.BeanTools;

public class GuessGameService {

    private MTGCard result;
    private MTGCard current;
    private List<Character> revealedLetters;
    private final String hiddenString = "_ ";
    private int maxAttemps = 8;

    public enum GameStatus { GAMING, WIN, LOST}


    public void setMaxAttemps(int maxAttempts) {
	this.maxAttemps  = maxAttempts;

    }

    public int getMaxAttemps() {
	return maxAttemps;
    }

    boolean isWordComplete() {
        return !current.getName().contains(hiddenString);
    }


    public int getAttemptsRemaining() {
	return getMaxAttemps() - getAttempts();
    }

    public GameStatus getStatus() {
        if (isWordComplete()) {
            return GameStatus.WIN;
        } else if (getAttemptsRemaining() <= 0) {
            return GameStatus.LOST;
        } else {
            return GameStatus.GAMING;
        }
    }


    public void init() throws IOException
    {

	final var card = new ScryFallProvider().randomCard();
	card.setUrl(new ScryFallPicturesProvider().generateUrl(card, true));

	init(card);
    }

    private void init(MTGCard mc)
    {
	revealedLetters = new ArrayList<>();
	this.result = mc;
	setMaxAttemps(mc.getName().length());
	suggest(null);
    }

    public MTGCard getCurrent() {
	return current;
    }

    public int getAttempts() {
	return revealedLetters.size();
    }

    public MTGCard getResult() {
	return result;
    }

    public List<Character> getRevealedLetters() {
	return revealedLetters;
    }

    private String anonymise(String text)
    {
	final var result = new StringBuilder();
	for (final var c : text.toCharArray()) {

	    if (!Character.isLetter(c) && !Character.isDigit(c)) {
		result.append(c);
		continue;
	    }

	    if (revealedLetters.contains(Character.toLowerCase(c))) {
		result.append(c);
	    } else {
		result.append(hiddenString );
	    }
	}
	return result.toString();
    }

    public void suggest( Character l) {

	if(l!=null) {
	    revealedLetters.add(Character.toLowerCase(l));
	}

	try {
	    current = BeanTools.cloneBean(result);
	    current.setName(anonymise(result.getName()));
	    current.setText(anonymise(result.getText()));
	} catch (final Exception _) {
	   //do nothing
	}

    }



}
