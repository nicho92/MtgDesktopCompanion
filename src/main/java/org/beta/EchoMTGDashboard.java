package org.beta;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.magic.api.beans.CardDominance;
import org.magic.api.beans.CardPriceVariations;
import org.magic.api.beans.CardShake;
import org.magic.api.beans.EditionPriceVariations;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicFormat.FORMATS;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.api.interfaces.abstracts.AbstractDashBoard;
import org.magic.tools.RequestBuilder;
import org.magic.tools.RequestBuilder.METHOD;

import com.google.gson.JsonElement;

import org.magic.tools.URLTools;

public class EchoMTGDashboard extends AbstractCardExport {

	private String authToken=null;
	
	
	@Override
	public void initDefault() {
		setProperty("EMAIL", "you@mail.com");
		setProperty("PASS", "");
	}
	
	public static void main(String[] args) throws IOException {
		
		new EchoMTGDashboard().connect();
		
	}
	
	
	private void connect() throws IOException
	{
		JsonElement con = RequestBuilder.build().method(METHOD.POST)
				 .url("https://www.echomtg.com/api/user/auth/")
				 .addContent("email", getString("EMAIL"))
				 .addContent("password", getString("PASS"))
				 .setClient(URLTools.newClient())
				 .toJson();
		
		authToken=con.getAsJsonObject().get("token").getAsString();
		
	}

	@Override
	public String getFileExtension() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "EchoMTG";
	}
	
	

}
