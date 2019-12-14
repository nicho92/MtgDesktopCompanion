package org.magic.api.exports.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicDeck;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MTGControler;

public class CSVExport extends AbstractCardExport {


	@Override
	public String getName() {
		return "CSV";
	}

	@Override
	public List<MagicCardStock> importStockFromFile(File f) throws IOException {
		try (BufferedReader read = new BufferedReader(new FileReader(f))) {
			List<MagicCardStock> stock = new ArrayList<>();
			String line = read.readLine();

			line = read.readLine();
			while (line != null) {
				String[] part = line.split(";");
				MagicCardStock mcs = new MagicCardStock();
				MagicCard mc = MTGControler.getInstance().getEnabled(MTGCardsProvider.class)
						.searchCardByName( part[1], null, true).get(0);

				for (MagicEdition ed : mc.getEditions())
					if (ed.getSet().equals(part[2])) {
						mc.getEditions().add(0, ed);
						break;
					}

				mcs.setMagicCard(mc);
				mcs.setLanguage(part[3]);
				mcs.setQte(Integer.parseInt(part[4]));
				mcs.setCondition(EnumCondition.valueOf(part[5]));
				mcs.setFoil(Boolean.valueOf(part[6]));
				mcs.setAltered(Boolean.valueOf(part[7]));
				mcs.setSigned(Boolean.valueOf(part[8]));
				mcs.setMagicCollection(new MagicCollection(part[9]));
				mcs.setPrice(Double.valueOf(part[10]));
				try {
					mcs.setComment(part[11]);
				} catch (ArrayIndexOutOfBoundsException aioob) {
					mcs.setComment("");
				}
				mcs.setIdstock(-1);
				mcs.setUpdate(true);
				stock.add(mcs);
				line = read.readLine();

			}
			return stock;
		}

	}

	@Override
	public void exportStock(List<MagicCardStock> stock, File f) throws IOException {

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
			bw.write("id;Card Name;Edition;Language;Qte;Condition;Foil;Altered;Signed;Collection;Price;Comment\n");
			for (MagicCardStock mcs : stock) {
				bw.write(mcs.getIdstock() + ";");
				bw.write(mcs.getMagicCard().getName() + ";");
				bw.write(mcs.getMagicCard().getCurrentSet() + ";");
				bw.write(mcs.getLanguage() + ";");
				bw.write(mcs.getQte() + ";");
				bw.write(mcs.getCondition() + ";");

				bw.write(mcs.isFoil() + ";");
				bw.write(mcs.isAltered() + ";");
				bw.write(mcs.isSigned() + ";");

				bw.write(mcs.getMagicCollection() + ";");
				bw.write(mcs.getPrice() + ";");
				bw.write(mcs.getComment() + ";");
				bw.write("\n");
				notify(mcs);
			}
		}

	}

	@Override
	public void export(MagicDeck deck, File f) throws IOException {

		String[] exportedDeckProperties= getArray("exportedDeckProperties");

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {

			bw.write("Main list\n");

			bw.write("Qte;");
			for (String k : exportedDeckProperties)
				bw.write(k + ";");

			bw.write("\n");

			for (MagicCard mc : deck.getMap().keySet()) {
				bw.write(deck.getMap().get(mc) + ";");
				for (String k : exportedDeckProperties) {
					String val = null;
					try {
						val = BeanUtils.getProperty(mc, k);
					} catch (Exception e) {
						logger.error("Error reading bean", e);
					}
					if (val == null)
						val = "";
					bw.write(val.replaceAll("\n", "") + ";");
				}
				bw.write("\n");
				notify(mc);
			}

			bw.write("SideBoard\n");
			bw.write("Qte;");
			for (String k : exportedDeckProperties)
				bw.write(k + ";");

			bw.write("\n");
			for (MagicCard mc : deck.getMapSideBoard().keySet()) {
				bw.write(deck.getMapSideBoard().get(mc) + ";");
				for (String k : exportedDeckProperties) {
					String val = null;
					try {
						val = BeanUtils.getProperty(mc, k);
					} catch (Exception e) {
						logger.error("Error reading bean ", e);
					}
					if (val == null)
						val = "";
					bw.write(val.replaceAll("\n", "") + ";");
				}
				bw.write("\n");
				notify(mc);
			}

		}
	}

	@Override
	public String getFileExtension() {
		return ".csv";
	}

	@Override
	public MagicDeck importDeck(String s,String n) throws IOException {
		try (BufferedReader read = new BufferedReader(new StringReader(s))) {
			MagicDeck deck = new MagicDeck();
			deck.setName(n);

			String line = read.readLine();
			line = read.readLine();
			while (line != null) {
				String[] part = line.split(getString("importDeckCharSeparator"));
				String name = part[0];
				String qte = part[1];
				String set = part[2];

				MagicEdition ed = new MagicEdition(set);
				List<MagicCard> list = MTGControler.getInstance().getEnabled(MTGCardsProvider.class).searchCardByName(name, ed, true);
				notify(list.get(0));
				deck.getMap().put(list.get(0), Integer.parseInt(qte));
				line = read.readLine();
			}
			return deck;
		}
	}


	@Override
	public void initDefault() {
		setProperty("exportedProperties", "number,name,cost,supertypes,types,subtypes,editions");
		setProperty("exportedDeckProperties", "name,cost,supertypes,types,subtypes,editions[0].id");
		setProperty("importDeckCharSeparator", ";");

	}


}
