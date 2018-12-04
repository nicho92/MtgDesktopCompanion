package org.beta;

import java.io.IOException;
import java.util.List;

import org.magic.api.beans.OrderEntry;
import org.magic.api.interfaces.abstracts.AbstractMagicShopper;

public class MagicBazarShopper extends AbstractMagicShopper {

	
	String urlLogin = "https://www.magicbazar.fr/user/signin.php";
	
	
	@Override
	public List<OrderEntry> listOrders() throws IOException {
		return null;
	}

	@Override
	public String getName() {
		return "MagicBazar";
	}
	
	
	@Override
	public void initDefault() {
		setProperty("LOGIN", "");
		setProperty("PASSWORD", "");
	}

}
