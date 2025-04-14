package org.magic.gui.components.tech;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.magic.gui.abstracts.MTGUIComponent;
import org.magic.services.MTGConstants;
import org.magic.services.tools.BeanTools;

public class ObjectViewerPanel extends MTGUIComponent {

	private static final long serialVersionUID = 1L;
	private RSyntaxTextArea textpane;
	private JRadioButton rdoJson;
	private JRadioButton rdoMemory;
	private JRadioButton rdoBeanUtils;
	private transient Object currentObject;

	public ObjectViewerPanel() {
		setLayout(new BorderLayout());
		textpane = new RSyntaxTextArea();
		textpane.setLineWrap(true);
		textpane.setEditable(false);
		textpane.setWrapStyleWord(true);
	
		add(new JScrollPane(textpane),BorderLayout.CENTER);
		setPreferredSize(new Dimension(1,400));
		var panel = new JPanel();
		add(panel, BorderLayout.NORTH);

		rdoJson = new JRadioButton("Json");
		rdoJson.setSelected(true);
		rdoMemory = new JRadioButton("Memory");
		rdoBeanUtils = new JRadioButton("Bean");
		var group = new ButtonGroup();
					group.add(rdoJson);
					group.add(rdoMemory);
					group.add(rdoBeanUtils);
		panel.add(rdoJson);
		panel.add(rdoMemory);
		panel.add(rdoBeanUtils);

		rdoJson.addItemListener(_->init(currentObject));
		rdoMemory.addItemListener(_->init(currentObject));
		rdoBeanUtils.addItemListener(_->init(currentObject));
	}

	public void init(Object mc) {
		currentObject = mc;

		if(currentObject==null)
			return;

		if(rdoJson.isSelected())
		{
			textpane.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
			textpane.setText(BeanTools.toJson(currentObject));
			textpane.setCaretPosition(0);
		}
		else if(rdoMemory.isSelected())
		{
			textpane.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_INI);
			textpane.setText(BeanTools.toMemory(currentObject));
		}
		else
		{
			textpane.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_INI);
			textpane.setText(BeanTools.toString(currentObject,"\t"));
		}

	}

	@Override
	public String getTitle() {
		return "Object";
	}

	@Override
	public ImageIcon getIcon() {
		return MTGConstants.ICON_TAB_JSON;
	}


}
