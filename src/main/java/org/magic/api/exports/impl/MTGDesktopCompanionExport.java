package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MTGDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;

public class MTGDesktopCompanionExport extends AbstractCardExport {


	@Override
	public STATUT getStatut() {
		return STATUT.DEPRECATED;
	}



	@Override
	public String getName() {
		return "MTGDesktopCompanion";
	}

	@Override
	public void exportDeck(MTGDeck deck, File name) throws IOException {
		deck.setDateUpdate(new Date());

		var fos = new FileOutputStream(name);
		try (var oos = new ObjectOutputStream(fos)) {
			oos.writeObject(deck);
			oos.flush();
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T read(File f) throws ClassNotFoundException, IOException {
		try (var oos = new ObjectInputStream(new FileInputStream(f))) {
			return (T) oos.readObject();
		}
	}

	@Override
	public String getFileExtension() {
		return ".deck";
	}

	@Override
	public MTGDeck importDeck(String f, String name) throws IOException {
		throw new IOException("Not Supported");
	}


	@Override
	public MTGDeck importDeckFromFile(File f) throws IOException {
		try {
			return read(f);
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		}
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(MTGDesktopCompanionExport.class.getResource("/icons/logo.png"));
	}

}
