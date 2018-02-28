package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.Wallpaper;
import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.api.wallpaper.impl.ArtOfMtgWallpaperProvider;
import org.magic.api.wallpaper.impl.FilesWallpaperProvider;
import org.magic.gui.renderer.MagicEditionListRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;
import org.magic.tools.ImageUtils;

public class JWallpaperChooserDialog extends JDialog{
	
	private static final long serialVersionUID = 1L;
	private JComboBox<MTGWallpaperProvider> cboWallpapersProv ;
	private transient MTGWallpaperProvider selectedProvider;
	private JLabel lblLoad;
	private JPanel panelThumnail;
	private JTextField txtSearch;
	private JButton btnImport;
	private JComboBox<MagicEdition> cboEdition;
	private JLabel lblOr;
	
	public static void main(String[] args) {
		new JWallpaperChooserDialog().setVisible(true);
	}
	
	
	public JWallpaperChooserDialog() {
		setTitle("Wallpaper");
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(2, 200));
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		panelThumnail = new JPanel();
		scrollPane.setViewportView(panelThumnail);
		panelThumnail.setLayout(new GridLayout(5, 3, 0, 0));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		
		cboWallpapersProv = new JComboBox<>();
		cboWallpapersProv.addItem(new ArtOfMtgWallpaperProvider());
		cboWallpapersProv.addItem(new FilesWallpaperProvider());
		
		selectedProvider=cboWallpapersProv.getItemAt(0);
		
		
		cboWallpapersProv.addActionListener(e->
				selectedProvider=(MTGWallpaperProvider)cboWallpapersProv.getSelectedItem()
		);
		
		panel.add(cboWallpapersProv);
		
		txtSearch = new JTextField();
		panel.add(txtSearch);
		txtSearch.setColumns(20);
		
		txtSearch.addActionListener(e->
				ThreadManager.getInstance().execute(()->{
						try {
							panelThumnail.removeAll();
							panelThumnail.revalidate();
							lblLoad.setVisible(true);
							List<Wallpaper> list = selectedProvider.search(txtSearch.getText());
							
							for(Wallpaper w : list)
								panelThumnail.add(new JWallThumb(w));
							
							lblLoad.setVisible(false);
							
						} catch (Exception e1) {
							lblLoad.setVisible(false);
							JOptionPane.showMessageDialog(null, e1,MTGControler.getInstance().getLangService().getError(),JOptionPane.ERROR_MESSAGE);
						}
					}, "search " + selectedProvider )
		);
		
		lblOr = new JLabel("or");
		panel.add(lblOr);
		
		List<MagicEdition> li;
		try {
			li = MTGControler.getInstance().getEnabledProviders().loadEditions();
			Collections.sort(li);
		} catch (Exception e1) {
			li= new ArrayList<>();
		}
		cboEdition = new JComboBox<>(new DefaultComboBoxModel<MagicEdition>(li.toArray(new MagicEdition[li.size()])));
		cboEdition.setRenderer(new MagicEditionListRenderer());
		
		cboEdition.addItemListener(e->
		ThreadManager.getInstance().execute(()->{
				try {
					panelThumnail.removeAll();
					panelThumnail.revalidate();
					lblLoad.setVisible(true);
					List<Wallpaper> list = selectedProvider.search((MagicEdition)cboEdition.getSelectedItem());
					
					for(Wallpaper w : list)
						panelThumnail.add(new JWallThumb(w));
					
					lblLoad.setVisible(false);
					
				} catch (Exception e1) {
					lblLoad.setVisible(false);
					JOptionPane.showMessageDialog(null, e1,MTGControler.getInstance().getLangService().getError(),JOptionPane.ERROR_MESSAGE);
				}
			}, "search " + selectedProvider )
);
		
		panel.add(cboEdition);
		
		
		lblLoad = new JLabel("");
		panel.add(lblLoad);
		lblLoad.setIcon(MTGConstants.ICON_LOADING);
		lblLoad.setVisible(false);
		
		JPanel panel1 = new JPanel();
		getContentPane().add(panel1, BorderLayout.SOUTH);
		
		JButton btnClose = new JButton(MTGControler.getInstance().getLangService().getCapitalize("CANCEL"));
		btnClose.addActionListener(e->dispose());
		panel1.add(btnClose);
		
		btnImport = new JButton(MTGControler.getInstance().getLangService().getCapitalize("IMPORT"));
		btnImport.addActionListener(ae->{
			
			//TODO import
			
			
		});
		panel1.add(btnImport);
		pack();
		setLocationRelativeTo(null);
	}


}

class JWallThumb extends JLabel
{
	private boolean selected=false;
	private Color c = getBackground();
	public void selected(boolean s)
	{
		selected=s;
		if(selected)
			setBackground(SystemColor.inactiveCaption);
		else
			setBackground(c);
	}
	
	public JWallThumb(Wallpaper w) {
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
	
}
