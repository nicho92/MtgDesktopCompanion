package org.magic.services.recognition;
import georegression.struct.point.Point2D_F32;

public class PointCluster implements Comparable<PointCluster>
{
    private Point2D_F32 loc;
    private int count;
    private float totalLen;

    public PointCluster(Point2D_F32 init, float len)
    {
        loc = init.copy();
        count = 1;
        totalLen = len;
    }

    public boolean testAndAdd(Point2D_F32 p, float len, float thresh)
    {
        float dist = loc.distance(p);
        if (dist <= thresh)
        {
            loc.x = ((loc.x * count) + p.x) / (count + 1);
            loc.y = ((loc.y * count) + p.y) / (count + 1);
            count += 1;
            totalLen += len;
            return true;
        }
        return false;
    }

    public float getTotalLen()
    {
        return totalLen;
    }

    public Point2D_F32 getPoint()
    {
        return loc;
    }

    @Override
    public int compareTo(PointCluster o)
    {
             return Double.compare(getTotalLen(), o.getTotalLen());

    }
}
