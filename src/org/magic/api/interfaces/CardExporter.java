package org.magic.api.interfaces;

import java.io.File;
import java.util.List;

import javax.swing.Icon;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.MagicDeck;

public interface CardExporter extends MTGPlugin {

	public String getFileExtension();

	public void export(MagicDeck deck, File dest) throws Exception;
	public MagicDeck importDeck(File f) throws Exception;
	
	public void export(List<MagicCard> cards, File f) throws Exception;
	
	public void exportStock(List<MagicCardStock> stock,File f)throws Exception;
	public List<MagicCardStock> importStock(File f)throws Exception;
	
	public Icon getIcon();

}