package org.beta;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.beta.fs.MTGFileSystem;

public class MTGFSTest {

	
	public static void main(String[] args) throws IOException {
		
		try(FileSystem fs = new MTGFileSystem())
		{

			Path cols = fs.getPath("/");
			
			Files.list(cols).forEach(p->{
				System.out.println(p);
			});
			
		}
		
		
	}
}
