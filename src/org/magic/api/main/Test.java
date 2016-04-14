package org.magic.api.main;

import java.io.File;

public class Test {

	public static void main(String[] args) {
		File dir = new File("D:\\programmation\\GIT\\MtgDesktopCompanion\\src\\res\\set\\icons");
		
		
		for(File f : dir.listFiles())
		{
			String name = f.getName().substring(0,f.getName().indexOf("."))+"_set.png";
			f.renameTo(new File(dir,name));
		}
	}

}
