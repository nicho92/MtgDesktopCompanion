package org.magic.composer.layer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.google.gson.JsonObject;

public class IllustrationLayer extends AbstractLayer {

	private transient BufferedImage image;
	private int width;
	private int height;
	private URL url;

	@Override
	public JsonObject toJson() {
		var obj = super.toJson();
		obj.addProperty("url", url.toString());
		return obj;

	}

	public IllustrationLayer(URL source) {

		this.url = source;

		try {
			this.image = ImageIO.read(source);
		} catch (IOException e) {
			logger.error(e);
		}
		width = image.getWidth();
		height = image.getHeight();
	}

	public BufferedImage getImage() {
		return image;
	}

	@Override
	public void paint(Graphics2D g2) {
		g2.drawImage(getImage(), getX(), getY(), getWidth(), getHeight(), null);

	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

}
