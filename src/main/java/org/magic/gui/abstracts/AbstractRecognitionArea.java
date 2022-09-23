package org.magic.gui.abstracts;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.event.MouseInputListener;

import org.magic.api.interfaces.MTGCardRecognition;
import org.magic.services.recognition.MatchResult;

public abstract class AbstractRecognitionArea implements MouseInputListener
{
    public abstract List<MatchResult> recognize(BufferedImage in, MTGCardRecognition strat,int recogTresh);

    public abstract String getName();

    public abstract void draw(Graphics g);

    @Override
	public String toString()
	{
		return getName();
    }

    public void init(int width, int height) {
    	//do nothing
    }

}