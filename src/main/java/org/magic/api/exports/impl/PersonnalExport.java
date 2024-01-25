package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MTGCard;
import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.tech.ExportConfiguratorPanel;
import org.magic.services.tools.BeanTools;
import org.magic.services.tools.FileTools;

public class PersonnalExport extends AbstractCardExport {

	private static final String REGEX = "REGEX";
	private String regx = "";

	@Override
	public String getFileExtension() {
		return "";
	}

	@Override
	public void exportDeck(MTGDeck deck, File dest) throws IOException {

		var panel = new ExportConfiguratorPanel();
		panel.setRegex(getString(REGEX));
		panel.initTree(deck.getMainAsList().get(0));

		var d = MTGUIComponent.createJDialog(panel,true,true);

		panel.getBtnExport().addActionListener(al->{
			regx = panel.getResult();
			setProperty(REGEX,regx);
			d.dispose();
		});


		d.setVisible(true);

		var temp = new StringBuilder();

		logger.debug("Parsing with : {}",regx);

		for(MTGCard mc : deck.getMainAsList())
			temp.append(BeanTools.createString(mc, regx)).append(System.lineSeparator());


		FileTools.saveFile(dest, temp.toString());
	}


	@Override
	public void exportStock(List<MTGCardStock> stock, File dest) throws IOException {
		var panel = new ExportConfiguratorPanel();
		panel.initTree(stock.get(0));
		panel.setRegex(getString(REGEX));
		var d = MTGUIComponent.createJDialog(panel,true,true);

		panel.getBtnExport().addActionListener(al->{
			regx = panel.getResult();
			setProperty(REGEX,regx);
			d.dispose();
		});


		d.setVisible(true);

		var temp = new StringBuilder();

		logger.debug("Parsing with : {}",regx);

		for(MTGCardStock mc : stock)
			temp.append(BeanTools.createString(mc, regx)).append(System.lineSeparator());


		FileTools.saveFile(dest, temp.toString());
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of(REGEX,"");
	}


	@Override
	public MTGDeck importDeck(String f, String name) throws IOException {
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
		return "Personal Export";
	}

}
