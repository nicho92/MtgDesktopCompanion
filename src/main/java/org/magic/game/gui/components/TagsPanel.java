package org.magic.game.gui.components;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

public class TagsPanel extends JPanel {
	
	private boolean isEditable;
	private Color foreground=Color.BLACK;
	private Color background=SystemColor.activeCaption;
	private Font font;
	private JPanel panelTags;
	private JPanel panelAdds;
	private JButton btnAdd ;
	private List<String> tags;
	private int clickcounttoDelete=2; 
	
	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.getContentPane().add(new TagsPanel());
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
	}
	
	
	public TagsPanel() {
		initGUI();
		tags=new ArrayList<String>();
		isEditable=true;
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
		
		btnAdd = new JButton("+");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JTextField field = new JTextField(10);
				panelTags.add(field);
				panelTags.revalidate();
				btnAdd.setEnabled(false);
				field.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						String s = field.getText();
						panelTags.remove(field);
						if(!s.equals(""))
							addTag(s);
						btnAdd.setEnabled(true);
					}
				});
				
			}
		});
		
		panelAdds.add(btnAdd);
		font=new Font("Tahoma", Font.PLAIN, 15);
	}

	public void setFont(Font f)
	{
		this.font=f;
	}
	
	public List<String> getValues()
	{
		return tags;
	}
	
	public void bind(List<String> tags)
	{
		this.tags=tags;
		for(String s : tags)
			addLabel(s);
	}

	public void setColors(Color background,Color foreground)
	{
		this.background=background;
		this.foreground=foreground;
		for(Component c : getComponents())
		{
			if(c instanceof TagLabel)
			{ 
				c.setBackground(background);
				c.setForeground(foreground);
			}
			
		}
	}
	
	public void setEditable(boolean l)
	{
		isEditable=l;
		panelAdds.setVisible(isEditable);
		
	}
	
	public void clean()
	{
		panelTags.removeAll();
		panelTags.revalidate();
	}
	
	public void addTags(List<String> list)
	{
		for(String t : list)
			addTag(t);
	}
	
	public void removeTag(String s)
	{
		//TODO
	}
	
	public void removeTag(TagLabel tag)
	{
		tags.remove(tag.getText());
		panelTags.remove(tag);
		panelTags.revalidate();
		panelTags.repaint();
	}
	
	
	private void addLabel(String s)
	{
		TagLabel tab = new TagLabel(s,foreground,background,font);
		tab.addMouseListener(new TagMouseLisenter(tab));
		panelTags.add(tab);
		revalidate();
		repaint();
	}
	
	public void addTag(String t)
	{
		tags.add(t);
		addLabel(t);
	}

	class TagMouseLisenter extends MouseAdapter
    {
    	private TagLabel tagLabel;
    
    	public TagMouseLisenter(TagLabel tagLabel) {
    		this.tagLabel=tagLabel;
		}
    	
    	@Override
    	public void mouseClicked(MouseEvent me) {
    		me.consume();
    		if(me.getClickCount()==clickcounttoDelete)
    			removeTag(tagLabel);
    		
    	}
    	
    }
}


    
   

	class TagLabel extends JLabel 
	{
		public TagLabel(String t,Color f, Color b,Font font) {
			super(t);
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
		
	}
