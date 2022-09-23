package org.magic.services.recognition.area;
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

public class AutoDetectAreaStrat extends AbstractRecognitionArea {

    private ArrayList<MatchResult> results = new ArrayList<>();
    private List<ContourBoundingBox> bounds = new ArrayList<>();

    @Override
    public List<MatchResult> recognize(BufferedImage in, MTGCardRecognition strat,int recogTresh) {
        results.clear();
        bounds = ContourBoundingBox.getContourBoundingBox(in);

        for (ContourBoundingBox bound : bounds)
        {
            var norm = ImageTools.getScaledImage(bound.getTransformedImage(in,false));
            var flip = ImageTools.getScaledImage(bound.getTransformedImage(in,true));
            var i = new ImageDesc(norm,flip);

            MatchResult mr = strat.getMatch(i, recogTresh/100.0);
            if (mr != null) {
                results.add(mr);
            }

         }
        return results;
    }

    @Override
    public String getName() {
        return "auto-detect";
    }


    @Override
    public void mouseClicked(MouseEvent e) {
    	//do nothing
    }

    @Override
    public void mousePressed(MouseEvent e) {
    	//do nothing
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    	//do nothing
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
    	//do nothing
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    	//do nothing
    }

    @Override
    public void draw(Graphics g) {
        for(ContourBoundingBox bb : bounds)
        {
            bb.draw(g);
        }
    }

}