package org.magic.gui.dashlet;

import static org.magic.services.tools.MTG.getPlugin;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.util.stream.IntStream;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicDeck;
import org.magic.api.interfaces.MTGCardsExport;
import org.magic.api.interfaces.abstracts.AbstractJDashlet;
import org.magic.gui.components.GroupedShoppingPanel;
import org.magic.gui.renderer.MagicCardListRenderer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;

public class ShoppingDashlet extends AbstractJDashlet {

	private static final long serialVersionUID = 1L;

	@Override
	public ImageIcon getDashletIcon() {
		return MTGConstants.ICON_SHOP;
	}

	@Override
	public String getCategory() {
		return "Market";
	}

	@Override
	public void initGUI() {
		var panel = new JPanel();
		var groupedShoppingPanel = new GroupedShoppingPanel();
		var btnPast = new JButton(MTGConstants.ICON_PASTE);
		DefaultListModel<MagicCard> model = new DefaultListModel<>();
		JList<MagicCard> list = new JList<>(model);
		var panneauHaut = new JPanel();
		var btnClear = new JButton(MTGConstants.ICON_DELETE);

		getContentPane().add(panel, BorderLayout.NORTH);
		list.setCellRenderer(new MagicCardListRenderer());
		panneauHaut.add(btnClear);
		panneauHaut.add(btnPast);
		getContentPane().add(new JScrollPane(list), BorderLayout.CENTER);
		getContentPane().add(panneauHaut, BorderLayout.NORTH);

		btnClear.addActionListener(ae->model.removeAllElements());



		btnPast.addActionListener(al->{

			try {
				MagicDeck d = getPlugin(MTGConstants.DEFAULT_CLIPBOARD_NAME,MTGCardsExport.class).importDeck(null,null);
				d.getMain().keySet().forEach(model::addElement);


				groupedShoppingPanel.enableControle(!model.isEmpty());

				groupedShoppingPanel.init(IntStream.range(0,model.size()).mapToObj(model::get).toList());


			} catch (Exception e) {
				logger.error(e);
				MTGControler.getInstance().notify(e);
			}

		});

	getContentPane().add(groupedShoppingPanel, BorderLayout.EAST);


		if (getProperties().size() > 0) {
			var r = new Rectangle((int) Double.parseDouble(getString("x")),
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
		// do nothing

	}


}