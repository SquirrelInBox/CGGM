package com.company;

/**
 * Created by Helen on 22.03.2016.
 */
public class Section {
    Point a;
    Point b;
    Point middle;
    Point normal;
    Point otherNormal;

    public Section(Point point1, Point point2)
    {
        this.a = point1;
        this.b = point2;
        setMiddle();
        setNormal();
    }

    public void setMiddle()
    {
        double x = (a.x + b.x) / 2;
        double y = (a.y + b.y) / 2;
        middle = new Point(x, y);
    }

    public void setNormal()
    {
        double xNormal = (a.y - b.y);
        double yNormal = (b.x - a.x);
        otherNormal = new Point(xNormal + middle.x, yNormal + middle.y);
        normal = new Point(xNormal * (-1) + middle.x,
                yNormal * (-1) + middle.y);
    }

    public void setMiddle(double x, double y)
    {
        middle.x = x;
        middle.y = y;
        setNormal();
    }

    public boolean equals(Section cd)
    {
        return a.equals(cd.a) && b.equals(cd.b) ||a.equals(cd.b) && b.equals(cd.a);
    }

    public Point getPointIntersect(Section cd)
    {
        Point c = cd.a;
        Point d = cd.b;

        double x = (a.x*(b.y - a.y)*(d.x-c.x) - c.x*(d.y-c.y)*(b.x - a.x) + (c.y - a.y)*(b.x - a.x)*(d.x - c.x))
                / ((b.y - a.y)*(d.x - c.x) - (d.y - c.y)*(b.x - a.x));
        double y;
        if (b.x != a.x)
        {
            y = (x - a.x)*(b.y - a.y) / (b.x - a.x) + a.y;
        } else {
            y = (x - c.x)*(d.y - c.y) / (d.x - c.x) + c.y;
        }
        return new Point(x, y);
    }

    public double getAlpha(Point c)
    {
        double x1 = a.x;
        double y1 = a.y;
        double x2 = b.x;
        double y2 = b.y;
        double alpha =  x1 != x2 ? (c.x - x2) / (x1 - x2) : (c.y - y2) / (y1 - y2);
        return alpha;
    }
}
