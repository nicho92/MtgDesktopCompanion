package org.magic.gui.components.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class JTagsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private boolean isEditable;
	private Color fontForeground = Color.BLACK;
	private Color fontBackground = SystemColor.control;
	private Font componentFont;
	private JPanel panelTags;
	private JPanel panelAdds;
	private JButton btnAdd;
	private List<String> tags;
	private int clickcounttoDelete = 2;

	public void setClickcounttoDelete(int clickcounttoDelete) {
		this.clickcounttoDelete = clickcounttoDelete;
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.getContentPane().add(new JTagsPanel());
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
	}

	public JTagsPanel() {
		initGUI();
		tags = new ArrayList<>();
		isEditable = true;
	}

	private void initGUI() {
		setLayout(new BorderLayout(0, 0));

		panelTags = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelTags.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(panelTags, BorderLayout.CENTER);

		panelAdds = new JPanel();
		add(panelAdds, BorderLayout.EAST);
		panelAdds.setLayout(new BorderLayout(0, 0));

		btnAdd = new JButton();
		AbstractAction action = new AbstractAction("+") {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JTextField field = new JTextField(10);
				panelTags.add(field);
				field.requestFocus();
				panelTags.revalidate();
				btnAdd.setEnabled(false);
				field.addActionListener(e -> {
					String s = field.getText();
					panelTags.remove(field);
					if (!s.equals(""))
						addTag(s);

					btnAdd.setEnabled(true);
					btnAdd.requestFocus();
				});

			}
		};

		btnAdd.setAction(action);
		btnAdd.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ADD");
		btnAdd.getActionMap().put("ADD", action);
		panelAdds.add(btnAdd);
		componentFont = new Font("Tahoma", Font.PLAIN, 15);

	}

	public void setFontSize(int s) {
		componentFont = new Font(componentFont.getName(), componentFont.getStyle(), s);
	}

	public void setForegroundColor(Color f) {
		this.fontForeground = f;
	}

	public void setBackgroundColor(Color b) {
		this.fontBackground = b;
	}

	public void changeFont(Font f) {
		this.componentFont = f;
	}

	public List<String> getValues() {
		return tags;
	}

	public void bind(List<String> tags) {
		this.tags = tags;
		for (String s : tags)
			addLabel(s);
	}

	public void setColors(Color background, Color foreground) {
		this.fontBackground = background;
		this.fontForeground = foreground;
		for (Component c : getComponents()) {
			if (c instanceof TagLabel) {
				c.setBackground(background);
				c.setForeground(foreground);
			}

		}
	}

	public void setEditable(boolean l) {
		isEditable = l;
		panelAdds.setVisible(isEditable);

		if (isEditable)
			for (Component c : getComponents()) {
				if (c instanceof TagLabel) {
					c.addMouseListener(new TagMouseLisenter((TagLabel) c));
				}
			}
	}

	public void clean() {
		panelTags.removeAll();
		panelTags.revalidate();
	}

	public void addTags(List<String> list) {
		for (String t : list)
			addTag(t);
	}

	public void removeTag(TagLabel tag) {
		tags.remove(tag.getText());
		panelTags.remove(tag);
		panelTags.revalidate();
		panelTags.repaint();
	}

	private void addLabel(String s) {
		TagLabel tab = new TagLabel(s, fontForeground, fontBackground, componentFont);

		if (isEditable)
			tab.addMouseListener(new TagMouseLisenter(tab));

		panelTags.add(tab);
		revalidate();
		repaint();
	}

	public void addTag(String t) {
		tags.add(t);
		addLabel(t);
	}

	class TagMouseLisenter extends MouseAdapter {
		private TagLabel tagLabel;

		public TagMouseLisenter(TagLabel tagLabel) {
			this.tagLabel = tagLabel;
		}

		@Override
		public void mouseClicked(MouseEvent me) {
			me.consume();
			if (me.getClickCount() == clickcounttoDelete)
				removeTag(tagLabel);

		}

	}
}

class TagLabel extends JLabel {
	public TagLabel(String t, Color f, Color b, Font font) {
		super(t);
		setToolTipText(t);
		setForeground(f);
		setBackground(b);
		setOpaque(true);
		setFont(font);
		setBounds(new Rectangle(0, 0, 7, 2));
		setBorder(new CompoundBorder(new LineBorder(f, 1, true), new EmptyBorder(5, 5, 5, 5)));
	}

	@Override
	public int hashCode() {
		return getText().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (obj.getClass() != this.getClass())
			return false;

		return this.getText().equals(((TagLabel) obj).getText());

	}

}
