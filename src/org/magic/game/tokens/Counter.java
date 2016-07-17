package org.magic.game.tokens;

public class Counter {

	private String name;
	
	private int bonusT;
	private int bonusP;
	private int type;

	public String getName() {
		return name;
	}

	

	public void setName(String s)
	{
		this.name=s;
	}

	public void setBonusMalus(int power,int toughness)
	{
		this.bonusP=power;
		this.bonusT=toughness;
	}
	

	public int getBonusT() {
		return bonusT;
	}



	public void setBonusT(int bonusT) {
		this.bonusT = bonusT;
	}



	public int getBonusP() {
		return bonusP;
	}



	public void setBonusP(int bonusP) {
		this.bonusP = bonusP;
	}
	
	public String toString()
	{
		return bonusP+"/"+bonusT;
	}
	

	

		
	
}
