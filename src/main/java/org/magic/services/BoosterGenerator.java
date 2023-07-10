package org.magic.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGBooster;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.enums.EnumExtra;
import org.magic.services.logging.MTGLogger;


public class BoosterGenerator {

	
	private List<Pair<Integer, Double>> itemWeights;
	private Map<String,List<Pair<MagicCard, Double>>> cardsSheets;
	private Map<String, Integer> boosterStructure;
	
	
	private MagicEdition set;
	private EnumExtra typeBooster;
	protected Logger logger = MTGLogger.getLogger(this.getClass());

	
	public BoosterGenerator(MagicEdition ed, EnumExtra typeBooster) {
		 itemWeights = new ArrayList<>();
		 cardsSheets = new HashMap<>();
		 boosterStructure = new HashMap<>();
		 this.set=ed;
		 this.typeBooster = typeBooster;
	}
	
	public Map<String, Integer> getBoosterStructure() {
		return boosterStructure;
	}
	
	public List<Pair<Integer, Double>> getItemWeights() {
		return itemWeights;
	}
	
	public Map<String, List<Pair<MagicCard, Double>>> getCardsSheets() {
		return cardsSheets;
	}
	
	public void addSheetNameStructure(String sheetName,Integer ratio)
	{
		boosterStructure.put(sheetName,ratio);
	}
	
	public void addItemWeight(Pair<Integer, Double> p)
	{
		itemWeights.add(p);
	}
	
	public void addCardToSheet(String sheetName,Pair<MagicCard, Double> p)
	{
		cardsSheets.compute(sheetName, (k, v) ->v != null ? v : new ArrayList<>()).add(p);
	}
	
	public List<Integer> randomBoosterStructure(int qty) throws NullPointerException
	{
		if(itemWeights.isEmpty())
			throw new NullPointerException("No booster found for " + set.getId() + " / " + typeBooster.getMtgjsonname());
		
		logger.info("generate {} random booster with ponderation= {}",qty,  itemWeights);
		
		
		return Arrays.asList(new EnumeratedDistribution<>(itemWeights).sample(qty,new Integer[qty]));
	}

	public MTGBooster generateBooster(int id) {
		var booster = new MTGBooster();
		  booster.setEdition(set);
		  booster.setTypeBooster(typeBooster);
		  booster.setBoosterNumber(""+id);
		 for(var e : boosterStructure.entrySet()){
			 var picker = new EnumeratedDistribution<>(getCardsSheets().get(e.getKey())).sample(e.getValue(), new MagicCard[e.getValue()]);
			 booster.getCards().addAll(Arrays.asList(picker));
		 }
		 return booster;
	}
	
	
	
	
	
}
