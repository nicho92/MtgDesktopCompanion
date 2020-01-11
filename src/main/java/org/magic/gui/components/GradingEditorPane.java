package org.magic.gui.components;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.Grading;
import org.magic.api.beans.MagicCardStock;
import org.magic.api.beans.PluginEntry;
import org.magic.api.interfaces.MTGGraders;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;
import org.magic.tools.UITools;

public class GradingEditorPane extends MTGUIComponent {
	
	private static final long serialVersionUID = 1L;
	private JTextField txtSerialNumber;
	private JSpinner spinnerSurface;
	private JSpinner spinnerThickness;
	private JSpinner spinnerWeight;
	private JSpinner spinnerGradeNote;
	private JSpinner spinnerCentering;
	private JSpinner spinnerCorner;
	private JSpinner spinnerEdges;
	private JComboBox<MTGGraders> cboGraders;
	private JComboBox<EnumCondition> cboMainGrade;
	private JComboBox<EnumCondition> cboSubGrade;
	private JButton btnSave;
	private JCheckBox chbGradded;
	
	public void initGUI(Grading grade) {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{166, 136, 69, 149, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		chbGradded = new JCheckBox("Gradded");
		cboGraders = UITools.createCombobox(MTGGraders.class,true);
		spinnerGradeNote = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));
		spinnerGradeNote.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD, 16));
		cboMainGrade = UITools.createCombobox(EnumCondition.values());
		cboSubGrade = UITools.createCombobox(EnumCondition.values());
		spinnerSurface = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));
		spinnerCentering = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));
		spinnerThickness = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));
		spinnerCorner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));
		spinnerWeight = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));
		spinnerEdges = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));
		txtSerialNumber = new JTextField(10);
		JButton btnLoad = new JButton(MTGControler.getInstance().getLangService().getCapitalize("LOAD"));
		btnSave = new JButton(MTGControler.getInstance().getLangService().getCapitalize("UPDATE"));
		
		
		
		add(new JLabel("Note :"), UITools.createGridBagConstraints(null, null, 2, 1));
		add(new JLabel("Grading :"), UITools.createGridBagConstraints(null, null, 0, 2));
		add(new JLabel("SubGrading :"), UITools.createGridBagConstraints(null, null, 2, 2));
		add(new JLabel("Surface :"), UITools.createGridBagConstraints(null, null, 0, 3));
		add(new JLabel("Centering :"), UITools.createGridBagConstraints(null, null, 2, 3));
		add(new JLabel("Thickness :"), UITools.createGridBagConstraints(null, null, 0, 4));
		add(new JLabel("Corners :"), UITools.createGridBagConstraints(null, null, 2, 4));
		add(new JLabel("Weight :"), UITools.createGridBagConstraints(null, null, 0, 5));
		add(new JLabel("Edges :"), UITools.createGridBagConstraints(null, null, 2, 5));
		add(new JLabel("Serial :"), UITools.createGridBagConstraints(null, null, 0, 6));
		
		add(chbGradded,UITools.createGridBagConstraints(null, null, 0, 0));
		add(cboGraders,UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 1));
		add(spinnerGradeNote, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 3, 1));
		add(cboMainGrade, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 2));
		add(cboSubGrade, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 3, 2));
		add(spinnerSurface, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 3));
		add(spinnerCentering, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 3, 3));
		add(spinnerThickness, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 4));
		add(spinnerCorner, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 3, 4));
		add(spinnerWeight, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 5));
		add(spinnerEdges, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 3, 5));
		add(txtSerialNumber, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 6,2,null));
		add(btnLoad, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 3, 6));
		add(btnSave, UITools.createGridBagConstraints(null, null, 0, 7,4,null));
		
		setGrading(grade);
		
		chbGradded.setSelected(false);
		
		btnLoad.addActionListener(al->{
			MTGGraders g = (MTGGraders) cboGraders.getSelectedItem();
			try {
				setGrading(g.loadGrading(txtSerialNumber.getText()));
			} catch (IOException e) {
				MTGControler.getInstance().notify(e);
			}
		});
		
		/*
		chbGradded.addItemListener(il->{
			
				System.out.println(il);
				spinnerCentering.setEnabled(chbGradded.isSelected());
				spinnerCorner.setEnabled(chbGradded.isSelected());
				spinnerEdges.setEnabled(chbGradded.isSelected());
				cboMainGrade.setEnabled(chbGradded.isSelected());
				cboSubGrade.setEnabled(chbGradded.isSelected());
				spinnerGradeNote.setEnabled(chbGradded.isSelected());
				cboGraders.setEnabled(chbGradded.isSelected());
				txtSerialNumber.setEnabled(chbGradded.isSelected());
				spinnerSurface.setEnabled(chbGradded.isSelected());
				spinnerThickness.setEnabled(chbGradded.isSelected());
				spinnerWeight.setEnabled(chbGradded.isSelected());
				setGrading(new Grading());
		});
		*/
	}
	
	public Grading getGrading()
	{
		Grading g = new Grading();
				g.setCentering((Double)spinnerCentering.getValue());
				g.setCorners((Double)spinnerCorner.getValue());
				g.setEdges((Double)spinnerEdges.getValue());
				g.setGrade((EnumCondition)cboMainGrade.getSelectedItem());
				g.setGradeNote((Double)spinnerGradeNote.getValue());
				g.setGraderName(cboGraders.getSelectedItem().toString());
				g.setNumberID(txtSerialNumber.getText());
				g.setSubGrade((EnumCondition)cboSubGrade.getSelectedItem());
				g.setSurface((Double)spinnerSurface.getValue());
				g.setThickness((Double)spinnerThickness.getValue());
				g.setWeight((Double)spinnerWeight.getValue());
		return g;
	}
	
	public void setGrading(Grading grade)
	{
			if(grade==null)
				grade=new Grading();
		
			chbGradded.setSelected(grade.isGradded());
			spinnerCentering.setValue(grade.getCentering());
			spinnerCorner.setValue(grade.getCorners());
			spinnerEdges.setValue(grade.getEdges());
			cboMainGrade.setSelectedItem(grade.getGrade());
			cboSubGrade.setSelectedItem(grade.getSubGrade());
			spinnerGradeNote.setValue(grade.getGradeNote());
			
			if(grade.isGradded())
				cboGraders.setSelectedItem(PluginRegistry.inst().getPlugin(grade.getGraderName(),MTGGraders.class));
			else
				cboGraders.setSelectedItem(null);
			
			txtSerialNumber.setText(grade.getNumberID());
			spinnerSurface.setValue(grade.getSurface());
			spinnerThickness.setValue(grade.getThickness());
			spinnerWeight.setValue(grade.getWeight());
	}
	
	
	public void saveTo(MagicCardStock stock)
	{
		if(!chbGradded.isSelected())
			stock.setGrade(null);
		else
			stock.setGrade(getGrading());
		
		stock.setUpdate(true);
	}
	
	
	public GradingEditorPane()
	{
		initGUI(new Grading());
	}

	
	@Override
	public String getTitle() {
		return "Grading";
	}
	
	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_GRADING;
	}


	public JButton getBtnSave() {
		return btnSave;
	}

}
