package org.magic.server;

import java.io.IOException;
import java.util.Map;

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
    		new MTGDesktopCompanionServer().start();	
    }
    
	@Override
	public Response serve(IHTTPSession session) {
		  String msg = "<html><body><h1>Hello server</h1>\n";
          Map<String, String> parms = session.getParms();
          if (parms.get("att") == null) {
              msg += "<form action='?' method='get'>\n  <p>search: <input type='text' name='name'></p>\n" + "</form>\n";
          } else {
              msg += "<p>Hello, " + parms.get("username") + "!</p>";
          }
          return newFixedLengthResponse(msg + "</body></html>\n");
	}
    
    
}