package org.magic.api.fs.impl;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.magic.api.beans.technical.MTGProperty;
import org.magic.api.interfaces.abstracts.AbstractFileStorage;
import org.magic.services.MTGConstants;

public class ZipFileSystemStorage extends AbstractFileStorage {




	@Override
	public void initFileSystem() throws IOException {
		Map<String, Object> env = new HashMap<>();
		  env.put("create", "true");
		fs = FileSystems.newFileSystem(getFile("ROOT").toPath(),env);
	}

	@Override
	public String getName() {
		return "Zip";
	}




	@Override
	public Map<String, MTGProperty> getDefaultAttributes() {
		return Map.of("ROOT", MTGProperty.newFileProperty(Paths.get(MTGConstants.DATA_DIR.getAbsolutePath(), "ged.zip")));
	}



	@Override
	public void unload() {

		try {
			if(getFilesSystem()==null)
				return;

			if(getFilesSystem().isOpen())
				getFilesSystem().close();
		} catch (IOException e) {
			logger.error("Error closing fs",e);
		}

	}
}
