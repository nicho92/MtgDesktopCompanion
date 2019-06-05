package org.magic.api.fs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

public class MTGByteChannel implements SeekableByteChannel {

	ByteArrayOutputStream out;
	
	
	public MTGByteChannel(MTGPath path) {
		out = new ByteArrayOutputStream();
		
		
	}

	@Override
	public void close() throws IOException {
		out.close();

	}

	@Override
	public boolean isOpen() {
		return out!=null;
	}

	@Override
	public long position() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SeekableByteChannel position(long arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int read(ByteBuffer bb) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long size() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SeekableByteChannel truncate(long arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int write(ByteBuffer arg0) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

}
