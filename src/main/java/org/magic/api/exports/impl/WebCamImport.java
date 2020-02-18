package org.magic.api.exports.impl;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.NotImplementedException;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.gui.components.WebcamCardImportComponent;
import org.magic.services.MTGControler;

public class WebCamImport extends AbstractCardExport {

	
	@Override
	public MODS getMods() {
		return MODS.IMPORT;
	}
	
	@Override
	public boolean needFile() {
		return false;
	}
	
	@Override
	public boolean needDialogGUI() {
		return true;
	}
	
	@Override
	public String getFileExtension() {
		return ".dat";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		throw new NotImplementedException("Not Implemented");

	}
	
	@Override
	public List<MagicCardStock> importStockFromFile(File f) throws IOException {
		
		WebcamCardImportComponent c = new WebcamCardImportComponent();
		
		c.setVisible(true);
		
		
		return c.getFindedCards().stream().map(card->{
			MagicCardStock st = MTGControler.getInstance().getDefaultStock();
			st.setMagicCard(card);
			return st;
		}).collect(Collectors.toList());
	}
	

	@Override
	public String getName() {
		return "Webcam";
	}

	@Override
	public MagicDeck importDeckFromFile(File f) throws IOException {
		WebcamCardImportComponent c = new WebcamCardImportComponent();
		c.setVisible(true);
		
		return c.getSelectedDeck();
		
	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		return importDeckFromFile(null);
	}

}
