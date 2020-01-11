package org.magic.gui.components.editor;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.magic.api.beans.EnumCondition;
import org.magic.api.beans.Grader;
import org.magic.api.beans.Grading;
import org.magic.api.beans.MagicCardStock;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.extra.GraderServices;
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
	private JComboBox<Grader> cboGraders;
	private JComboBox<EnumCondition> cboMainGrade;
	private JComboBox<EnumCondition> cboSubGrade;
	private JButton btnSave;
	
	
	public void initGUI(Grading grade) {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{82, 136, 69, 149, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		cboGraders = UITools.createCombobox(GraderServices.inst().listGraders());
		GridBagConstraints gbccboGraders = new GridBagConstraints();
		gbccboGraders.insets = new Insets(0, 0, 5, 5);
		gbccboGraders.fill = GridBagConstraints.HORIZONTAL;
		gbccboGraders.gridx = 1;
		gbccboGraders.gridy = 1;
		add(cboGraders, gbccboGraders);
		
		JLabel lblNote = new JLabel("Note :");
		GridBagConstraints gbclblNote = new GridBagConstraints();
		gbclblNote.fill = GridBagConstraints.VERTICAL;
		gbclblNote.insets = new Insets(0, 0, 5, 5);
		gbclblNote.gridx = 2;
		gbclblNote.gridy = 1;
		add(lblNote, gbclblNote);
		
		spinnerGradeNote = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));
		spinnerGradeNote.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD, 16));
		GridBagConstraints gbcspinnerGradeNote = new GridBagConstraints();
		gbcspinnerGradeNote.fill = GridBagConstraints.HORIZONTAL;
		gbcspinnerGradeNote.insets = new Insets(0, 0, 5, 0);
		gbcspinnerGradeNote.gridx = 3;
		gbcspinnerGradeNote.gridy = 1;
		add(spinnerGradeNote, gbcspinnerGradeNote);
		
		cboMainGrade = UITools.createCombobox(EnumCondition.values());
		GridBagConstraints gbccboMainGrade = new GridBagConstraints();
		gbccboMainGrade.insets = new Insets(0, 0, 5, 5);
		gbccboMainGrade.fill = GridBagConstraints.HORIZONTAL;
		gbccboMainGrade.gridx = 1;
		gbccboMainGrade.gridy = 2;
		add(cboMainGrade, gbccboMainGrade);
		
		JLabel lblSubGrading = new JLabel("SubGrading :");
		GridBagConstraints gbclblSubGrading = new GridBagConstraints();
		gbclblSubGrading.insets = new Insets(0, 0, 5, 5);
		gbclblSubGrading.gridx = 2;
		gbclblSubGrading.gridy = 2;
		add(lblSubGrading, gbclblSubGrading);
		
		cboSubGrade = UITools.createCombobox(EnumCondition.values());
		GridBagConstraints gbccboSubGrade = new GridBagConstraints();
		gbccboSubGrade.insets = new Insets(0, 0, 5, 0);
		gbccboSubGrade.fill = GridBagConstraints.HORIZONTAL;
		gbccboSubGrade.gridx = 3;
		gbccboSubGrade.gridy = 2;
		add(cboSubGrade, gbccboSubGrade);
		
		JLabel lblSurface = new JLabel("Surface :");
		GridBagConstraints gbclblSurface = new GridBagConstraints();
		gbclblSurface.insets = new Insets(0, 0, 5, 5);
		gbclblSurface.gridx = 0;
		gbclblSurface.gridy = 3;
		add(lblSurface, gbclblSurface);
		
		spinnerSurface = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));
		GridBagConstraints gbcspinnerSurface = new GridBagConstraints();
		gbcspinnerSurface.fill = GridBagConstraints.HORIZONTAL;
		gbcspinnerSurface.insets = new Insets(0, 0, 5, 5);
		gbcspinnerSurface.gridx = 1;
		gbcspinnerSurface.gridy = 3;
		add(spinnerSurface, gbcspinnerSurface);
		
		JLabel lblCentering = new JLabel("Centering:");
		GridBagConstraints gbclblCentering = new GridBagConstraints();
		gbclblCentering.insets = new Insets(0, 0, 5, 5);
		gbclblCentering.gridx = 2;
		gbclblCentering.gridy = 3;
		add(lblCentering, gbclblCentering);
		
		spinnerCentering = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));
		GridBagConstraints gbcspinnerCentering = new GridBagConstraints();
		gbcspinnerCentering.fill = GridBagConstraints.HORIZONTAL;
		gbcspinnerCentering.insets = new Insets(0, 0, 5, 0);
		gbcspinnerCentering.gridx = 3;
		gbcspinnerCentering.gridy = 3;
		add(spinnerCentering, gbcspinnerCentering);
		
		JLabel lblThickness = new JLabel("Thickness :");
		GridBagConstraints gbclblThickness = new GridBagConstraints();
		gbclblThickness.insets = new Insets(0, 0, 5, 5);
		gbclblThickness.gridx = 0;
		gbclblThickness.gridy = 4;
		add(lblThickness, gbclblThickness);
		
		spinnerThickness = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));
		GridBagConstraints gbcspinnerThickness = new GridBagConstraints();
		gbcspinnerThickness.fill = GridBagConstraints.HORIZONTAL;
		gbcspinnerThickness.insets = new Insets(0, 0, 5, 5);
		gbcspinnerThickness.gridx = 1;
		gbcspinnerThickness.gridy = 4;
		add(spinnerThickness, gbcspinnerThickness);
		
		JLabel lblCorners = new JLabel("Corner :");
		GridBagConstraints gbclblCorners = new GridBagConstraints();
		gbclblCorners.insets = new Insets(0, 0, 5, 5);
		gbclblCorners.gridx = 2;
		gbclblCorners.gridy = 4;
		add(lblCorners, gbclblCorners);
		
		spinnerCorner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));
		GridBagConstraints gbcspinnerCorner = new GridBagConstraints();
		gbcspinnerCorner.fill = GridBagConstraints.HORIZONTAL;
		gbcspinnerCorner.insets = new Insets(0, 0, 5, 0);
		gbcspinnerCorner.gridx = 3;
		gbcspinnerCorner.gridy = 4;
		add(spinnerCorner, gbcspinnerCorner);
		
		JLabel lblWeight = new JLabel("Weight :");
		GridBagConstraints gbclblWeight = new GridBagConstraints();
		gbclblWeight.insets = new Insets(0, 0, 5, 5);
		gbclblWeight.gridx = 0;
		gbclblWeight.gridy = 5;
		add(lblWeight, gbclblWeight);
		
		spinnerWeight = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));
		GridBagConstraints gbcspinnerWeight = new GridBagConstraints();
		gbcspinnerWeight.fill = GridBagConstraints.HORIZONTAL;
		gbcspinnerWeight.insets = new Insets(0, 0, 5, 5);
		gbcspinnerWeight.gridx = 1;
		gbcspinnerWeight.gridy = 5;
		add(spinnerWeight, gbcspinnerWeight);
		
		JLabel lblEdges = new JLabel("Edges :");
		GridBagConstraints gbclblEdges = new GridBagConstraints();
		gbclblEdges.insets = new Insets(0, 0, 5, 5);
		gbclblEdges.gridx = 2;
		gbclblEdges.gridy = 5;
		add(lblEdges, gbclblEdges);
		
		spinnerEdges = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));
		GridBagConstraints gbcspinnerEdges = new GridBagConstraints();
		gbcspinnerEdges.insets = new Insets(0, 0, 5, 0);
		gbcspinnerEdges.fill = GridBagConstraints.HORIZONTAL;
		gbcspinnerEdges.gridx = 3;
		gbcspinnerEdges.gridy = 5;
		add(spinnerEdges, gbcspinnerEdges);
		
		JLabel lblNewLabel = new JLabel("Serial : ");
		GridBagConstraints gbclblNewLabel = new GridBagConstraints();
		gbclblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbclblNewLabel.gridx = 0;
		gbclblNewLabel.gridy = 6;
		add(lblNewLabel, gbclblNewLabel);
		
		txtSerialNumber = new JTextField(10);
		GridBagConstraints gbctxtSerialNumber = new GridBagConstraints();
		gbctxtSerialNumber.gridwidth = 3;
		gbctxtSerialNumber.insets = new Insets(0, 0, 5, 0);
		gbctxtSerialNumber.fill = GridBagConstraints.HORIZONTAL;
		gbctxtSerialNumber.gridx = 1;
		gbctxtSerialNumber.gridy = 6;
		add(txtSerialNumber, gbctxtSerialNumber);
		
		
		btnSave = new JButton(MTGControler.getInstance().getLangService().getCapitalize("UPDATE"));
		GridBagConstraints gbcbtnSave = new GridBagConstraints();
		gbcbtnSave.gridwidth = 4;
		gbcbtnSave.gridx = 0;
		gbcbtnSave.gridy = 7;
		add(btnSave, gbcbtnSave);
		
		
		setGrading(grade);
	}
	
	public Grading getGrading()
	{
		Grading g = new Grading();
				g.setCentering((Double)spinnerCentering.getValue());
				g.setCorners((Double)spinnerCorner.getValue());
				g.setEdges((Double)spinnerEdges.getValue());
				g.setGrade((EnumCondition)cboMainGrade.getSelectedItem());
				g.setGradeNote((Double)spinnerGradeNote.getValue());
				g.setGrader((Grader)cboGraders.getSelectedItem());
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
		
				spinnerCentering.setValue(grade.getCentering());
				spinnerCorner.setValue(grade.getCorners());
				spinnerEdges.setValue(grade.getEdges());
				cboMainGrade.setSelectedItem(grade.getGrade());
				cboSubGrade.setSelectedItem(grade.getSubGrade());
				spinnerGradeNote.setValue(grade.getGradeNote());
				cboGraders.setSelectedItem(grade.getGrader());
				txtSerialNumber.setText(grade.getNumberID());
				spinnerSurface.setValue(grade.getSurface());
				spinnerThickness.setValue(grade.getThickness());
				spinnerWeight.setValue(grade.getWeight());
	}
	
	
	public void saveTo(MagicCardStock stock)
	{
		if(cboGraders.getSelectedItem()==null)
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
