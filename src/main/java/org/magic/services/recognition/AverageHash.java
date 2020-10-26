package org.magic.services.recognition;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class AverageHash
{
    private static BufferedImage gray;
    private static Graphics g;
    private static boolean initiated = false;

    private int width;
    private int height;
    private long[] digest;

    public static void init(int maxW, int maxH)
    {
        gray = new BufferedImage(8 * maxW, 8 * maxH, BufferedImage.TYPE_BYTE_GRAY);
        g = gray.getGraphics();
        initiated = true;
    }

    public static AverageHash avgHash(BufferedImage img, int w, int h)
    {
        g.drawImage(img.getScaledInstance(8 * w, 8 * h, Image.SCALE_FAST), 0, 0, null);

        AverageHash d = new AverageHash();
        d.digest = new long[w * h];
        d.width = w;
        d.height = h;

        int ix = 0;
        for (int i = 0; i < w; i++)
        {
            for (int j = 0; j < w; j++)
            {
                d.digest[ix] = hashChunk(gray.getSubimage(i * 8, j * 8, 8, 8));
                ix++;
            }
        }
        return d;
    }

    private static long hashChunk(BufferedImage chunk)
    {
        WritableRaster r = chunk.getRaster();
        long hash = 0;
        int avg = 0;
        for (int x = 0; x < 8; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                avg += r.getSample(x, y, 0);
            }
        }
        avg /= 64;
        for (int x = 0; x < 8; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                if (r.getSample(x, y, 0) < avg)
                {
                    hash |= 1;
                }
                hash <<= 1;
            }
        }
        return hash;
    }

    public double match(AverageHash b)
    {
        return matchingBits(b) / (64.0 * width * height);
    }

    public int matchingBits(AverageHash b)
    {
        if (width != b.width || height != b.height)
        {
            return -1;
        }
        int ix = width * height;

        int bits = 0;
        for (int i = 0; i < ix; i++)
        {
            long d1 = digest[i];
            long d2 = b.digest[i];
            for (int j = 0; j < 64; j++)
            {
                if ((d1 & 1) == (d2 & 1))
                {
                    bits++;
                }
                d1 >>>= 1;
                d2 >>>= 1;
            }
        }
        return bits;
    }

    public static boolean isInitiated()
    {
        return initiated;
    }

    public void writeOut(DataOutputStream out) throws IOException
    {
        out.writeInt(width);
        out.writeInt(height);
        for (int i = 0; i < width * height; i++)
        {
            out.writeLong(digest[i]);
        }
    }

    public static AverageHash readIn(ByteBuffer buf)
    {
        AverageHash d = new AverageHash();
        d.width = buf.getInt();
        d.height = buf.getInt();
        d.digest = new long[d.width * d.height];
        for (int i = 0; i < d.width * d.height; i++)
        {
            d.digest[i] = buf.getLong();
        }
        return d;
    }
}