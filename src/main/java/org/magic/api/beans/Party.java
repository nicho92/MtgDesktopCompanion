package org.magic.api.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.beans.game.Player;

public class Party implements Serializable {

	private static final long serialVersionUID = 1L;
	private Player player1;
	private Player player2;
	private List<Round> rounds;
	private boolean started = false;



	public void setStarted(boolean started) {
		this.started = started;
	}

	public boolean isStarted() {
		return started;
	}

	public Party(Player p1,Player p2, int nbRounds, boolean started)
	{
		this.player1=p1;
		this.player2=p2;
		this.started=started;

		rounds = new ArrayList<>();

		for(var i=0;i<nbRounds;i++)
			rounds.add(new Round(i+1));
	}


	public Player getPlayer1() {
		return player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public List<Round> getRounds() {
		return rounds;
	}






}
