package org.magic.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.exports.impl.JsonExport;
import org.magic.services.MagicFactory;

import com.google.gson.Gson;

import fi.iki.elonen.NanoHTTPD;

public class MTGDesktopCompanionServer  extends NanoHTTPD
{
    public MTGDesktopCompanionServer() throws IOException {
		super(8080);
		
	}
    
    @Override
    public void start() throws IOException {
    	start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }
    

	public static void main(String[] args) throws IOException {
		MagicFactory.getInstance().getEnabledProviders().init();
    		new MTGDesktopCompanionServer().start();	
    }
    
	@Override
	public Response serve(IHTTPSession session) {
		  Map<String, List<String>> parms = session.getParameters();
		  List<MagicCard> list=new ArrayList<MagicCard>();
		try {
			list = MagicFactory.getInstance().getEnabledProviders().searchCardByCriteria(parms.get("att").get(0), parms.get("search").get(0), null);
			Response resp = newFixedLengthResponse(new Gson().toJson(list));
					 resp.addHeader("Content-Type", "application/json");
			
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
			return newFixedLengthResponse("erreur");
		}
		 
	}
    
    
}