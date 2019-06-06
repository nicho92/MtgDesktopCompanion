package org.magic.api.fs;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.sql.SQLException;

import org.magic.api.beans.MagicCard;
import org.magic.api.exports.impl.JsonExport;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGControler;


public class MTGFSTest {

	
	public static void main(String[] args) throws IOException, SQLException {
		
		
		MTGDao dao = MTGControler.getInstance().getEnabled(MTGDao.class);
		dao.init();
		
		try(FileSystem fs = new MTGFileSystem(dao))
		{
			MTGPath p = (MTGPath)fs.getPath("Collections/Library/WAR/Despark");
			
			try
			{
				byte[] b = Files.readAllBytes(p);
				String mc = new String(b);
				System.out.println(mc);
				MagicCard c = new JsonExport().fromJson(mc,MagicCard.class);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		
		
	}
}
