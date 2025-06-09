package org.magic.gui;

import static org.magic.services.tools.MTG.capitalize;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.magic.api.beans.MTGWallpaper;
import org.magic.api.interfaces.MTGWallpaperProvider;
import org.magic.gui.abstracts.AbstractBuzyIndicatorComponent;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.gui.components.wallpaper.JWallThumb;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
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
	private JCheckBox chkSelectAll;

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
		
		chkSelectAll = new JCheckBox("Select All");

		c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 2, 2);
		c.anchor = GridBagConstraints.NORTHWEST;

		panelThumnail.setLayout(new GridBagLayout());

		var panel = new JPanel();
		add(panel, BorderLayout.NORTH);

		cboWallpapersProv = UITools.createComboboxPlugins(MTGWallpaperProvider.class, false);
		selectedProvider = cboWallpapersProv.getItemAt(0);
		cboWallpapersProv.addActionListener(_ -> selectedProvider = (MTGWallpaperProvider) cboWallpapersProv.getSelectedItem());

		panel.add(cboWallpapersProv);
		txtSearch = UITools.createSearchField();

		panel.add(txtSearch);
		txtSearch.setColumns(20);
		txtSearch.addActionListener(_ ->{
			panelThumnail.removeAll();
			panelThumnail.revalidate();
			index = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.gridx = 0;
			c.gridy = 0;
			lblLoad.start();

			var sw = new SwingWorker<List<MTGWallpaper>, MTGWallpaper>() {

				@Override
				protected List<MTGWallpaper> doInBackground() throws Exception {
					return selectedProvider.search(txtSearch.getText()).stream().map(w -> {
						try {
							var p= w.load();
							publish(p);
							return p;
						} catch (IOException e) {
							logger.error(e);
						}
						return w;
					}).toList();
				}

				@Override
				protected void process(List<MTGWallpaper> chunks) 
				{
						for (MTGWallpaper w : chunks) 
						{
							var thumb = new JWallThumb(w,true);
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
		panel1.add(chkSelectAll);
		panel1.add(btnImport);


		btnImport.addActionListener(_ ->{
			
			lblLoad.start();
			var sw =  new SwingWorker<Void, Void>()
			{
				@Override
				protected Void doInBackground() throws Exception {
					for (var comp : panelThumnail.getComponents()) 
					{
						var th = (JWallThumb) comp;

						if (th.isSelected() || chkSelectAll.isSelected()) 
						{
							try {
								MTGControler.getInstance().saveWallpaper(th.getWallpaper());
								th.selected(false);
							} catch (IOException e1) {
								logger.error(e1);
							}
						}
					}
					
					return null;
				}

				@Override
				protected void done() {
					lblLoad.end();
				}
			};
			
			ThreadManager.getInstance().runInEdt(sw, "Saving wallpapers");
		});
	}

}


