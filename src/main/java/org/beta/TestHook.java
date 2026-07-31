package org.beta;

import org.apache.http.entity.StringEntity;
import org.magic.services.network.URLTools;

public class TestHook {

    public static void main(String[] args) throws Exception {
	
	System.setProperty( "javax.net.ssl.trustStore", "C:\\Users\\nicol\\.magicDeskCompanion\\data\\jetty.jks" );
	System.setProperty( "javax.net.ssl.trustStorePassword", "changeit" );
	
	String content = "{\"data\":{\"changes\":[{\"after\":2,\"before\":1,\"customId\":null,\"inventoryId\":\"69d3e15c3e676d07d4cd24ed\",\"productId\":75886}],\"reason\":\"edit\"},\"eventId\":\"8688a50bedbd5072-0\",\"timestamp\":\"2026-07-31T07:39:42.158Z\",\"type\":\"inventory.quantity.changed\"}";
	URLTools.newClient().doPost("http://localhost:8080/webhook", new StringEntity(content), null);
    }
    
    
}
