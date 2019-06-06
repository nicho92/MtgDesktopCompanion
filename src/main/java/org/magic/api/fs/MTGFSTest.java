package org.magic.api.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;

import org.apache.commons.lang3.SerializationUtils;
import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGControler;

public class MTGFSTest {

	
	public static void main(String[] args) throws IOException, SQLException {
		
		
		MTGDao dao = MTGControler.getInstance().getEnabled(MTGDao.class);
		dao.init();
		
		try(FileSystem fs = new MTGFileSystem(dao))
		{
			MTGPath p = (MTGPath)fs.getPath("Collections/Library/WAR/Despark");
			
			SeekableByteChannel sbc = Files.newByteChannel(p, StandardOpenOption.READ);
			
		}
		
		
	}
}
