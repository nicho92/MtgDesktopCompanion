package org.magic.gui.components;

import java.awt.BorderLayout;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import org.magic.api.beans.MagicEdition;
import org.magic.services.extra.BoosterPicturesProvider;

public class MagicEditionPackagesBrowser extends JComponent {
	
	
	private BoosterPicturesProvider provider;
	
	private MagicEdition selected;
	
	
	
	public MagicEditionPackagesBrowser() {
		
		provider = new BoosterPicturesProvider();
		
		
		initGUI();
	}

	
	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		JTree tree = new JTree();
		JPanel panelDraw = new JPanel() {
			
			 @Override
			    protected void paintComponent(Graphics g) {
			        super.paintComponent(g);
			        
			        if(selected!=null)
			        	g.drawImage(provider.getBoxFor(selected), 0, 0, this);           
			    }
		} ;
		add(new JScrollPane(tree), BorderLayout.WEST);
		add(panelDraw, BorderLayout.CENTER);
		
	}


	public void setMagicEdition(MagicEdition ed)
	{
		selected=ed;
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		
		MagicEditionPackagesBrowser pane = new MagicEditionPackagesBrowser();
		pane.setMagicEdition(new MagicEdition("EXO"));
		
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(pane,BorderLayout.CENTER);
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
	}
}
