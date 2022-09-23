package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.ExportConfiguratorPanel;
import org.magic.tools.BeanTools;
import org.magic.tools.FileTools;

public class PersonnalExport extends AbstractCardExport {

	private static final String REGEX = "REGEX";
	private String regx = "";

	@Override
	public String getFileExtension() {
		return "";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {

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

		logger.debug("Parsing with : " + regx);

		for(MagicCard mc : deck.getMainAsList())
			temp.append(BeanTools.createString(mc, regx)).append(System.lineSeparator());


		FileTools.saveFile(dest, temp.toString());
	}


	@Override
	public void exportStock(List<MagicCardStock> stock, File dest) throws IOException {
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

		logger.debug("Parsing with : " + regx);

		for(MagicCardStock mc : stock)
			temp.append(BeanTools.createString(mc, regx)).append(System.lineSeparator());


		FileTools.saveFile(dest, temp.toString());
	}

	@Override
	public Map<String, String> getDefaultAttributes() {
		return Map.of(REGEX,"");
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
		return "Personal Export";
	}

}
