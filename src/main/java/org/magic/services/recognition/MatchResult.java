package org.magic.services.recognition;

public class MatchResult {
	private String result;
	private String name;
	private String setCode;
	private String number;
	private String scryfallId;
	private double score;
	private String id;

	public String getSetCode() {
		return setCode;
	}

	public String getName() {
		return name;
	}

	public String getNumber() {
		return number;
	}

	public String getScryfallId() {
		return scryfallId;
	}

	public double getScore() {
		return score;
	}

	public String getId() {
		return id;
	}

	public MatchResult(String result, double score) {
		super();
		this.result = result;
		this.score = score;
		String[] split = result.split("\\|");
		name = split[0];
		setCode = split[1].toUpperCase();
		number = split[2];
		scryfallId = split[3];
	}

	@Override
	public int hashCode() {
		final var prime = 31;
		var r = 1;
		r = prime * r + ((this.result == null) ? 0 : this.result.hashCode());
		return r;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		var other = (MatchResult) obj;
		if (result == null) {
			if (other.result != null)
				return false;
			}
			else if (!result.equals(other.result))
					return false;
		return true;
	}

	@Override
	public String toString() {
		return "MatchResult [name=" + name + ", setCode=" + setCode + ", number="+ number +",scryfall="+ scryfallId +", score=" + score + "]";
	}
}
