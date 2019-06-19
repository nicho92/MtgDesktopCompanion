package test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import org.junit.Test;
import org.magic.api.beans.MagicCard;
import org.magic.api.fs.MTGFileSystem;
import org.magic.api.fs.MTGPath;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGControler;


public class MTGFSTest {

	@Test
	public void testFS() throws IOException, SQLException {
		
		
		MTGDao dao = MTGControler.getInstance().getEnabled(MTGDao.class);
		dao.init();
		
		try(MTGFileSystem fs = new MTGFileSystem(dao))
		{
			MTGPath pth = (MTGPath)fs.getPath("Collections/Library/WAR/Despark");
			
			try
			{
				byte[] b = Files.readAllBytes(pth);
				System.out.println(Files.exists(pth));
				
				MagicCard card = fs.getSerializer().fromJson(new String(b), MagicCard.class);
				System.out.println(card);
				
				Path c = Files.createDirectory(new MTGPath(fs, "Collections", "Test"));
				
				Files.write(c.resolve("Despark 2"),Files.readAllBytes(pth));
				
				Files.list(c).forEach(p->{
					System.out.println(p);
				});
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		
		
	}
}
