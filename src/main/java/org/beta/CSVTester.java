package org.beta;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class CSVTester {

	public static void main(String[] args) throws IOException {
		Reader in = new FileReader("C:\\Users\\Pihen\\Downloads\\export.csv");
		
		CSVFormat  format = CSVFormat.EXCEL.builder()
												.setDelimiter(";")
												.setHeader()
												.setIgnoreEmptyLines(true)
												.setNullString("")
												.build();
		
		
		
		Iterable<CSVRecord> records = format.parse(in);
		for (CSVRecord record : records) {
		    String lastName = record.get("extendedArt");
		    System.out.println(lastName);
		}
	}
}
