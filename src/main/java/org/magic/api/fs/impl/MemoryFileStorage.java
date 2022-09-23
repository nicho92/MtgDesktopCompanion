package org.magic.api.fs.impl;

import java.io.IOException;

import org.magic.api.interfaces.abstracts.AbstractFileStorage;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;

public class MemoryFileStorage extends AbstractFileStorage
{

	@Override
	public void initFileSystem() throws IOException {
		fs= MemoryFileSystemBuilder.newEmpty().build();
	}

	@Override
	public String getName() {
		return "Memory";
	}




}
