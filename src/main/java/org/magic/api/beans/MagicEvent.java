package org.magic.api.beans;

import java.awt.Color;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.magic.game.model.Player;

public class MagicEvent implements Serializable{

	private static final long serialVersionUID = 1L;
	private int id;

	private Date startDate;
	private Date endDate;
	private String title;
	private EVENT_FORMAT format;
	private String localisation;
	private String description;
	private URL url;
	private int duration;
	private Color color;
	private ROUNDS roundFormat;
	private List<Player> players;
	private Integer rounds =3;
	private Integer roundTime = 45;
	private Integer maxWinRound = 3;
	private int currentRound=1;
	private List<Party> parties;

	public enum EVENT_FORMAT { CONSTRUCTED, DRAFT, SEALED}
	public enum ROUNDS { SWISS, DIRECT_ELIMINATION }

	private boolean started=false;



	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Party> getParties() {
		return parties;
	}

	public void setParties(List<Party> parties) {
		this.parties = parties;
	}


	public void setCurrentRound(int currentRound) {
		this.currentRound = currentRound;
	}

	public int getCurrentRound() {
		return currentRound;
	}

	public Integer getRounds() {
		return rounds;
	}

	public void setRounds(Integer rounds) {
		this.rounds = rounds;
	}

	public Integer getRoundTime() {
		return roundTime;
	}

	public void setRoundTime(Integer roundTime) {
		this.roundTime = roundTime;
	}

	public Integer getMaxWinRound() {
		return maxWinRound;
	}

	public void setMaxWinRound(Integer maxWinRound) {
		this.maxWinRound = maxWinRound;
	}

	public MagicEvent() {
		players = new ArrayList<>();
		parties = new ArrayList<>();
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setRoundFormat(ROUNDS roundFormat) {
		this.roundFormat = roundFormat;
	}

	public ROUNDS getRoundFormat() {
		return roundFormat;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public EVENT_FORMAT getFormat() {
		return format;
	}

	public void setFormat(EVENT_FORMAT format) {
		this.format = format;
	}

	public String getLocalisation() {
		return localisation;
	}

	public void setLocalisation(String localisation) {
		this.localisation = localisation;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return getTitle();
	}

}
