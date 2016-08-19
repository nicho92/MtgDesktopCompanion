package org.magic.exports.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.DeckExporter;

public class SerializerDeckExport implements DeckExporter  {

	
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

	public static <T> T read(File f, Class<T> class1) throws Exception {
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


}
