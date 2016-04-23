package org.magic.gui.components;

import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.gui.renderer.ManaCellRenderer;
import org.magic.tools.ImageUtils;

public class ManaPanel extends JPanel {
	
	static final Logger logger = LogManager.getLogger(ManaPanel.class.getName());

	int cols=10;
	int rows =7;
	int chunkWidth=100;
	int chunkHeight=100;
	BufferedImage imgs[];
	
	public static int row_height=18;
	public static int row_width=18;
	
	String regex ="\\{(.*?)\\}";
	FlowLayout fl =new FlowLayout();
	
	int chunks = rows * cols;  
	int count = 0;  

	String manaCost;
	
	
	public ManaPanel() {
		fl.setAlignment(FlowLayout.LEFT);
		setLayout(fl);
		init();
		
	}
	
	
	public String getManaCost()
	{
		return manaCost;
	}
	
	public void setManaCost(String manaCost) {
	
		if(manaCost==null)
			return;
		
		this.removeAll();
		
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(manaCost);
		
		
		
		fl.setVgap(0);
		fl.setHgap(0);
		
		while(m.find()) {
			{
			JLabel lab = new JLabel();
				//logger.debug("Analyse symbol : " + m.group());
				Image img = getManaSymbol(m.group());
				  lab.setIcon(new ImageIcon(img.getScaledInstance(row_width, row_height, Image.SCALE_DEFAULT)));
				  lab.setHorizontalAlignment(SwingConstants.LEFT);
				  
			add(lab);
			}
        }
		
	}
	
	private void init() {
		BufferedImage image;
		imgs = new BufferedImage[chunks];
		
		try {
			image = ImageIO.read(ManaCellRenderer.class.getResource("/res/Mana.png"));
			for (int x = 0; x < rows; x++) {  
	            for (int y = 0; y < cols; y++) 
	            {  
	                imgs[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());  
	                Graphics2D gr = imgs[count++].createGraphics();  
	                gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x, chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);  
	                gr.dispose(); 
	            }  
        }  
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public Image getManaSymbol(String el) 
	{
		row_width=18;
		el = el.replaceAll("\\{", "").replaceAll("\\}", "").trim();
		int val = 0;
		try{
			val = Integer.parseInt(el);
		}
		catch(NumberFormatException ne)
		{
			
			switch(el)
			{

			case "X":val=21;break;
			case "Y":val=22;break;
			case "Z":val=23;break;
			case "W":val=24;break;
			case "U":val=25;break;
			case "B":val=26;break;
			case "R":val=27;break;
			case "G":val=28;break;
			case "S":val=29;break;
			case "W/P":val=45;break;
			case "U/P":val=46;break;
			case "B/P":val=47;break;
			case "R/P":val=48;break;
			case "G/P":val=49;break;
			
			case "W/U":val=30;break;
			case "W/B":val=31;break;
			case "U/B":val=32;break;
			case "U/R":val=33;break;
			case "B/R":val=34;break;
			case "B/G":val=35;break;
			case "R/W":val=36;break;
			case "R/G":val=37;break;
			case "G/W":val=38;break;
			case "G/U":val=39;break;
			
			case "2/W":val=40;break;
			case "2/U":val=41;break;
			case "2/B":val=42;break;
			case "2/R":val=43;break;
			case "2/G":val=44;break;
			case "T" : val=50;break;
			case "C" : val=69;break;
			case "\u221e" : val=52;break;//infinity symbol
			case "\u00BD" : val=53;break;
			case "CHAOS" : val=67;break; 
			default:val=0;
			}
		}
		List<Image> lst = new ArrayList<Image>();
		
		if(val==100)//mox lotus
		{
			lst.add(imgs[65]);
			lst.add(imgs[66]);
			row_width=row_width*lst.size();
			return ImageUtils.joinBufferedImage(lst);
		}
		
		if(val==1000000)//gleemax
		{
			
			lst.add(imgs[60]);
			lst.add(imgs[61]);
			lst.add(imgs[62]);
			lst.add(imgs[63]);
			lst.add(imgs[64]);
			row_width=row_width*lst.size();
			return ImageUtils.joinBufferedImage(lst);
		}
		
		return imgs[val];
	}
	
}
