package org.magic.services.recognition.area;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.magic.api.interfaces.MTGCardRecognition;
import org.magic.gui.abstracts.AbstractRecognitionArea;
import org.magic.services.recognition.ContourBoundingBox;
import org.magic.services.recognition.ImageDesc;
import org.magic.services.recognition.MatchResult;
import org.magic.tools.ImageTools;

import georegression.struct.point.Point2D_I32;

public class ManualAreaStrat extends AbstractRecognitionArea {

	private Point2D_I32[] points = new Point2D_I32[4];
	private ContourBoundingBox bound;
    private int draggingPoint = -1;
    private int width;
    private int height;

    @Override
    public ArrayList<MatchResult> recognize(BufferedImage in, MTGCardRecognition strat,int recogTresh) {
		ArrayList<MatchResult> res = new ArrayList<>();
		
		
		if(bound==null)
		{
			init(in.getWidth(), in.getHeight());
		}
		
		var norm = ImageTools.getScaledImage(bound.getTransformedImage(in,false));
		var flip = ImageTools.getScaledImage(bound.getTransformedImage(in,true));
    
        var id = new ImageDesc(norm,flip);
        var m = strat.getMatch(id, recogTresh/100f);
        if(m != null)
        {
            res.add(m);
        }
        return res;
    }

    private void updateBoundedZone()
	{
		List<Point2D_I32> pts = new ArrayList<>();
		for(var ix=0; ix<points.length; ix++)
		{
			pts.add(new Point2D_I32(points[ix].x,points[ix].y));
		}
		bound = new ContourBoundingBox(pts);
	}

    @Override
    public String getName() {
        return "manual";
    }


    @Override
    public void mouseClicked(MouseEvent e) {
    	//do nothing
    }

    @Override
    public void mousePressed(MouseEvent e) {
    	var p = e.getPoint();
		for(var i=0;i<4;i++){
			Point2D_I32 pt = points[i];
			if(Math.abs(p.x-pt.x)<=3 && Math.abs(p.y-pt.y)<=3)
			{
				draggingPoint = i;
				return;
			}
		}
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        draggingPoint = -1;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    	//do nothing
    }

    @Override
    public void mouseExited(MouseEvent e) {
    	//do nothing
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(draggingPoint != -1)
		{
        	var p = e.getPoint();
			if(p.x >= 0 && p.x <= this.width)
			{
				points[draggingPoint].x = p.x;
			}
			if(p.y >= 0 && p.y <= this.height)
			{
				points[draggingPoint].y = p.y;
			}
		}
		updateBoundedZone();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    	//do nothing
    }

    @Override
    public void draw(Graphics g) {
		drawBounds(g);
    }

    @Override
	public void init(int width, int height)
	{
        this.width = width;
        this.height = height;
		int h = (height*8/10);
		int w = height*50/88;
		int x = (width/2-w/2);
		int y = (height/2-h/2);

		points[0] = new Point2D_I32(x,y);
		points[1] = new Point2D_I32(x+w,y);
		points[2] = new Point2D_I32(x+w,y+h);
		points[3] = new Point2D_I32(x,y+h);
		updateBoundedZone();
	}

    public void drawBounds(Graphics g)
	{
		g.setColor(Color.WHITE);
		bound.draw(g);
		for(var i=0;i<4;i++){
			Point2D_I32 p = points[i];
			if(draggingPoint == i)
			{
				g.setColor(Color.RED);
			}
			else
			{
				g.setColor(Color.WHITE);
			}
			g.fillOval(p.x-3, p.y-3, 7, 7);
		}
	}

}