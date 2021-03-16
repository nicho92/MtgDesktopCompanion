package org.beta;

import java.io.File;
import java.io.IOException;

import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.ExportConfiguratorPanel;
import org.magic.tools.FileTools;

public class PersonnalExport extends AbstractCardExport {

	@Override
	public String getFileExtension() {
		return "";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		
		ExportConfiguratorPanel panel = new ExportConfiguratorPanel();
		
		panel.initTree(deck);
		
		MTGUIComponent.createJDialog(panel,true,true).setVisible(true);
		
		
		FileTools.saveFile(dest, panel.getResult());
	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		return null;
	}
	
	@Override
	public MODS getMods() {
		return MODS.EXPORT;
	}
	
	@Override
	public boolean needDialogForDeck(MODS mod) {
		return true;
	}
	
	@Override
	public boolean needDialogForStock(MODS mod) {
		return true;
	}
	

	@Override
	public String getName() {
		return "Parametrized Export";
	}

}
