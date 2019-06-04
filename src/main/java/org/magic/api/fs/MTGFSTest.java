package org.magic.api.fs;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGControler;

public class MTGFSTest {

	
	public static void main(String[] args) throws IOException, SQLException {
		
		
		MTGDao dao = MTGControler.getInstance().getEnabled(MTGDao.class);
		dao.init();
		
		try(FileSystem fs = new MTGFileSystem(dao))
		{
			Path root = fs.getPath("/");
			Files.list(root).forEach(p->{
				System.out.println(p.toFile());
			});
		}
		
		
	}
}
