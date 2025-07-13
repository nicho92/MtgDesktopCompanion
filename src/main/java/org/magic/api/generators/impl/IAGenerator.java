package org.magic.api.generators.impl;

import java.io.IOException;

import javax.swing.Icon;

import org.magic.api.interfaces.MTGIA;
import org.magic.api.interfaces.abstracts.AbstractMTGTextGenerator;
import org.magic.services.MTGConstants;
import org.magic.services.tools.MTG;

public class IAGenerator extends AbstractMTGTextGenerator{

	@Override
	public String generateText() {
		try {
			return MTG.getEnabledPlugin(MTGIA.class).generateRandomCard(null, null, null).getText();
		} catch (IOException e) {
			return "";
		}
	}

	@Override
	public String[] suggestWords(String[] start) {
		return new String[0];
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return "IA";
	}
	
	@Override
	public Icon getIcon() {
		return MTGConstants.ICON_IA;
	}
	

}
