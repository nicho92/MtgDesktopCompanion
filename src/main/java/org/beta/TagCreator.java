package org.beta;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.magic.api.beans.MagicEdition;
import org.magic.services.extra.BoosterPicturesProvider;
import org.magic.services.extra.BoosterPicturesProvider.LOGO;
import org.magic.tools.ImageUtils;

import com.itextpdf.awt.geom.Dimension;

public class TagCreator {

	private BoosterPicturesProvider prov;
	private Color backColor=null;
	private Dimension d;
	private boolean border;
	private boolean addlogo;
	
	
	public static void main(String[] args) throws IOException {
		Dimension d = new Dimension(567, 2173);
		TagCreator t = new TagCreator();
		t.init(Color.WHITE, d, true,true);
		t.generateFromId(new File("d:/test.png"),"4ED","MIR","VIS","WTH","MM3","MM2","IMA","DDU","");
	}
	
	public TagCreator(){
		prov = new BoosterPicturesProvider();
		addlogo=true;
		border=true;
	}
	
	public void init(Color back,Dimension d,boolean border, boolean b)
	{
		this.backColor=back;
		this.d=d;
		this.border=border;
		this.addlogo=b;
	}
	
	
	public void generateFromId(File f,List<String> ids) throws IOException
	{
		List<BufferedImage> ims = new ArrayList<>();
		for(String id :ids)
		{
				BufferedImage im = prov.getBannerFor(id);
				if(im!=null)
					ims.add(im);
		}
		ImageUtils.saveImage(add(ims), f,"PNG");
	}
	
	public void generateFromList(File f,List<MagicEdition> eds) throws IOException
	{
		generateFromId(f,eds.stream().map(MagicEdition::getId).collect(Collectors.toList()));
	}
	
	
	public void generateFromId(File f,String... ids) throws IOException
	{
		generateFromId(f,Arrays.asList(ids));
	}
	

	
	private BufferedImage add(List<BufferedImage> imgs) {

		int offset = 0;
		int hei = offset;
		int width=0;
		List<BufferedImage> lst = new ArrayList<>();
		
		
		if(d==null)
			width=imgs.get(0).getWidth(null);
		else
			width=(int) d.getWidth();
		
		if(addlogo)
			lst.add(ImageUtils.scaleResize(prov.getLogo(LOGO.YELLOW), width));
		
		for (Image im : imgs) {
			BufferedImage imgb = (BufferedImage) im;
			imgb=ImageUtils.scaleResize(imgb, width);
			hei += imgb.getHeight();
			lst.add(imgb);
		}
		
		if(d!=null&&d.getHeight()>0)
			hei=(int)d.getHeight();
		
		
		BufferedImage newImage = new BufferedImage(width, hei, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = newImage.createGraphics();
		
		if(backColor!=null)
		{
			g2.setColor(Color.WHITE);
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
