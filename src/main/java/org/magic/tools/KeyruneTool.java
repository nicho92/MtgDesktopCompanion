package org.magic.tools;

import java.io.File;

public class KeyruneTool {

	public static void main(String[] args) {
		File dir = new File("D:\\Téléchargements\\icomoon_216_icons\\PNG");
		
		
		for(File f : dir.listFiles())
		{
			String name = f.getName().substring(0, f.getName().indexOf(".")).toUpperCase()+"_set.png";
			f.renameTo(new File(dir,name));
		}
	}
}
