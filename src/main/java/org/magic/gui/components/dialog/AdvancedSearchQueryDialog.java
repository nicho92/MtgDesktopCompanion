package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.magic.api.beans.MagicCollection;
import org.magic.api.criterias.MTGCrit;
import org.magic.gui.components.CriteriaComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.tools.UITools;

public class AdvancedSearchQueryDialog extends JDialog {


	private GridLayout layout;
	private JPanel pContent;
	private transient List<MTGCrit> crits;
	private JComboBox<MagicCollection> cboCollection;
	private JCheckBox chkSearchInCollection;


	public AdvancedSearchQueryDialog() {
		getContentPane().setLayout(new BorderLayout(0, 0));

		crits = new ArrayList<>();

		layout = new GridLayout(1,1);

		var btnNewButton = new JButton(MTGConstants.ICON_NEW);
		var btnSearch = new JButton(MTGConstants.ICON_SEARCH);
		chkSearchInCollection = new JCheckBox("Collection Only");
		cboCollection = UITools.createComboboxCollection();

		var bottom = new JPanel();

		bottom .add(btnNewButton);
		bottom.add(btnSearch);

		getContentPane().add(bottom, BorderLayout.SOUTH);

		pContent = new JPanel();
		getContentPane().add(pContent, BorderLayout.CENTER);
		pContent.setLayout(layout);

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);


		cboCollection.setEnabled(false);

		panel.add(chkSearchInCollection);
		panel.add(cboCollection);

		chkSearchInCollection.addItemListener(il->cboCollection.setEnabled(chkSearchInCollection.isSelected()));

		btnNewButton.addActionListener(al->
			SwingUtilities.invokeLater(()->{

				var cc = new CriteriaComponent();

				var delete = new JButton(MTGConstants.ICON_DELETE);
				delete.addActionListener(el->{
					pContent.remove(cc);
					pContent.revalidate();
					pContent.repaint();

				});

				cc.addButton(delete,false);
				pContent.add(cc);
				layout.setRows(layout.getRows()+1);
				pContent.revalidate();
				pContent.repaint();
			})
		);


		btnSearch.addActionListener(al->{

			crits.clear();
			for(var i = 0 ; i<pContent.getComponentCount();i++)
			{
				if(pContent.getComponent(i) instanceof CriteriaComponent comp)
				{
					crits.add(comp.getMTGCriteria());
				}
			}

			dispose();
		});

		setIconImage(MTGConstants.ICON_SEARCH_24.getImage());
		setTitle(MTGControler.getInstance().getLangService().get("ADVANCED_SEARCH"));
		setSize(new Dimension(875, 400));
		setModal(true);
		setLocationRelativeTo(null);
		btnNewButton.doClick();
	}

	public List<MTGCrit> getCrits() {
		return crits;
	}

	public MagicCollection getCollection() {

		if(!chkSearchInCollection.isSelected())
			return null;

		return (MagicCollection)cboCollection.getSelectedItem();
	}




	private static final long serialVersionUID = 1L;

}
