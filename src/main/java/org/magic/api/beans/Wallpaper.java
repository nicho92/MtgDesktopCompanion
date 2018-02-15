package org.magic.api.beans;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class Wallpaper {

	private BufferedImage picture;
	private URL url;
	private String name;
	private String format;
	
	
	public BufferedImage getPicture() throws IOException {
		if(picture==null)
		{
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("User-Agent",MTGConstants.USER_AGENT);
			picture=ImageIO.read(connection.getInputStream());
		}
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
	
	public String getFormat() {
		return format;
	}
	public void setFormat(String extension) {
		this.format=extension;
		
	}
	
	
	
	
	
	
}
