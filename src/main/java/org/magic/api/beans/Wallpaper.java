package org.magic.api.beans;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;

import javax.imageio.ImageIO;

import org.magic.tools.URLTools;

public class Wallpaper {

	private BufferedImage picture;
	private URI url;
	private String name;
	private String format;

	public BufferedImage getPicture() throws IOException {
		if (picture == null) {
			if(url.getScheme().startsWith("http"))
				picture = URLTools.extractImage(url.toURL());
			else
				picture = ImageIO.read(url.toURL());
			
		}
		return picture;
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
		} catch (IOException e) {
			return new Dimension(0, 0);
		}
	}

}
