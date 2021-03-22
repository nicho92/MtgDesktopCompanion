package org.beta;

import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.ExportConfiguratorPanel;
import org.magic.tools.BeanTools;
import org.magic.tools.FileTools;

public class PersonnalExport extends AbstractCardExport {
	
	private String regx = "";
	
	@Override
	public String getFileExtension() {
		return "";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		
		ExportConfiguratorPanel panel = new ExportConfiguratorPanel();

		JDialog d = MTGUIComponent.createJDialog(panel,true,true);
		
		panel.getBtnExport().addActionListener(al->{
			regx = panel.getResult();
			d.dispose();
		});
		
		
		d.setVisible(true);
		
		StringBuilder temp = new StringBuilder();
		
		logger.debug("Parsing with : " + regx);
		
		for(MagicCard mc : deck.getMainAsList())
			temp.append(BeanTools.createString(mc, regx)).append(System.lineSeparator());
		
		
		FileTools.saveFile(dest, temp.toString());
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
