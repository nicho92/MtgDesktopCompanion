package org.beta;

import java.io.IOException;
import java.util.Base64;

import org.magic.tools.RequestBuilder;
import org.magic.tools.URLTools;
import org.magic.tools.RequestBuilder.METHOD;

public class MtgGatheringPricer {

	public static void main(String[] args) throws IOException {
		String url ="https://api.wpengineapi.com/v1";
		
		String authString = "Nick:s7oWoGJH#9kwq4I6Wf^d09v@";
		authString="c82c1392-4dc7-4f4b-b44e-675341a81634:KUynEBdUxhn0ho06IAlwLw==";
		
		byte[] authEncBytes = Base64.getEncoder().encode(authString.getBytes());
		String ret = RequestBuilder.build().setClient(URLTools.newClient()).url(url).method(METHOD.GET).addHeader("Authorization", "Basic "+new String(authEncBytes)).execute();		
		System.out.println(ret);
	}
}
