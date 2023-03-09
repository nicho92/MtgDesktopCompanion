package org.magic.gui;

import static org.magic.services.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGNotification;
import org.magic.api.beans.MTGNotification.MESSAGE_TYPE;
import org.magic.api.beans.Wallpaper;
import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.logging.MTGLogger;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.UITools;
public class WallpaperGUI extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private JComboBox<MTGWallpaperProvider> cboWallpapersProv;
	private transient MTGWallpaperProvider selectedProvider;
	private AbstractBuzyIndicatorComponent lblLoad;
	private JPanel panelThumnail;
	private JTextField txtSearch;
	private JButton btnImport;
	private GridBagConstraints c;
	private int index = 0;
	private int val = 4;


	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_WALLPAPER;
	}

	@Override
	public String getTitle() {
		return capitalize("WALLPAPER");
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

		revalidate();

	}

	public WallpaperGUI() {

		setLayout(new BorderLayout(0, 0));



		panelThumnail = new JPanel();
		add(new JScrollPane(panelThumnail), BorderLayout.CENTER);


		c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 2, 2);
		c.anchor = GridBagConstraints.NORTHWEST;

		panelThumnail.setLayout(new GridBagLayout());

		var panel = new JPanel();
		add(panel, BorderLayout.NORTH);

		cboWallpapersProv = UITools.createComboboxPlugins(MTGWallpaperProvider.class, false);

		selectedProvider = cboWallpapersProv.getItemAt(0);
		cboWallpapersProv
				.addActionListener(e -> selectedProvider = (MTGWallpaperProvider) cboWallpapersProv.getSelectedItem());

		panel.add(cboWallpapersProv);

		txtSearch = UITools.createSearchField();

		panel.add(txtSearch);
		txtSearch.setColumns(20);


		txtSearch.addActionListener(e ->{

			panelThumnail.removeAll();
			panelThumnail.revalidate();
			index = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.gridx = 0;
			c.gridy = 0;
			lblLoad.start();

			SwingWorker<List<Wallpaper>, Wallpaper> sw = new SwingWorker<>() {

				@Override
				protected List<Wallpaper> doInBackground() throws Exception {
					return selectedProvider.search(txtSearch.getText()).stream().map(w -> {
						try {
							Wallpaper p= w.load();
							publish(p);
							return p;
						} catch (IOException e) {
							logger.error(e);
						}
						return w;
					}).toList();
				}

				@Override
				protected void process(List<Wallpaper> chunks) {
					for (Wallpaper w : chunks) {
						var thumb = new JWallThumb(w);
						addComponent(thumb);

						thumb.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								thumb.selected(!thumb.isSelected());

							}
						});
				}
				}

				@Override
				protected void done() {
					lblLoad.end();
				}
			};


			ThreadManager.getInstance().runInEdt(sw,"searching " + txtSearch.getText());

		});

		lblLoad = AbstractBuzyIndicatorComponent.createLabelComponent();
		panel.add(lblLoad);

		var panel1 = new JPanel();
		add(panel1, BorderLayout.SOUTH);

		btnImport = UITools.createBindableJButton(null,MTGConstants.ICON_IMPORT,KeyEvent.VK_I,"wallpaper import");
		btnImport.setToolTipText(capitalize("IMPORT"));
		panel1.add(btnImport);

		btnImport.addActionListener(ae -> {

			var error = false;
			for (Component comp : panelThumnail.getComponents()) {
				JWallThumb th = (JWallThumb) comp;

				if (th.isSelected()) {
					try {
						MTGControler.getInstance().saveWallpaper(th.getWallpaper());
						th.selected(false);
					} catch (IOException e1) {
						error = true;
						MTGControler.getInstance().notify(e1);
					}
				}
			}

			if (!error)
				MTGControler.getInstance().notify(new MTGNotification("OK", MTGControler.getInstance().getLangService().get("FINISHED"), MESSAGE_TYPE.INFO));


		});

	}

}

class JWallThumb extends JLabel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
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
		} catch (Exception e) {
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
		setHorizontalTextPosition(SwingConstants.CENTER);
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalTextPosition(SwingConstants.BOTTOM);
		setText(w.getName());
		setOpaque(true);

		if(w.getPicture()==null)
			try {
				wall = w.load();
			} catch (IOException e) {
				logger.error(e);
			}

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
