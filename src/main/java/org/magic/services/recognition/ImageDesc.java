package org.magic.services.recognition;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.ddogleg.struct.FastQueue;

import boofcv.abst.feature.associate.AssociateDescription;
import boofcv.abst.feature.associate.ScoreAssociation;
import boofcv.abst.feature.detdesc.DetectDescribePoint;
import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.alg.descriptor.UtilFeature;
import boofcv.alg.enhance.EnhanceImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.core.image.ConvertImage;
import boofcv.factory.feature.associate.FactoryAssociation;
import boofcv.factory.feature.detdesc.FactoryDetectDescribe;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.feature.AssociatedIndex;
import boofcv.struct.feature.BrightFeature;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayU8;
import georegression.struct.point.Point2D_F64;


public class ImageDesc
{

	private static float detectThreshold = 10;
    private static int extractRadius = 2;
    private static int initialSampleSize = 2;
    private static int initialSize = 9;
    private static int numberScalesPerOctave = 3;
    private static int numberOfOctaves = 4;
    private static int maxFeaturesPerScale = 50;


    private static DetectDescribePoint<GrayF32, BrightFeature> detDesc =
            FactoryDetectDescribe.surfStable(
                    getHessianConf(),
                    null,
                    null,
                    GrayF32.class
            );

    private static ConfigFastHessian getHessianConf()
	{
		return new ConfigFastHessian(detectThreshold, extractRadius, maxFeaturesPerScale,initialSampleSize, initialSize, numberScalesPerOctave, numberOfOctaves);
	}


    private static ScoreAssociation<BrightFeature> scorer =
            FactoryAssociation.defaultScore(detDesc.getDescriptionType());
    private static AssociateDescription<BrightFeature> associate =
            FactoryAssociation.greedy(scorer, 8, true);

    private AverageHash hash;
    private AverageHash flipped;
    private FastQueue<BrightFeature> desc = UtilFeature.createQueue(detDesc, 0);
    private List<Point2D_F64> points = new ArrayList<>(0);
    private int size;

    public ImageDesc(BufferedImage in, BufferedImage flipin)
    {
        if (!AverageHash.isInitiated())
        {
            AverageHash.init(2, 2);
        }
        hash = AverageHash.avgHash(in, 2, 2);
        if (flipin != null)
        {
            flipped = AverageHash.avgHash(flipin, 2, 2);
        }
        var histogram = new int[256];
        var transform = new int[256];
        var img = ConvertBufferedImage.convertFromSingle(in, null, GrayU8.class);
        var norm = img.createSameShape();
        ImageStatistics.histogram(img, 0, histogram);
        EnhanceImageOps.equalize(histogram, transform);
        EnhanceImageOps.applyTransform(img, transform, norm);
        var normf = new GrayF32(img.width, img.height);
        ConvertImage.convert(norm, normf);
        desc.reset();
        size = describeImage(normf, desc, points);
    }

    public ImageDesc(BufferedImage in)
    {
        this(in, null);
    }

    public ImageDesc(FastQueue<BrightFeature> d, List<Point2D_F64> p, AverageHash h)
    {
        desc = d;
        hash = h;
        points = p;
        size = p.size();
    }

    public void writeOut(DataOutputStream out) throws IOException
    {
        out.writeInt(size);
        for (var i = 0; i < size; i++)
        {
            BrightFeature f = desc.get(i);
            for (double val : f.value)
            {
                out.writeDouble(val);
            }
            Point2D_F64 pt = points.get(i);
            out.writeDouble(pt.x);
            out.writeDouble(pt.y);
        }
        hash.writeOut(out);
    }

    public static ImageDesc readIn(ByteBuffer buf)
    {
    	var size = buf.getInt();
        List<Point2D_F64> points = new ArrayList<>(size);
        FastQueue<BrightFeature> descs = UtilFeature.createQueue(detDesc, size);
        for (var i = 0; i < size; i++)
        {
        	var f = detDesc.createDescription();
            for (var j = 0; j < f.size(); j++)
            {
                f.value[j] = buf.getDouble();
            }
            descs.add(f);
            points.add(new Point2D_F64(
                    buf.getDouble(), buf.getDouble()
            ));
        }
        var hash = AverageHash.readIn(buf);
        return new ImageDesc(descs, points, hash);
    }

    public static int describeImage(GrayF32 input, FastQueue<BrightFeature> descs, List<Point2D_F64> points)
    {
        detDesc.detect(input);
        var size = detDesc.getNumberOfFeatures();
        for (var i = 0; i < size; i++)
        {
            descs.grow().setTo(detDesc.getDescription(i));
            points.add(detDesc.getLocation(i));
        }
        return size;
    }

    public double compareSURF(ImageDesc i2)
    {
        associate.setSource(desc);
        associate.setDestination(i2.desc);
        associate.associate();

        double max = Math.max(desc.size(), i2.desc.size());
        FastQueue<AssociatedIndex> matches = associate.getMatches();
        double score = 0;
        for (var i = 0; i < matches.size(); i++)
        {
            AssociatedIndex match = matches.get(i);
            score += 1 - match.fitScore;
        }
        score = score / max;
        return score;
    }

    public double compareHash(ImageDesc i2)
    {
        return hash.match(i2.hash);
    }

    public double compareHashWithFlip(ImageDesc i2)
    {
        return Math.max(hash.match(i2.hash), flipped.match(i2.hash));
    }
}