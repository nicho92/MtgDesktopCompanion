package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.magic.api.beans.Wallpaper;
import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;
import org.magic.tools.ImageUtils;

public class WallpaperGUI extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private JComboBox<MTGWallpaperProvider> cboWallpapersProv ;
	private transient MTGWallpaperProvider selectedProvider;
	private JLabel lblLoad;
	private JPanel panelThumnail;
	private JTextField txtSearch;
	private JButton btnImport;
	private GridBagConstraints c;
	private int index=0;
	private int val=4;

	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new WallpaperGUI());
		f.pack();
		f.setVisible(true);
	}
	
	
	public void addComponent(JWallThumb i)
	{
		if(index>=val)
		{
			c.gridy=c.gridy+1;
			c.gridx=0;
			index=0;
		}
	   c.gridx=c.gridx+1;
	   panelThumnail.add(i,c);
	   index++;
		
	}
	
	
	public WallpaperGUI() {
		
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(2, 200));
		add(scrollPane, BorderLayout.CENTER);
		
		panelThumnail = new JPanel();
		scrollPane.setViewportView(panelThumnail);
		
		c = new GridBagConstraints();
		c.insets = new Insets(2,2,2,2); 
		c.anchor = GridBagConstraints.NORTHWEST;
		
		panelThumnail.setLayout(new GridBagLayout());
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		
		cboWallpapersProv = new JComboBox<>();
		
		for(MTGWallpaperProvider prov : MTGControler.getInstance().getEnabledWallpaper())
			cboWallpapersProv.addItem(prov);
		
		selectedProvider=cboWallpapersProv.getItemAt(0);
		cboWallpapersProv.addActionListener(e->selectedProvider=(MTGWallpaperProvider)cboWallpapersProv.getSelectedItem());
		
		panel.add(cboWallpapersProv);
		
		txtSearch = new JTextField();
		panel.add(txtSearch);
		txtSearch.setColumns(20);
		
		txtSearch.addActionListener(e->
				ThreadManager.getInstance().execute(()->{
						try {
							panelThumnail.removeAll();
							panelThumnail.revalidate();
							index=0;
							c.weightx = 1;
							c.weighty = 1;
							c.gridx = 0;
							c.gridy = 0;
							lblLoad.setVisible(true);
							List<Wallpaper> list = selectedProvider.search(txtSearch.getText());
							
							for(Wallpaper w : list)
							{
								JWallThumb thumb = new JWallThumb(w);
								addComponent(thumb);
								
								thumb.addMouseListener(new MouseAdapter() {
									@Override
									public void mouseClicked(MouseEvent e) {
										thumb.selected(!thumb.isSelected());
										
									}
								});
								
							}
								
							lblLoad.setVisible(false);
							
						} catch (Exception e1) {
							lblLoad.setVisible(false);
							JOptionPane.showMessageDialog(null, e1,MTGControler.getInstance().getLangService().getError(),JOptionPane.ERROR_MESSAGE);
						}
					}, "search " + selectedProvider )
		);
		
		lblLoad = new JLabel("");
		panel.add(lblLoad);
		lblLoad.setIcon(MTGConstants.ICON_LOADING);
		lblLoad.setVisible(false);
		
		JPanel panel1 = new JPanel();
		add(panel1, BorderLayout.SOUTH);
		
		
		btnImport = new JButton(MTGControler.getInstance().getLangService().getCapitalize("IMPORT"));
		panel1.add(btnImport);
		
		
		
		btnImport.addActionListener(ae->{
			
			for(Component comp : panelThumnail.getComponents())
			{
				JWallThumb th = (JWallThumb)comp;
				
				if(th.isSelected())
				{
					try {
						MTGControler.getInstance().saveWallpaper(th.getWallpaper());
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, e1,MTGControler.getInstance().getLangService().getError(),JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			
		});
		
		
	}


}

class JWallThumb extends JLabel
{
	private boolean selected=false;
	private Color c = getBackground();
	private transient Wallpaper wall;
	
	public boolean isSelected() {
		return selected;
	}
	
	public Wallpaper getWallpaper() {
		return wall;
	}
	
	public void resizePic(int w, int h)
	{
		try {
			setIcon(new ImageIcon(ImageUtils.resize(wall.getPicture(), w, h)));
		} catch (IOException e) {
			MTGLogger.printStackTrace(e);
		}
	}
	public void selected(boolean s)
	{
		selected=s;
		if(selected)
			setBackground(SystemColor.inactiveCaption);
		else
			setBackground(c);
	}
	
	public JWallThumb(Wallpaper w) {
		wall=w;
		setHorizontalTextPosition(JLabel.CENTER);
		setVerticalTextPosition(JLabel.BOTTOM);
		setText(w.getName());
		setOpaque(true);
		try {
			setIcon(new ImageIcon(ImageUtils.resize(w.getPicture(), 200, 350)));
		} 
		catch (IOException e) {
			MTGLogger.printStackTrace(e);
		}
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
