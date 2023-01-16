package org.magic.gui.components.widgets;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;

public class JFontChooser extends JComponent {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	private enum Styles {
		PLAIN(Font.PLAIN),
		BOLD(Font.BOLD),
		ITALIC(Font.ITALIC),
		BOTH(Font.BOLD|Font.ITALIC);
		private int fontStyle;

		Styles(int font)
		{
			this.fontStyle=font;
		}

		public int getStyles()
		{
			return fontStyle;
		}
	}


	private Integer[] fontSizes = new Integer[]{8, 9, 10, 11, 12, 14, 16, 18, 20,22, 24, 26, 28, 36, 48, 72};

	private JComboBox<String> cboFontFamilies;
	private JComboBox<Styles> cboFontStyles;
	private JComboBox<Integer> cboFontSize;

	public JFontChooser() {
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		cboFontFamilies = new JComboBox<>(new DefaultComboBoxModel<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
		add(cboFontFamilies);

		cboFontStyles = new JComboBox<>(new DefaultComboBoxModel<>(Styles.values()));
		add(cboFontStyles);

		cboFontSize = new JComboBox<>(new DefaultComboBoxModel<>(fontSizes));
		add(cboFontSize);


		cboFontFamilies.setRenderer((JList<? extends String> jlist, String f, int arg2,boolean arg3, boolean arg4)->{
			var ft = new Font(f, Font.PLAIN, 14);
			var l = new JLabel(f);
				   l.setFont(ft);
			return l;
		});

	}

	public void initFont(Font f)
	{
		cboFontFamilies.getModel().setSelectedItem(f.getFamily());
		cboFontSize.getModel().setSelectedItem(f.getSize());
		cboFontStyles.setSelectedIndex(f.getStyle());
	}


	@Override
	public Font getFont() {
		return new Font(cboFontFamilies.getSelectedItem().toString(), ((Styles)cboFontStyles.getSelectedItem()).getStyles(), Integer.parseInt(cboFontSize.getSelectedItem().toString()));
	}
}
