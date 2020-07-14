package org.beta;

import java.io.File;

import org.apache.metamodel.DataContext;
import org.apache.metamodel.json.JsonDataContext;
import org.apache.metamodel.query.builder.SatisfiedSelectBuilder;
import org.magic.services.MTGConstants;

public class QueryTester {

	public static void main(String[] args) {
		DataContext dc = new JsonDataContext(new File(MTGConstants.DATA_DIR, "AllSets-x5.json"));
		
		dc.getDefaultSchema().getTableByName("AllSets-x5.json").getColumnNames().forEach(System.out::println);
		
		SatisfiedSelectBuilder<?> dataSet = dc.query().from("AllSets-x5.json").select("data");
		
		System.out.println(dataSet.toString());
		
		
	}

}
