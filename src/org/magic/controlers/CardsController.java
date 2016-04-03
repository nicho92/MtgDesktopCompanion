package org.magic.controlers;

public class CardsController {

	private static CardsController instance;
	
	
	
	public static CardsController getInstance(){
		if(instance==null)
			instance = new CardsController();
		
		return instance;
	}
	
	
	private CardsController()
	{
		
	}
	
	
}
