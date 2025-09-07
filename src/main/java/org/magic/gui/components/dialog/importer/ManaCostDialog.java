package org.magic.gui.components.dialog.importer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.magic.gui.abstracts.AbstractDelegatedImporterDialog;
import org.magic.gui.components.wallpaper.WrapLayout;
import org.magic.services.MTGConstants;
import org.magic.services.providers.IconsProvider;
import org.magic.services.threads.MTGRunnable;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.ImageTools;
import org.magic.services.tools.UITools;

public class ManaCostDialog extends AbstractDelegatedImporterDialog<String> {
	private List<String> symbols;
	private static final long serialVersionUID = 1L;

	@Override
	public JComponent getSelectComponent() {
		
		var component = new JPanel();
		var panelManaSymbols = new JPanel();
		
		var lblResults = new JLabel();
		var btnClear = UITools.createBindableJButton("", MTGConstants.ICON_SMALL_CLEAR, KeyEvent.VK_DELETE, "clearmana");
		
		
		symbols = new ArrayList<>();
		
		
		
		
		component.setLayout(new BorderLayout());
		panelManaSymbols.setLayout(new WrapLayout());
		
		component.add(UITools.createFlowPanel(lblResults, btnClear),BorderLayout.SOUTH);
		component.add(panelManaSymbols,BorderLayout.CENTER);
		
		setPreferredSize(new Dimension(350, 200));
		
		
		btnClear.addActionListener(_->{
			lblResults.setText("");
			symbols.clear();
		});
		
		
		ThreadManager.getInstance().invokeLater(new MTGRunnable() {
			
			@Override
			protected void auditedRun() {
				
				for (var s : getSymbols())
				{
						var lblMana = new JLabel(ImageTools.resize(new ImageIcon(IconsProvider.getInstance().getManaSymbol(s)), 16,16));
						lblMana.addMouseListener(new MouseAdapter() {
							
							@Override
							public void mouseEntered(MouseEvent e) {
								lblMana.setBorder(new LineBorder(Color.BLACK));
							}
							@Override
							public void mouseExited(MouseEvent e) {
								lblMana.setBorder(null);
							}
							@Override
							public void mouseClicked(MouseEvent e) {
								symbols.add(s);
								lblResults.setText(getSelectedItem());
							}
						});
						panelManaSymbols.add(lblMana);
				}	
				
				pack();
			}
		}, "load icons");
		
		
		return component;
	}
	
	private List<String> getSymbols() {
		return Arrays.asList(
	            "0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","X",
	            "W", "U", "B", "R", "G",
	            "W/U", "W/B", "U/B", "U/R", "B/R", "B/G", "R/G", "R/W", "G/W", "G/U",
	            "W/P", "U/P", "B/P", "R/P", "G/P",
	            "C", "S"
	        );
	}
	
	@Override
	public String getSelectedItem() {

		symbols.sort(Comparator.comparingInt(sym -> {
            var index = getSymbols().indexOf(sym);
            return index >= 0 ? index : Integer.MAX_VALUE;
        }));

        var sortedCost = new StringBuilder();
        symbols.forEach(sym->sortedCost.append("{").append(sym).append("}"));
        
        return sortedCost.toString();
	}
}
