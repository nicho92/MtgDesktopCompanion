package org.magic.tests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import org.magic.api.beans.MagicDeck;
import org.magic.api.dao.impl.HsqlDAO;
import org.magic.api.dao.impl.MysqlDAO;
import org.magic.api.exports.impl.CSVExport;
import org.magic.api.exports.impl.MTGDesktopCompanionExport;
import org.magic.api.exports.impl.MTGODeckExport;
import org.magic.api.interfaces.CardExporter;
import org.magic.api.interfaces.MagicDAO;

public class SearchTest {

	
	public static void main(String[] args) throws Exception {
		
		CardExporter exp = new MTGODeckExport();
		MagicDeck deck = exp.importDeck(new File("c:/Jeskai Mentor.dec"));
		deck.setName("Test");
		MTGDesktopCompanionExport saver = new MTGDesktopCompanionExport();
		
		saver.export(deck, new File("c:/test.deck"));
		
		
	}
	
	
}
