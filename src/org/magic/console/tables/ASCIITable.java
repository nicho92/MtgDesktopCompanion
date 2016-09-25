
package org.magic.console.tables;

import java.io.PrintStream;


public class ASCIITable implements IASCIITable {

	private static ASCIITable instance = null;
	private IASCIITable asciiTable = new ASCIITableImpl(System.out);
	private ASCIITable(PrintStream out) {
	}
	
	public static synchronized ASCIITable getInstance(PrintStream out) {
		if (instance == null) {
			instance = new ASCIITable(out);
		}
		return instance;
	}

	@Override
	public String getTable(String[] header, String[][] data) {
		return asciiTable.getTable(header, data);
	}

	@Override
	public String getTable(String[] header, String[][] data, int dataAlign) {
		return asciiTable.getTable(header, data, dataAlign);
	}

	@Override
	public String getTable(String[] header, int headerAlign, String[][] data, int dataAlign) {
		return asciiTable.getTable(header, headerAlign, data, dataAlign);
	}

	public void printTable(String[] header, String[][] data) {
		asciiTable.printTable(header, data);
	}

	@Override
	public void printTable(String[] header, String[][] data, int dataAlign) {
		asciiTable.printTable(header, data, dataAlign);
	}

	@Override
	public void printTable(String[] header, int headerAlign, String[][] data, int dataAlign) {
		asciiTable.printTable(header, headerAlign, data, dataAlign);
	}

	public String getTable(ASCIITableHeader[] headerObjs, String[][] data) {
		return asciiTable.getTable(headerObjs, data);
	}
	
	public void printTable(ASCIITableHeader[] headerObjs, String[][] data) {
		asciiTable.printTable(headerObjs, data);
	}

	@Override
	public String getTable(IASCIITableAware asciiTableAware) {
		return asciiTable.getTable(asciiTableAware);
	}

	@Override
	public void printTable(IASCIITableAware asciiTableAware) {
		asciiTable.printTable(asciiTableAware);
	}
	
}
