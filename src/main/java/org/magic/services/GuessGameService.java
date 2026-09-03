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
    private String hiddenString = "-";
    private int attempts=0;

  
    public void init() throws IOException
    {
	
	var card = new ScryFallProvider().randomCard();
	card.setUrl(new ScryFallPicturesProvider().generateUrl(card, true));	
	
	init(card);
    }


    private void init(MTGCard mc)
    {
	revealedLetters = new ArrayList<>();
	this.result = mc;
	attempts=0;
	
	suggest(null);
    }

    public MTGCard getCurrent() {
	return current;
    }

    public int getAttempts() {
	return attempts;
    }

    public MTGCard getResult() {
	return result;
    }

    public List<Character> getRevealedLetters() {
	return revealedLetters;
    }

    private String anonymise(String text)
    {
	var result = new StringBuilder();
	for (var c : text.toCharArray()) {
	    
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
	
	if(l!=null)
	    revealedLetters.add(Character.toLowerCase(l));

	try {
	    current = BeanTools.cloneBean(result);
	    current.setName(anonymise(result.getName()));
	    current.setText(anonymise(result.getText()));

	} catch (Exception e) {
	    e.printStackTrace();
	}

    }








}
