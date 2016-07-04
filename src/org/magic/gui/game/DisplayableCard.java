package org.magic.gui.game;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import org.magic.api.beans.MagicCard;
import org.magic.gui.game.transfert.TransferableCard;
import org.magic.services.games.PositionEnum;

public class DisplayableCard extends JLabel implements MouseListener, MouseMotionListener, DragGestureListener{

	
	private MagicCard magicCard;
	private boolean tapped=false;
	private ImageIcon image;
	private boolean draggable=false;
	private PositionEnum origine;
	
	
	private String title;
	private String bottom;
	private int x,y;
	
	
	
	public PositionEnum getOrigine() {
		return origine;
	}


	public void setOrigine(PositionEnum origine) {
		this.origine = origine;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
		setText(title);
	}


	public String getBottom() {
		return bottom;
	}


	public void setBottom(String bottom) {
		this.bottom = bottom;
	}


	public boolean isDraggable() {
		return draggable;
	}


	public void enableDrag(boolean drag) {
		this.draggable = drag;
	}


	public DisplayableCard(MagicCard mc,int width,int height) {
		setSize(width, height);
		setHorizontalAlignment(JLabel.CENTER);
		setVerticalAlignment(JLabel.CENTER);
		
		magicCard=mc;
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		DragSource ds = new DragSource();
		ds.createDefaultDragGestureRecognizer(this,DnDConstants.ACTION_MOVE, this);
		
		
		URL url;
		try {
			url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+mc.getEditions().get(0).getMultiverse_id()+"&type=card");
			image = new ImageIcon(ImageIO.read(url).getScaledInstance(getWidth(), getHeight(), Image.SCALE_FAST));
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	@Override
	public Icon getIcon() {
		
		if(magicCard != null)
				return image;
		
		return super.getIcon();
	}
	
	
	public MagicCard getMc() {
		return magicCard;
	}
	public void setMc(MagicCard mc) {
		this.magicCard = mc;
	}
	public boolean isTapped() {
		return tapped;
	}
	public void setTapped(boolean tapped) {
		this.tapped = tapped;
	}


	@Override
	public void dragGestureRecognized(DragGestureEvent event) {
		Cursor cursor = null;
        if (event.getDragAction() == DnDConstants.ACTION_MOVE) {
            cursor = DragSource.DefaultMoveDrop;
        }
       	event.startDrag(cursor, new TransferableCard(this));
	}


	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		
	}


	@Override
	public void mouseEntered(MouseEvent arg0) {
		setBorder(new LineBorder(Color.red));
		
		System.out.println(magicCard + " " + getOrigine());
		
		
		if(isDraggable())
			this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
		
	}


	@Override
	public void mouseExited(MouseEvent arg0) {
		setBorder(null);
		
		
	}


	@Override
	public void mousePressed(MouseEvent me) {
		//this.setLocation(me.getPoint());
	}


	@Override
	public void mouseReleased(MouseEvent me) {
		
	}


	
	public String toString()
	{
		return getText(); 
	}


	@Override
	public void mouseDragged(MouseEvent me) {
		
		
	}


	@Override
	public void mouseMoved(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}
	
}
