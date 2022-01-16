package org.beta;

import java.io.IOException;

import org.magic.api.beans.audit.JsonQueryInfo;
import org.magic.api.exports.impl.JsonExport;
import org.magic.services.TechnicalServiceManager;

public class TEst {
	
	
	
	public static void main(String[] args) throws IOException {
		TechnicalServiceManager.inst().restore();
		var obj = TechnicalServiceManager.inst().getJsonInfo().get(0);
		var str = new JsonExport().toJson(obj);
		
		
		obj = new JsonExport().fromJson(str,JsonQueryInfo.class);
		
		System.out.println(obj.getUserAgent());
	}
}
