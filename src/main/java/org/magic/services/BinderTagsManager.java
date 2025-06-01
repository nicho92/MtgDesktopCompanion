package org.magic.services;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.enums.EnumItems;
import org.magic.api.interfaces.MTGSealedProvider;
import org.magic.api.sealedprovider.impl.MTGCompanionSealedProvider.LOGO;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.MTG;


public class BinderTagsManager {

	private MTGSealedProvider prov;
	private Color backColor=null;
	private Dimension d;
	private boolean border;
	private LOGO addlogo;
	private List<BufferedImage> lst;
	private int height=1;
	private int width=1;
	private int space;
	private Logger logger = MTGLogger.getLogger(this.getClass());

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

	public BinderTagsManager(Dimension d){
		prov = MTG.getEnabledPlugin(MTGSealedProvider.class);
		addlogo=null;
		border=true;
		space=0;
		this.d=d;
		lst = new ArrayList<>();
	}

	public void setEditions(List<MTGEdition> eds)
	{
		clear();
		addIds(eds.stream().map(MTGEdition::getId).toList());
	}



	public void addIds(List<String> ids)
	{
		List<BufferedImage> ims = new ArrayList<>();
		for(String id :ids)
		{
			try {
				BufferedImage im = prov.getPictureFor(prov.get(new MTGEdition(id),EnumItems.SET).get(0));
				ims.add(im);
			}catch(IndexOutOfBoundsException _)
			{
				logger.error("No {} found for {}",EnumItems.SET,id);
			}

		}
		create(ims);
	}

	public void clear()
	{
		lst.clear();
	}



	private void create(List<BufferedImage> imgs) {

		var offset = 1;
		height = offset;
		width=1;

		if(d==null)
			width=imgs.get(0).getWidth(null);
		else
			width=(int) d.getWidth();

		if(addlogo!=null)
		{
			if(!lst.isEmpty())
				lst.set(0,ImageTools.scaleResize(prov.getLogo(addlogo), width));
			else
				lst.add(ImageTools.scaleResize(prov.getLogo(addlogo), width));
		}

		for (BufferedImage im : imgs) {
			im=ImageTools.scaleResize(im, width);
			height += im.getHeight()+space;
			lst.add(im);
		}

		if(d!=null && d.getHeight()>0)
			height=(int)d.getHeight();


	}

	public BufferedImage generate() {

		var newImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2 = newImage.createGraphics();

		if(backColor!=null)
		{
			g2.setColor(backColor);
			g2.fillRect(0,0,newImage.getWidth(),newImage.getHeight());
		}

		if(border)
		{
			var thik=1;
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(thik));
			g2.drawRect(0, 0, newImage.getWidth()-thik,newImage.getHeight()-thik);
		}


		var x = 10;
		for (BufferedImage im : lst) {
			g2.drawImage(im, null, 0, x);
			x += im.getHeight()+space;
		}
		g2.dispose();
		return newImage;
	}



	public void setSpace(int value) {
		this.space=value*10;

	}


}
