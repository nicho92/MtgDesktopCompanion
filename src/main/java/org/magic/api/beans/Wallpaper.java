package org.magic.api.beans;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class Wallpaper {

	private BufferedImage picture;
	private URL url;
	private String name;
	
	
	public BufferedImage getPicture() throws IOException {
		if(picture==null)
			picture=ImageIO.read(url);
		
		return picture;
	}
	public void setPicture(BufferedImage picture) {
		this.picture = picture;
	}
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
	
	
}
