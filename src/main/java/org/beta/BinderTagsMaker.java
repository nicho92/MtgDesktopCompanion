package org.beta;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.magic.api.beans.MagicEdition;
import org.magic.services.extra.BoosterPicturesProvider;
import org.magic.services.extra.BoosterPicturesProvider.LOGO;
import org.magic.tools.ImageUtils;


public class BinderTagsMaker {

	private BoosterPicturesProvider prov;
	private Color backColor=null;
	private Dimension d;
	private boolean border;
	private LOGO addlogo;
	private List<BufferedImage> lst;
	int height = 0;
	int width=0;
	
	
	public Dimension getDimension() {
		return d;
	}
	
	public void setLogo(LOGO addlogo) {
		this.addlogo = addlogo;
	}
	
	public void setBackColor(Color backColor) {
		this.backColor = backColor;
	}
	
	public void setBorder(boolean border) {
		this.border = border;
	}

	public BinderTagsMaker(Dimension d){
		prov = new BoosterPicturesProvider();
		addlogo=null;
		border=true;
		this.d=d;
		lst = new ArrayList<>();
	}
	

		
	
	public void addIds(List<String> ids)
	{
		List<BufferedImage> ims = new ArrayList<>();
		for(String id :ids)
		{
				BufferedImage im = prov.getBannerFor(id);
				if(im!=null)
					ims.add(im);
		}
		create(ims);
	}
	
	public void add(MagicEdition ed)
	{
		BufferedImage img = prov.getBannerFor(ed);
		if(img!=null)
		{
			ArrayList<BufferedImage> l = new ArrayList<>();
			l.add(img);
			create(l);
		}
	}
	
	
	public void addList(List<MagicEdition> eds)
	{
		addIds(eds.stream().map(MagicEdition::getId).collect(Collectors.toList()));
	}
	
	
	public void adds(String... ids)
	{
		addIds(Arrays.asList(ids));
	}
	
	public void clear()
	{
		lst.clear();
	}

	
	
	private void create(List<BufferedImage> imgs) {

		int offset = 0;
		height = offset;
		width=0;
		
		if(d==null)
			width=imgs.get(0).getWidth(null);
		else
			width=(int) d.getWidth();
		
		if(addlogo!=null)
			lst.set(0,ImageUtils.scaleResize(prov.getLogo(addlogo), width));
		
		for (Image im : imgs) {
			BufferedImage imgb = (BufferedImage) im;
			imgb=ImageUtils.scaleResize(imgb, width);
			height += imgb.getHeight();
			lst.add(imgb);
		}
		
		if(d!=null&&d.getHeight()>0)
			height=(int)d.getHeight();
		
		
	}

	public BufferedImage generate() {

		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = newImage.createGraphics();
		
		if(backColor!=null)
		{
			g2.setColor(backColor);
			g2.fillRect(0,0,newImage.getWidth(),newImage.getHeight());
		}
		
		if(border)
		{
			int thik=1;
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(thik));
			g2.drawRect(0, 0, newImage.getWidth()-thik,newImage.getHeight()-thik);
		}
		
		
		int x = 0;
		for (BufferedImage im : lst) {
			g2.drawImage(im, null, 0, x);
			x += im.getHeight();
		}
		g2.dispose();
		return newImage;
	}
	

}
