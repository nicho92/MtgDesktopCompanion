package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.NotImplementedException;
import org.beta.WebcamCardImportComponent;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
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
		
		MTGUIComponent.createJDialog(c, true, true).setVisible(true);
		
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
		MTGUIComponent.createJDialog(c, true, true).setVisible(true);
		MagicDeck st = new MagicDeck();
		c.getFindedCards().stream().forEach(st::add);
		return st;
		
	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		return importDeckFromFile(null);
	}

}
