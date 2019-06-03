package org.beta;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.sql.SQLException;

import org.magic.api.interfaces.MTGDao;
import org.magic.services.MTGControler;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class VirtualFSTEst {
	public static void main(String[] args) throws IOException, SQLException, InterruptedException {
		FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
		Path data = fs.getPath("/collections");
		Files.createDirectory(data);
 
		WatchService watcher = data.getFileSystem().newWatchService();
		Thread watcherThread = new Thread(() -> {
			WatchKey key;
			try {
				key = watcher.take();
				while (key != null) {
					for (WatchEvent<?> event : key.pollEvents()) {
						System.out.printf("event of type: %s received for file: %s\n", event.kind(), event.context());
					}
					key.reset();
					key = watcher.take();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, "CustomWatcher");
		
		
		watcherThread.start();
		data.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
 
		
		MTGDao dao = MTGControler.getInstance().getEnabled(MTGDao.class);
		dao.init();
		dao.listCollections().forEach(mc->{
			Path hello = data.resolve(mc.getName()); 
			try {
				Files.createDirectory(hello);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
	}
}
