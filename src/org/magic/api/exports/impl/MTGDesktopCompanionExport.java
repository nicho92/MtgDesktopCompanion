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
import org.magic.api.interfaces.abstracts.AbstractCardExport;
import org.magic.services.MagicFactory;

public class MTGDesktopCompanionExport extends AbstractCardExport  {

	@Override
	public String getName() {
		return "MTGDesktopCompanion";
	}
	
	public MTGDesktopCompanionExport() {
		super();
		if(!new File(MagicFactory.CONF_DIR, "exp-"+getName()+".conf").exists()){
			props.put("VERSION", "1.0");
			save();
		}
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
