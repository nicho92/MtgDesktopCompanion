package org.beta;

import java.io.IOException;

import forohfor.scryfall.api.MTGCardQuery;

public class ScryFall2 {

	
	public static void main(String[] args) {
		  try {
			  MTGCardQuery.search("");
			MTGCardQuery.getCardFromURI("https://api.scryfall.com/cards/multiverse/1381");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
	}
}
