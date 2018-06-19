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

import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsProvider.STATUT;
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

	public void export(MagicDeck deck, File name) throws IOException {
		deck.setDateUpdate(new Date());

		FileOutputStream fos = new FileOutputStream(name);
		try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(deck);
			oos.flush();

		}
	}

	private <T> T read(File f) throws ClassNotFoundException, IOException {
		try (ObjectInputStream oos = new ObjectInputStream(new FileInputStream(f))) {
			return (T) oos.readObject();
		}
	}

	@Override
	public String getFileExtension() {
		return ".deck";
	}

	@Override
	public MagicDeck importDeck(File f) throws IOException {
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

	@Override
	public void initDefault() {
		setProperty("VERSION", "1.0");

	}

}
