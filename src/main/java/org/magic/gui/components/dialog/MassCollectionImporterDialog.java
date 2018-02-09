package org.magic.gui.components.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MTGCardsProvider;
import org.magic.api.interfaces.MTGDao;
import org.magic.gui.renderer.MagicEditionListRenderer;
import org.magic.services.MTGControler;
import org.magic.services.MTGLogger;
import org.magic.services.ThreadManager;

public class MassCollectionImporterDialog extends JDialog{
	
	private transient MTGCardsProvider provider;
	private transient MTGDao dao;
	private List<MagicEdition> list;
	private String[] ids;
	private JTextPane txtNumbersInput;
	
	public MassCollectionImporterDialog(MTGDao dao,MTGCardsProvider provider,List<MagicEdition> list) {
		setSize(new Dimension(646, 290));
		setTitle(MTGControler.getInstance().getLangService().getCapitalize("MASS_CARDS_IMPORT"));
		
		this.dao=dao;
		this.provider=provider;
		this.list=list;
		
		try {
			initGUI();
		} catch (Exception e) {
			MTGLogger.printStackTrace(e);
		}
	}

	private void initGUI() throws SQLException {
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panelCollectionInput = new JPanel();
		getContentPane().add(panelCollectionInput, BorderLayout.NORTH);
		
		JLabel lblImport = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("IMPORT")+" ");
		panelCollectionInput.add(lblImport);
		
		final JComboBox cboEditions = new JComboBox(list.toArray());
		cboEditions.setRenderer(new MagicEditionListRenderer());
		panelCollectionInput.add(cboEditions);
		
		List lc = dao.getCollections();
		
		JLabel lblNewLabel = new JLabel(MTGControler.getInstance().getLangService().getCapitalize("BY"));
		panelCollectionInput.add(lblNewLabel);
		
		final JComboBox<String> cboByType = new JComboBox<>();
		cboByType.setModel(new DefaultComboBoxModel<String>(new String[] {"number", "name"}));
		panelCollectionInput.add(cboByType);
		
		JLabel lblIn = new JLabel("in");
		panelCollectionInput.add(lblIn);
		final JComboBox cboCollections = new JComboBox(lc.toArray());
		panelCollectionInput.add(cboCollections);
		
		JPanel panneauBas = new JPanel();
		getContentPane().add(panneauBas, BorderLayout.SOUTH);
		final JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		
		final JCheckBox checkNewOne = new JCheckBox(MTGControler.getInstance().getLangService().getCapitalize("IMPORT_OTHER_SERIE"));
		
		JButton btnInverse = new JButton("Inverse");
		btnInverse.addActionListener(e-> {
				MagicEdition ed = (MagicEdition)cboEditions.getSelectedItem();
				int max = ed.getCardCount();
				List<String> elements = Arrays.asList(txtNumbersInput.getText().replaceAll("\n", " ").replaceAll("  ", " ").trim().split(" "));
				List<String> edList = new ArrayList<>();
				for(int i=1;i<=max;i++)
					edList.add(String.valueOf(i));
					
				edList.removeAll(elements);
				
				StringBuilder temp = new StringBuilder();
				for(String s : edList)
					temp.append(s).append(" ");
				
				txtNumbersInput.setText(temp.toString());
		});
		panneauBas.add(btnInverse);
		
		
		
		
		panneauBas.add(checkNewOne);
		
		
		JButton btnImport = new JButton(MTGControler.getInstance().getLangService().getCapitalize("IMPORT"));
		btnImport.addActionListener(e->{
				final MagicEdition ed = (MagicEdition)cboEditions.getSelectedItem();
				final MagicCollection col = (MagicCollection)cboCollections.getSelectedItem();
				
				if(cboByType.getSelectedItem().equals("number"))
					ids = txtNumbersInput.getText().replaceAll("\n", " ").replaceAll("  ", " ").trim().split(" ");
				else
					ids = txtNumbersInput.getText().split("\n");
				progressBar.setMaximum(ids.length);
				
				ThreadManager.getInstance().execute(()->{
						int i=1;
						for(String id : ids)
						{
							try {
								MagicCard mc = null;
								
								if(cboByType.getSelectedItem().toString().equalsIgnoreCase("number"))
									mc=provider.getCardByNumber(id, ed);
								else
									mc=provider.searchCardByCriteria("name", id.replaceAll("\n", " ").replaceAll("  ", " ").trim(),(MagicEdition)cboEditions.getSelectedItem(),true).get(0);
								
								
								dao.saveCard(mc, col);
								progressBar.setValue(i++);
							} catch (Exception e1) {
								MTGLogger.printStackTrace(e1);
							}
						}
						JOptionPane.showMessageDialog(null, MTGControler.getInstance().getLangService().getCapitalize("X_ITEMS_IMPORTED",ids.length),MTGControler.getInstance().getLangService().getCapitalize("FINISHED"),JOptionPane.INFORMATION_MESSAGE);
						if(!checkNewOne.isSelected())
						{
							setVisible(false);
							progressBar.setValue(0);
						}
				},"btnImport importCards");
		});
		panneauBas.add(btnImport);
		
		panneauBas.add(progressBar);
		
		txtNumbersInput = new JTextPane();
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(txtNumbersInput);
		
		setModal(true);
		setLocationRelativeTo(null);
		
	}
	
	

}
