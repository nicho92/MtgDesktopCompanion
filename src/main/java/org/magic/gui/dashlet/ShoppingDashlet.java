package org.magic.gui.dashlet;

import static org.magic.tools.MTG.getPlugin;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.models.MagicCardTableModel;
import org.magic.gui.renderer.MagicCardListRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.UITools;
import org.magic.gui.components.GroupedShoppingPanel;

public class ShoppingDashlet extends AbstractJDashlet {

	private DefaultListModel<MagicCard> model;
	
	public ShoppingDashlet() {
		

		
	}

	private static final long serialVersionUID = 1L;

	@Override
	public ImageIcon getDashletIcon() {
		return MTGConstants.ICON_SHOP;
	}
	
	
	public void initGUI() {
		JPanel panel = new JPanel();
		GroupedShoppingPanel groupedShoppingPanel = new GroupedShoppingPanel();
		
		getContentPane().add(panel, BorderLayout.NORTH);
		
		model = new DefaultListModel<>();
		
		JList<MagicCard> list = new JList<>(model);
		list.setCellRenderer(new MagicCardListRenderer());
		JPanel panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);
		JButton btnPast = new JButton(MTGConstants.ICON_PASTE);
		panneauHaut.add(btnPast);
		getContentPane().add(new JScrollPane(list), BorderLayout.CENTER);
		
		btnPast.addActionListener(al->{
			
			try {
				MagicDeck d = getPlugin(MTGConstants.DEFAULT_CLIPBOARD_NAME,MTGCardsExport.class).importDeck(null,null);
				d.getMain().keySet().forEach(model::addElement);
				
				
				groupedShoppingPanel.enableControle(!model.isEmpty());
				
				groupedShoppingPanel.initListCards(IntStream.range(0,model.size()).mapToObj(model::get).collect(Collectors.toList()));
				
				
			} catch (Exception e) {
				logger.error(e);
				MTGControler.getInstance().notify(e);
			}
			
		});
		
	getContentPane().add(groupedShoppingPanel, BorderLayout.EAST);
	
		
		if (getProperties().size() > 0) {
			Rectangle r = new Rectangle((int) Double.parseDouble(getString("x")),
					(int) Double.parseDouble(getString("y")), (int) Double.parseDouble(getString("w")),
					(int) Double.parseDouble(getString("h")));

			setBounds(r);
		}
	}
		
	@Override
	public String getName() {
		return "Shopping";
	}

	@Override
	public void init() {

		
	}


}