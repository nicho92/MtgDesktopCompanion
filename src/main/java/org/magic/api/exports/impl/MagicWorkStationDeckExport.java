package org.magic.api.exports.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.abstracts.AbstractCardExport;

public class MagicWorkStationDeckExport extends AbstractCardExport {

	@Override
	public String getFileExtension() {
		return ".mwDeck";
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		StringBuilder temp = new StringBuilder();
		temp.append("// MAIN\n");
		for (MagicCard mc : deck.getMap().keySet()) {
			temp.append("    ");
			temp.append(deck.getMap().get(mc));
			temp.append(" ");
			temp.append("[").append(mc.getCurrentSet().getId().toUpperCase()).append("]");
			temp.append(mc.getName());
			temp.append("\n");
			notify(mc);
		}
		temp.append("// Sideboard\n");
		for (MagicCard mc : deck.getMapSideBoard().keySet()) {
			temp.append("SB: ");
			temp.append(deck.getMap().get(mc));
			temp.append(" ");
			temp.append("[").append(mc.getCurrentSet().getId()).append("]");
			temp.append(mc.getName());
			temp.append("\n");
			notify(mc);
		}

		try (FileWriter out = new FileWriter(dest)) {
			out.write(temp.toString());
		}

	}

	@Override
	public MagicDeck importDeck(File f) throws IOException {
		try (BufferedReader read = new BufferedReader(new FileReader(f))) {
			MagicDeck deck = new MagicDeck();
			deck.setName(f.getName().substring(0, f.getName().indexOf('.')));

			String line = read.readLine();

			while (line != null) {
				line = line.trim();
				if (!line.startsWith("//")) {
					int qte = 0;
					MagicCard mc = null;
					notify(mc);
					// TODO line parse

					if (line.startsWith("SB"))
						deck.getMapSideBoard().put(mc, qte);
					else
						deck.getMap().put(mc, qte);

				}
				line = read.readLine();
			}
			return deck;
		}
	}



	@Override
	public String getName() {
		return "MagicWorkStation";
	}

	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}

	@Override
	public void initDefault() {
		// nothing to do

	}


}
