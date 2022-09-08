package org.magic.services.recognition;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.magic.services.logging.MTGLogger;

import boofcv.alg.distort.RemovePerspectiveDistortion;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.ConfigLength;
import boofcv.struct.ConnectRule;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayS32;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageType;
import boofcv.struct.image.Planar;
import georegression.struct.point.Point2D_F64;
import georegression.struct.point.Point2D_I32;

public class ContourBoundingBox
{
    private static Point2D_I32[] farpoints;
    protected Logger logger = MTGLogger.getLogger(this.getClass());

    static
    {
        farpoints = new Point2D_I32[4];
        farpoints[0] = new Point2D_I32(-9999,-9999);
        farpoints[1] = new Point2D_I32(9999,-9999);
        farpoints[2] = new Point2D_I32(9999,9999);
        farpoints[3] = new Point2D_I32(-9999,9999);
    }

    private Point2D_I32[] corners;
    private Point2D_F64[] cornersf;
    private Point2D_I32[] midpoints;
    private double[] slopes;
    private double longestSide;
    private double shortestSide;

    public BufferedImage getTransformedImage(BufferedImage in, boolean flip)
	{
        try
        {
            Planar<GrayF32> input = ConvertBufferedImage.convertFromPlanar(in, null, true, GrayF32.class);

            RemovePerspectiveDistortion<Planar<GrayF32>> removePerspective =new RemovePerspectiveDistortion<>(300, 418, ImageType.pl(3, GrayF32.class));

            int start = longEdge();

            if(flip)
            {
                start = (start+2)%4;
            }
            
            
            input.setNumberOfBands(3);
            
            if( !removePerspective.apply(input,
                    new Point2D_F64(corners[start].x,corners[start].y),
                    new Point2D_F64(corners[(start+1)%4].x,corners[(start+1)%4].y),
                    new Point2D_F64(corners[(start+2)%4].x,corners[(start+2)%4].y),
                    new Point2D_F64(corners[(start+3)%4].x,corners[(start+3)%4].y)
                                    ) ){
                return null;
            }
            Planar<GrayF32> output = removePerspective.getOutput();
            return ConvertBufferedImage.convertTo_F32(output,null,true);
        }
		catch(Exception e)
		{
			logger.error("RemovePerspectiveDistortion.apply Error: " + e);
			return in;
		}
	}
    
	 public static List<ContourBoundingBox> getContourBoundingBox(BufferedImage in)
	    {
	        List<ContourBoundingBox> bounds = new ArrayList<>();
	        GrayU8 img = ConvertBufferedImage.convertFromSingle(in, null, GrayU8.class);
	        GrayU8 binary = img.createSameShape();
	        GThresholdImageOps.localMean(img, binary, ConfigLength.fixed(20), 1.0, true, null, null,null);

	        GrayU8 filtered = BinaryImageOps.erode8(binary, 2, null);
	        var label = new GrayS32(img.width,img.height);

	        int imgArea = img.getHeight()*img.getWidth();

	        List<Contour> contours = BinaryImageOps.contour(filtered, ConnectRule.EIGHT, label);
	        bounds.clear();
	        for(Contour contour:contours)
	        {
	        	var bb = new ContourBoundingBox(contour.external);
	            double ratio = bb.area()/imgArea;
	            if(ratio > 0.05 && ratio < 0.5 && bb.isRoughlyRecttangular())
	            {
	                bounds.add(bb);
	            }
	        }
	        return bounds;
	    }

    public ContourBoundingBox(List<Point2D_I32> contour)
    {
        corners = new Point2D_I32[4];
        midpoints = new Point2D_I32[4];
        slopes = new double[4];
        cornersf = new Point2D_F64[4];

        for(Point2D_I32 p : contour)
        {
            for(var i=0; i<4; i++)
            {
                if(corners[i]==null)
                {
                    corners[i] = p.copy();
                }
                else
                {
                    double d1 = corners[i].distance(farpoints[i]);
                    double d2 = p.distance(farpoints[i]);
                    if(d2<d1)
                    {
                        corners[i] = p.copy();
                    }
                }
            }
        }

        for(var i=0; i<4; i++)
        {
            cornersf[i] = new Point2D_F64(corners[i].x, corners[i].y);
            int j = (i+1)%4;
            midpoints[i] = new Point2D_I32(
                (corners[i].x + corners[j].x)/2, 
                (corners[i].y + corners[j].y)/2
            );
            slopes[i] = slope(corners[i],corners[j]);
        }
        initLongShort();
    }

    public int longEdge()
    {
        double shortest = Integer.MAX_VALUE;
        var shortestIX = 0;
        for(var i=0; i<4; i++)
        {
            int j = (i+1)%4;
            double d = corners[i].distance(corners[j]);
            if(d<shortest)
            {
                shortest=d;
                shortestIX=i;
            }
        }
        return shortestIX%2;
    }

    private void initLongShort()
    {
        shortestSide = Integer.MAX_VALUE;
        longestSide = 0;
        for(var i=0; i<4; i++)
        {
            int j = (i+1)%4;
            double d = corners[i].distance(corners[j]);
            if(d<shortestSide)
            {
                shortestSide=d;
            }
            if(d>longestSide)
            {
                longestSide=d;
            }
        }
    }

    public double area()
    {
        return midpoints[0].distance(midpoints[2]) * 
            midpoints[1].distance(midpoints[3]);
    }

    public boolean isRoughlyRecttangular()
    {
        double d1 = midpoints[0].distance(midpoints[2]);
        double d2 = midpoints[1].distance(midpoints[3]);
        double ratio;
        if(d1>d2)
        {
            ratio = d1/d2;
        }
        else
        {
            ratio = d2/d1;
        }
        if(shortestSide != 0)
        {
            double shortLong = longestSide/shortestSide;
            return ratio > 1 && ratio < 2 && shortLong < 2;
        }
        else
        {
            return false;
        }
    }

    private static double slope(Point2D_I32 p1, Point2D_I32 p2)
    {
        if(p1.x == p2.x)
        {
            return 9999999;
        }
        else
        {
            return (p1.y-p2.y)/(double)(p1.x-p2.x);
        }
    }

    public void draw(Graphics g)
    {
        for(var i=0; i<4; i++)
        {
            if(i%2==longEdge()%2)
            {
                g.setColor(Color.RED);
            }
            else
            {
                g.setColor(Color.WHITE);
            }
            int j = (i+1)%4;
            g.drawLine(
                corners[i].x,
                corners[i].y,
                corners[j].x,
                corners[j].y
            );
        }
    }
}