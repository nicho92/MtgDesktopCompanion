package org.magic.api.exports.impl;

import java.io.File;
import java.io.IOException;

import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;

public class MoxFieldCSVExport extends AbstractCardExport {

	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public void exportDeck(MagicDeck deck, File dest) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public MagicDeck importDeck(String f, String name) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "MoxField";
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
