
package org.magic.console.tables;

import org.magic.console.tables.ASCIITableHeader;

public interface IASCIITable {
	
	public static final int ALIGN_LEFT = -1;
	public static final int ALIGN_CENTER = 0;
	public static final int ALIGN_RIGHT = 1;
	
	public static final int DEFAULT_HEADER_ALIGN = ALIGN_CENTER;
	public static final int DEFAULT_DATA_ALIGN = ALIGN_RIGHT;
	
	/**
	 * Prints the ASCII table to console.
	 * 
	 * @param header
	 * @param data
	 */
	public void printTable(String[] header, String[][] data);
	public void printTable(String[] header, String[][] data, int dataAlign);
	public void printTable(String[] header, int headerAlign, String[][] data, int dataAlign);
	public void printTable(ASCIITableHeader[] headerObjs, String[][] data);
	public void printTable(IASCIITableAware asciiTableAware);
	
	/**
	 * Returns the ASCII table as string which can be rendered in console or JSP.
	 * 
	 * @param header
	 * @param data
	 * @return
	 */
	public String getTable(String[] header, String[][] data);
	public String getTable(String[] header, String[][] data, int dataAlign);
	public String getTable(String[] header, int headerAlign, String[][] data, int dataAlign);
	public String getTable(ASCIITableHeader[] headerObjs, String[][] data);
	public String getTable(IASCIITableAware asciiTableAware);
	
}
