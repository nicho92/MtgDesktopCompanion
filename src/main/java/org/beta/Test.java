package org.beta;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.kitfox.svg.app.beans.SVGIcon;

public class Test {

	public static void main(String[] args) throws URISyntaxException, MalformedURLException {
		SVGIcon ic = new SVGIcon();
		ic.setSvgURI(new URL("https://raw.githubusercontent.com/andrewgioia/keyrune/master/svg/afr.svg").toURI());
		ic.setAntiAlias(true);
		ic.setAutosize(1);
		JLabel p = new JLabel();
		p.setIcon(ic);
		
		JFrame f = new JFrame();
		
		f.getContentPane().add(p);
		f.setVisible(true);
	
	
		
		
	}
	
	

}
