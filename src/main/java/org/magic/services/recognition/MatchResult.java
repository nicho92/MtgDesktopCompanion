package org.magic.services.recognition;

public class MatchResult {
	private String result;
	private String name;
	private String setCode;
	private double score;
	
	public String getSetCode() {
		return setCode;
	}
	
	public String getName() {
		return name;
	}
	
	
	public MatchResult(String result, double score) {
		super();
		this.result = result;
		this.score = score;
		String[] split = result.split("\\|");
		name = split[0];
		setCode = split[1].toUpperCase();
	}

	public int hashCode() {
		final int prime = 31;
		int r = 1;
		r = prime * r + ((this.result == null) ? 0 : this.result.hashCode());
		return r;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MatchResult other = (MatchResult) obj;
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
		return "MatchResult [name=" + name + ", setCode=" + setCode + ", score=" + score + "]";
	}
}
