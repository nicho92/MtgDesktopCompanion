package org.magic.composer.layer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import org.magic.services.network.URLTools;

public class IllustrationLayer extends AbstractLayer {

	private transient BufferedImage image;
	private URL url;

	
	public IllustrationLayer(URL source) {

		this.url = source;
		setName("Illustration");
		reload();
		width = image.getWidth();
		height = image.getHeight();
	}

	public BufferedImage getImage() {
		return image;
	}
	
	@Override
	public void reload() {

		try {
			this.image = URLTools.extractAsImage(url.toString());
		} catch (IOException e) {
			logger.error(e);
		}
		
	}
	
	@Override
	public void paint(Graphics2D g2) {
		g2.drawImage(getImage(), getX(), getY(), getWidth(), getHeight(), null);

	}

}
