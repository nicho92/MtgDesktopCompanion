package org.magic.gui.dashlet;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTable;
import org.magic.api.beans.CardShake;
import org.magic.api.interfaces.abstracts.AbstractDashBoard.FORMAT;
import org.magic.gui.abstracts.AbstractJDashlet;
import org.magic.gui.models.CardsShakerTableModel;
import org.magic.gui.renderer.CardShakeRenderer;
import org.magic.services.MTGControler;
import org.magic.services.ThreadManager;

import javafx.scene.control.Skinnable;
import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

public class BestTrendingDashlet extends AbstractJDashlet{

	private JXTable table;
	private CardsShakerTableModel modStandard;

	public BestTrendingDashlet() {
		super();
		setTitle(getName());
		setResizable(true);
		setClosable(true);
		setIconifiable(true);
		setMaximizable(true);
		
		
		initGUI();
	}
	
	@Override
	public String getName() {
		return "Winners/Loosers";
	}

	

	@Override
	public void init() {
		ThreadManager.getInstance().execute(new Runnable() {
			
			@Override
			public void run() {
				
				
				try {
					List<CardShake> shakes = MTGControler.getInstance().getEnabledDashBoard().getShakerFor(FORMAT.modern.toString());
					Collections.sort(shakes,new Comparator<CardShake>() {

						public int compare(CardShake o1, CardShake o2) {
							if(o1.getPercentDayChange()>o2.getPercentDayChange())
									return -1;
								else
									return 1;
						}
					});
					
					List<CardShake> ret = new ArrayList<CardShake>();
					ret.addAll(shakes.subList(0, 5));
					ret.addAll(shakes.subList(shakes.size()-6, shakes.size()-1));
					
					modStandard.init(ret);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				table.setModel(modStandard);
				table.setRowSorter(new TableRowSorter(modStandard) );
				table.packAll();
				table.getColumnModel().getColumn(3).setCellRenderer(new CardShakeRenderer());
				modStandard.fireTableDataChanged();
				
				
			}
		}, "Init best Dashlet");
		
	}

	@Override
	public void initGUI() {
		JPanel panneauHaut = new JPanel();
		getContentPane().add(panneauHaut, BorderLayout.NORTH);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		modStandard = new CardsShakerTableModel();
		table = new JXTable(modStandard);
		scrollPane.setViewportView(table);

		if(props.size()>0) {
			Rectangle r = new Rectangle((int)Double.parseDouble(props.getProperty("x")), 
										(int)Double.parseDouble(props.getProperty("y")),
										(int)Double.parseDouble(props.getProperty("w")),
										(int)Double.parseDouble(props.getProperty("h")));
			
			try {
				//cboFormats.setSelectedItem(FORMAT.valueOf(props.getProperty("NUMBER").toString()));
			
			} catch (Exception e) {
				e.printStackTrace();
			}
			setBounds(r);
			}
		
		setVisible(true);
	}


}
