package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXSearchField;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.beans.Wallpaper;
import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.gui.components.JBuzyLabel;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

public class WallpaperGUI extends JPanel {

	private static final long serialVersionUID = 1L;
	private JComboBox<MTGWallpaperProvider> cboWallpapersProv;
	private transient MTGWallpaperProvider selectedProvider;
	private JBuzyLabel lblLoad;
	private JPanel panelThumnail;
	private JXSearchField txtSearch;
	private JButton btnImport;
	private GridBagConstraints c;
	private int index = 0;
	private int val = 4;

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new WallpaperGUI());
		f.pack();
		f.setVisible(true);
	}

	public void addComponent(JWallThumb i) {
		if (index >= val) {
			c.gridy = c.gridy + 1;
			c.gridx = 0;
			index = 0;
		}
		c.gridx = c.gridx + 1;
		panelThumnail.add(i, c);
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
		c.insets = new Insets(2, 2, 2, 2);
		c.anchor = GridBagConstraints.NORTHWEST;

		panelThumnail.setLayout(new GridBagLayout());

		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);

		cboWallpapersProv = new JComboBox<>();

		for (MTGWallpaperProvider prov : MTGControler.getInstance().getEnabledWallpaper())
			cboWallpapersProv.addItem(prov);

		selectedProvider = cboWallpapersProv.getItemAt(0);
		cboWallpapersProv
				.addActionListener(e -> selectedProvider = (MTGWallpaperProvider) cboWallpapersProv.getSelectedItem());

		panel.add(cboWallpapersProv);

		txtSearch = new JXSearchField();
		txtSearch.setSearchMode(MTGConstants.SEARCH_MODE);
		panel.add(txtSearch);
		txtSearch.setColumns(20);

		txtSearch.addActionListener(e -> ThreadManager.getInstance().execute(() -> {
			try {
				panelThumnail.removeAll();
				panelThumnail.revalidate();
				index = 0;
				c.weightx = 1;
				c.weighty = 1;
				c.gridx = 0;
				c.gridy = 0;
				lblLoad.buzy(true);
				List<Wallpaper> list = selectedProvider.search(txtSearch.getText());

				for (Wallpaper w : list) {
					JWallThumb thumb = new JWallThumb(w);
					addComponent(thumb);

					thumb.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							thumb.selected(!thumb.isSelected());

						}
					});

				}

				lblLoad.buzy(false);

			} catch (Exception e1) {
				lblLoad.buzy(false);
				MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),e1));
			}
		}, "search " + selectedProvider));

		lblLoad = new JBuzyLabel();
		panel.add(lblLoad);

		JPanel panel1 = new JPanel();
		add(panel1, BorderLayout.SOUTH);

		btnImport = new JButton(MTGControler.getInstance().getLangService().getCapitalize("IMPORT"));
		panel1.add(btnImport);

		btnImport.addActionListener(ae -> {

			boolean error = false;
			for (Component comp : panelThumnail.getComponents()) {
				JWallThumb th = (JWallThumb) comp;

				if (th.isSelected()) {
					try {
						MTGControler.getInstance().saveWallpaper(th.getWallpaper());

					} catch (IOException e1) {
						error = true;
						MTGControler.getInstance().notify(new MTGNotification(MTGControler.getInstance().getLangService().getError(),e1));
					}
				}
			}

			if (!error)
				MTGControler.getInstance().notify(new MTGNotification("OK", MTGControler.getInstance().getLangService().get("FINISHED"), MESSAGE_TYPE.INFO));
			

		});

	}

}

class JWallThumb extends JLabel {
	private boolean selected = false;
	private Color c = getBackground();
	private transient Wallpaper wall;
	private int size;
	private int fontHeight = 20;
	private transient Logger logger = MTGLogger.getLogger(this.getClass());

	public boolean isSelected() {
		return selected;
	}

	public Wallpaper getWallpaper() {
		return wall;
	}

	public void resizePic(int size) {
		this.size = size;
		try {

			int w = wall.getPicture().getWidth(null);
			int h = wall.getPicture().getHeight(null);
			float scaleW = (float) size / w;
			float scaleH = (float) size / h;
			if (scaleW > scaleH) {
				w = -1;
				h = (int) (h * scaleH);
			} else {
				w = (int) (w * scaleW);
				h = -1;
			}
			Image img = wall.getPicture().getScaledInstance(w, h, Image.SCALE_SMOOTH);
			setIcon(new ImageIcon(img));
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public void selected(boolean s) {
		selected = s;
		if (selected)
			setBackground(SystemColor.inactiveCaption);
		else
			setBackground(c);
	}

	public JWallThumb(Wallpaper w) {
		wall = w;
		setHorizontalTextPosition(JLabel.CENTER);
		setHorizontalAlignment(JLabel.CENTER);
		setVerticalTextPosition(JLabel.BOTTOM);
		setText(w.getName());
		setOpaque(true);
		resizePic(400);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(size, size + fontHeight);
	}

	@Override
	public String toString() {
		return getName();
	}

}
