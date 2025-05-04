package org.magic.gui.components;

import static org.magic.services.tools.MTG.capitalize;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;

import org.magic.api.beans.MTGCardStock;
import org.magic.api.beans.MTGGrading;
import org.magic.api.beans.enums.EnumCondition;
import org.magic.api.interfaces.MTGGraders;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.MTGControler;
import org.magic.services.PluginRegistry;
import org.magic.services.threads.ThreadManager;
import org.magic.services.tools.MTG;
import org.magic.services.tools.UITools;
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
	private JLabel lblCertified= new JLabel(MTGConstants.ICON_CHECK);

	public void initGUI(MTGGrading grade) {
		var gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{166, 136, 69, 149, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		chbGradded = new JCheckBox("Gradded");
		cboGraders = UITools.createComboboxPlugins(MTGGraders.class,true);
		spinnerGradeNote = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));
		spinnerGradeNote.setFont(MTGControler.getInstance().getFont().deriveFont(Font.BOLD, 16));
		cboMainGrade = UITools.createCombobox(EnumCondition.values());
		cboSubGrade = UITools.createCombobox(EnumCondition.values());
		spinnerSurface = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));
		spinnerCentering = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));
		spinnerCorner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));
		spinnerEdges = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.5));


		spinnerThickness = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.1));
		((JSpinner.NumberEditor)spinnerThickness.getEditor()).getFormat().setMaximumFractionDigits(3);
		spinnerWeight = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10.0, 0.1));
		((JSpinner.NumberEditor)spinnerWeight.getEditor()).getFormat().setMaximumFractionDigits(2);


		txtSerialNumber = new JTextField(10);
		var btnLoad = UITools.createBindableJButton(capitalize("LOAD"), MTGConstants.ICON_WEBSITE_24, KeyEvent.VK_L, "Grade info loading");
		btnSave = UITools.createBindableJButton(capitalize("UPDATE"), MTGConstants.ICON_SAVE, KeyEvent.VK_G, "Grade info saving");



		add(new JLabel("Note :"), UITools.createGridBagConstraints(null, null, 2, 1));
		add(new JLabel("Grading :"), UITools.createGridBagConstraints(null, null, 0, 2));

		add(new JLabel("Centering :"), UITools.createGridBagConstraints(null, null, 2, 3));
		add(new JLabel("Thickness :"), UITools.createGridBagConstraints(null, null, 0, 4));
		add(new JLabel("Corners :"), UITools.createGridBagConstraints(null, null, 2, 4));
		add(new JLabel("Weight :"), UITools.createGridBagConstraints(null, null, 0, 5));
		add(new JLabel("Edges :"), UITools.createGridBagConstraints(null, null, 2, 5));
		add(new JLabel("Serial :"), UITools.createGridBagConstraints(null, null, 0, 6));

		add(chbGradded,UITools.createGridBagConstraints(null, null, 0, 0));
		add(lblCertified,UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 0, 1));
		add(cboGraders,UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 1));
		add(spinnerGradeNote, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 3, 1));
		add(cboMainGrade, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 2));

		add(new JLabel("SubGrading :"), UITools.createGridBagConstraints(null, null, 0, 3));
		add(cboSubGrade, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1,3));

		add(new JLabel("Surface :"), UITools.createGridBagConstraints(null, null, 2, 2));
		add(spinnerSurface, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 3, 2));

		add(spinnerCentering, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 3, 3));
		add(spinnerThickness, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 4));
		add(spinnerCorner, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 3, 4));
		add(spinnerWeight, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 5));
		add(spinnerEdges, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 3, 5));
		add(txtSerialNumber, UITools.createGridBagConstraints(null, GridBagConstraints.HORIZONTAL, 1, 6));
		add(btnLoad, UITools.createGridBagConstraints(GridBagConstraints.WEST, null, 2, 6));
		add(btnSave, UITools.createGridBagConstraints(null, null, 0, 7,4,null));

		setGrading(grade);

		chbGradded.setSelected(false);

		btnLoad.addActionListener(_->{
			MTGGraders grader = (MTGGraders) cboGraders.getSelectedItem();

			if(grader==null)
			{
				MTG.notifyError("Choose a Grader");
				return;
			}


			btnLoad.setEnabled(false);

			SwingWorker<MTGGrading, MTGGrading> sw = new SwingWorker<>(){

				@Override
				protected MTGGrading doInBackground() throws Exception {
					return grader.loadGrading(txtSerialNumber.getText());
				}

				@Override
				protected void done() {
					MTGGrading grad;
					btnLoad.setEnabled(true);

					try {
						grad = get();
						if(grad!=null)
						{
							grad.setCertified(true);
							setGrading(grad);
						}
					}
					catch(InterruptedException _)
					{
							Thread.currentThread().interrupt();
					}
					catch (Exception e) {
						MTGControler.getInstance().notify(e);
					}
				}
			};



			ThreadManager.getInstance().runInEdt(sw, "checking grading");
		});


	}

	public MTGGrading getGrading()
	{
		var g = new MTGGrading();
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
				g.setCertified(lblCertified.isVisible());
				g.setGradeDate(UITools.parseDate(lblCertified.getText()));
		return g;
	}

	public void setGrading(MTGGrading grade)
	{
			if(grade==null)
				grade=new MTGGrading();

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
			lblCertified.setVisible(grade.isCertified());

			if(grade.isCertified()) {
				lblCertified.setText(UITools.formatDate(grade.getGradeDate()));
				lblCertified.setToolTipText("Certified by " + grade.getGraderName()+ " : " + grade.getUrlInfo());
			}

	}


	public void saveTo(MTGCardStock stock)
	{
		if(!chbGradded.isSelected())
			stock.setGrade(null);
		else
			stock.setGrade(getGrading());

		stock.setUpdated(true);
	}


	public GradingEditorPane()
	{
		initGUI(new MTGGrading());
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
