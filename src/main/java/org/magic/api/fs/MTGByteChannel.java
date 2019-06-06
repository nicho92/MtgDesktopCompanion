package org.magic.api.fs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

import org.apache.commons.lang3.SerializationUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGDao;

public class MTGByteChannel implements SeekableByteChannel {

	ByteArrayOutputStream out;
	byte[] content;
	private long position;
	
	public MTGByteChannel(MTGPath path, MTGDao dao) {
		out = new ByteArrayOutputStream();
		try {
			MagicCard card = dao.listCardsFromCollection(path.getCollection(), new MagicEdition(path.getIDEdition())).stream().filter(mc->mc.getName().equals(path.getCardName())).findFirst().get();
			content = SerializationUtils.serialize(card);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
		return position;
	}

	@Override
	public SeekableByteChannel position(long newp) throws IOException {
		if(!isOpen())
			throw new IOException("Channel is closed");
		
		this.position = newp;
		return this;
	}

	@Override
	public int read(ByteBuffer dst) throws IOException {
		
		while (dst.remaining()>0)
		{
		
		}
		
		
		return 1;
			
		
	}

	@Override
	public long size() throws IOException {
		return content.length;
	}

	@Override
	public SeekableByteChannel truncate(long arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int write(ByteBuffer src) throws IOException {
		int len = src.remaining();
	    byte[] buf = new byte[len];
	    while (src.hasRemaining()) {
	      src.get(buf);
	      out.write(buf);
	    }
	    return len;
	}

}
