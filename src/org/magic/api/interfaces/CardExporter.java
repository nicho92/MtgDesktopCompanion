package org.magic.api.interfaces;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.Icon;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;

public interface CardExporter {

	public String getFileExtension();

	public void export(MagicDeck deck, File dest) throws IOException;
	public MagicDeck importDeck(File f) throws Exception;
	public void export(List<MagicCard> cards, File f) throws Exception;
	
	public String getName();

	public void enable(boolean boolean1);

	public boolean isEnable();

	public Icon getIcon();

}