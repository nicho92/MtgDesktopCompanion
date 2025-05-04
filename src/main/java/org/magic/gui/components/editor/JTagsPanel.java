package org.magic.gui.components.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.magic.services.MTGControler;

public class JTagsPanel extends JComponent {

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


	public JTagsPanel() {
		initGUI();
		tags = new ArrayList<>();
		isEditable = true;
	}

	private void initGUI() {
		setLayout(new BorderLayout(0, 0));

		panelTags = new JPanel();
		var flowLayout = (FlowLayout) panelTags.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(panelTags, BorderLayout.CENTER);

		panelAdds = new JPanel();
		add(panelAdds, BorderLayout.EAST);
		panelAdds.setLayout(new BorderLayout(0, 0));

		btnAdd = new JButton();

		AbstractAction action = new AbstractAction("+") {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent ae) {
				var field = new JTextField(10);
				panelTags.add(field);
				field.requestFocus();
				panelTags.revalidate();
				btnAdd.setEnabled(false);
				field.addActionListener(_ -> {
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
		componentFont = MTGControler.getInstance().getFont().deriveFont(Font.PLAIN, 15);

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

	public void bind(List<String> tagsList) {
		clean();
		this.tags = tagsList;
		tags.forEach(this::addLabel);

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

		if(isEditable)
			for (Component c : getComponents()) {
				if (c instanceof TagLabel t) {
						c.addMouseListener(new TagMouseListener(t));
				}
			}
	}

	public void clean() {
		panelTags.removeAll();
		panelTags.revalidate();
	}


	public void addTag(String t) {
		tags.add(t);
		addLabel(t);
	}


	public void addTags(List<String> list) {
		list.forEach(this::addTag);
	}

	public void removeTag(TagLabel tag) {
		tags.remove(tag.getText());
		panelTags.remove(tag);
		panelTags.revalidate();
		panelTags.repaint();
	}

	private void addLabel(String s) {
		var tab = new TagLabel(s, fontForeground, fontBackground, componentFont);

		if (isEditable)
			tab.addMouseListener(new TagMouseListener(tab));

		panelTags.add(tab);
		revalidate();
		repaint();
	}



	class TagMouseListener extends MouseAdapter {
		private TagLabel tagLabel;

		public TagMouseListener(TagLabel tagLabel) {
			this.tagLabel = tagLabel;
		}

		@Override
		public void mouseClicked(MouseEvent me) {
			me.consume();
			if (me.getClickCount() == clickcounttoDelete)
				removeTag(tagLabel);

		}

	}

	public String getTagAt(Point point) {
		try {
		TagLabel t = (TagLabel) panelTags.getComponentAt(point);
		return t.getText();
		}
		catch(Exception _)
		{
			return "";
		}
	}

}

class TagLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	public TagLabel(String t, Color f, Color b, Font font) {
		super(t);
		setFont(font);
		init(f,b);
	}

	public TagLabel(String t) {
		super(t);
		init(Color.BLACK,Color.WHITE);
	}

	private void init(Color f, Color b)
	{
		setForeground(f);
		setBackground(b);
		setBounds(new Rectangle(2, 2, 7, 2));
		setBorder(new CompoundBorder(new LineBorder(f, 1, true), new EmptyBorder(5, 5, 5, 5)));
		setOpaque(true);
	}

	@Override
	public int hashCode() {
		return getText().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;

		return this.getText().equals(((TagLabel) obj).getText());

	}

}
