package org.magic.api.exports.impl;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MTGDeck;
import org.magic.api.beans.enums.EnumExportCategory;
import org.magic.api.interfaces.abstracts.AbstractCardExport;

public class SystemClipBoardExport extends AbstractCardExport {

	
	Clipboard clipboard;
	
	@Override
	public String getFileExtension() {
		return "";
	}

	@Override
	public EnumExportCategory getCategory() {
		return EnumExportCategory.MANUAL;
	}

	@Override
	public boolean needDialogForDeck(MODS mod) {
		return false;
	}

	@Override
	public boolean needDialogForStock(MODS mod) {
		return false;
	}


	@Override
	public boolean needFile() {
		return false;
	}

	@Override
	public MODS getMods() {
		return MODS.BOTH;
	}

	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {
		clipboard=Toolkit.getDefaultToolkit().getSystemClipboard();
		
		var strse1 = new StringSelection(deck.getMain().entrySet().stream().map(e->e.getValue() + " " + e.getKey()).collect(Collectors.joining(System.lineSeparator())));
	    clipboard.setContents(strse1, strse1);

	}

	@Override
	public MTGDeck importDeck(String f, String name) throws IOException {
		var d = new MTGDeck();
		d.setName("ClipBoard");
		clipboard=Toolkit.getDefaultToolkit().getSystemClipboard();
		try {
			var data = clipboard.getData(DataFlavor.stringFlavor).toString();
			
			MTGODeckExport expo = new MTGODeckExport();
			return expo.importDeck(data, "ClipBoard");
			
			
		} catch (UnsupportedFlavorException e) {
			throw new IOException(e);
		}
	}

	@Override
	public String getName() {
		return "System ClipBoard";
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(SystemClipBoardExport.class.getResource("/icons/plugins/clipboard.png"));
	}


}
