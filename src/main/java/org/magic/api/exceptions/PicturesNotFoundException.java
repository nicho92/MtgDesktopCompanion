package org.magic.api.exceptions;

import java.io.FileNotFoundException;

public class PicturesNotFoundException extends Exception {

	public PicturesNotFoundException() {
		super();
	}
	
	public PicturesNotFoundException(String message)
	{
		super(message);
	}

	public PicturesNotFoundException(FileNotFoundException e) {
		super(e);
	}
	
	
	
}
