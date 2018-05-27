package org.magic.api.exports.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGControler;

public class XMageDeckExport extends AbstractCardExport {


	@Override
	public String getName() {
		return "XMage";
	}

	@Override
	public String getFileExtension() {
		return ".dck";
	}

	@Override
	public void export(MagicDeck deck, File dest) throws IOException {
		StringBuilder temp = new StringBuilder();

		temp.append("NAME: " + deck.getName() + "\n");
		for (MagicCard mc : deck.getMap().keySet()) {
			temp.append(deck.getMap().get(mc)).append(" ").append("[").append(mc.getCurrentSet().getId())
					.append(":").append(mc.getCurrentSet().getNumber()).append("]").append(" ")
					.append(mc.getName()).append("\n");
		}
		for (MagicCard mc : deck.getMapSideBoard().keySet()) {
			temp.append("SB: ").append(deck.getMapSideBoard().get(mc)).append(" ").append("[")
					.append(mc.getCurrentSet().getId()).append(":").append(mc.getCurrentSet().getNumber())
					.append("]").append(" ").append(mc.getName()).append("\n");
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
				if (!line.startsWith("NAME:")) {
					if (!line.startsWith("SB:")) {
						MagicEdition ed = new MagicEdition();
						ed.setId(line.substring(line.indexOf('[') + 1, line.indexOf(':')));
						String cardName = line.substring(line.indexOf(']') + 1, line.length()).trim();
						int qte = Integer.parseInt(line.substring(0, line.indexOf('[')).trim());
						MagicCard mc = MTGControler.getInstance().getEnabledCardsProviders()
								.searchCardByName( cardName, ed, true).get(0);
						deck.getMap().put(mc, qte);

					} else {
						line = line.replace("SB:", "").trim();
						MagicEdition ed = new MagicEdition();
						ed.setId(line.substring(line.indexOf('[') + 1, line.indexOf(':')));
						String cardName = line.substring(line.indexOf(']') + 1, line.length()).trim();
						int qte = Integer.parseInt(line.substring(0, line.indexOf('[')).trim());
						MagicCard mc = MTGControler.getInstance().getEnabledCardsProviders()
								.searchCardByName( cardName, ed, true).get(0);
						deck.getMap().put(mc, qte);
					}
				} else {
					deck.setName(line.replaceAll("NAME: ", ""));
				}

				line = read.readLine();
			}
			return deck;
		}

	}

	@Override
	public void initDefault() {
		// Nothing to do

	}

}
