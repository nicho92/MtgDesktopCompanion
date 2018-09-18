package org.magic.tools;

import java.awt.Color;

import javax.swing.JTable;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

public class UITools {

	public static void initTableFilter(JTable table)
	{
		TableFilterHeader filterHeader = new TableFilterHeader(table, AutoChoices.ENABLED);
		filterHeader.setSelectionBackground(Color.LIGHT_GRAY);
	}
	
	
}
