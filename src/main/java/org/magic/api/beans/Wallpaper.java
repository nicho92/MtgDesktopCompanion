package org.magic.api.beans;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;

import org.magic.services.network.URLTools;
import org.magic.services.tools.ImageTools;

public class Wallpaper {

	private BufferedImage picture;
	private URI url;
	private String name;
	private String format;

	public BufferedImage getPicture() {
		return picture;
	}


	public Wallpaper load() throws IOException
	{
		if (getPicture() == null) {
			if(getUrl().getScheme().startsWith("http"))
				setPicture(URLTools.extractAsImage(getUrl().toASCIIString()));
			else
				setPicture(ImageTools.readLocal(getUrl().toURL()));
		}

		return this;
	}


	public void setPicture(BufferedImage picture) {
		this.picture = picture;
	}

	public URI getUrl() {
		return url;
	}

	public void setUrl(URI url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String extension) {
		this.format = extension;

	}

	public Dimension getDimension() {
		try {
			return new Dimension(getPicture().getWidth(), getPicture().getHeight());
		} catch (Exception e) {
			return new Dimension(0, 0);
		}
	}

}
