package org.magic.api.exports.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.CardExporter;

public class MTGDesktopCompanionExport implements CardExporter  {

private boolean enable;
	
	@Override
	public boolean isEnable() {
		return enable;
	}


	@Override
	public void enable(boolean b) {
		this.enable=b;
		
	}
	
	@Override
	public String getName() {
		return "MTGDesktopCompanion";
	}
	
	public void export(MagicDeck deck, File name) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(name);
		
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(deck);
		oos.flush();
		oos.close();
	}

	private <T> T read(File f, Class<T> class1) throws Exception {
		FileInputStream fos = new FileInputStream(f);
		ObjectInputStream oos = new ObjectInputStream(fos);
		T bean = (T)oos.readObject();
		oos.close();
		return bean;
		/*return new Gson().fromJson(new FileReader(f), class1);*/
	}

	@Override
	public String getFileExtension() {
		return ".deck";
	}

	@Override
	public MagicDeck importDeck(File f) throws Exception {
		return read(f, MagicDeck.class);
	}


	@Override
	public void export(List<MagicCard> cards, File f) throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Icon getIcon() {
		return new ImageIcon(MTGDesktopCompanionExport.class.getResource("/res/logo.gif"));
	}


}
