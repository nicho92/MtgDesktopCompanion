package org.magic.gui.components.widgets;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

public class JTextFieldFileChooser extends JComponent {
	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private int mode = JFileChooser.FILES_ONLY;


	public JTextFieldFileChooser() {
		init();
	}

	public JTextFieldFileChooser(int size) {
		init();
		textField.setColumns(size);
	}

	public JTextFieldFileChooser(int size,int mode) {
		init();
		textField.setColumns(size);
		this.mode=mode;
	}

	public JTextFieldFileChooser(int size,int mode,String def) {
		init();
		textField.setColumns(size);
		this.mode=mode;
		textField.setText(def);
	}

	public JTextFieldFileChooser(String def,int mode) {
		init();
		this.mode=mode;
		textField.setText(def);
	}

	public void setMode(int mode) {
		this.mode = mode;
	}


	private void init()
	{
		setLayout(new BorderLayout(0, 0));
		var btnOpenDialog = new JButton("...");
		btnOpenDialog.addActionListener(ae->{
			var f = new JFileChooser(textField.getText());
			 			 f.setFileSelectionMode(mode);

			int res = f.showOpenDialog(null);
			if(res==JFileChooser.APPROVE_OPTION)
				textField.setText(f.getSelectedFile().getAbsolutePath());


		});



		add(btnOpenDialog, BorderLayout.EAST);

		textField = new JTextField();
		add(textField, BorderLayout.CENTER);
		textField.setColumns(10);
	}


	public JTextField getTextField() {
		return textField;
	}

	public File getFile()
	{
		return new File(textField.getText());
	}


}
