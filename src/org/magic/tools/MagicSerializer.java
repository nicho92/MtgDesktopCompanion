package org.magic.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.magic.api.beans.MagicDeck;

import com.google.gson.Gson;

public class MagicSerializer {

	
	
	public static void serialize(MagicDeck deck,String name) throws Exception
	{
		FileOutputStream fos = new FileOutputStream(name);
		
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(deck);
		oos.flush();
		oos.close();
		
		File f = new File(name+".json");
		Gson GSON = new Gson();
		FileWriter fw = new FileWriter(f.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(GSON.toJson(deck));
		bw.close();
		
		
	}

	public static <T> T read(File f, Class<T> class1) throws Exception {
		FileInputStream fos = new FileInputStream(f);
		ObjectInputStream oos = new ObjectInputStream(fos);
		T bean = (T)oos.readObject();
		oos.close();
		return bean;
		/*return new Gson().fromJson(new FileReader(f), class1);*/
	}
	
}
