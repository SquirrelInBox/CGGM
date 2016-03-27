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
        normal = new Point(xNormal + middle.x, yNormal + middle.y);
        otherNormal = new Point(xNormal * (-1) + middle.x,
                yNormal * (-1) + middle.y);
    }

    public void setMiddle(double x, double y)
    {
        middle.x = x;
        middle.y = y;
        setNormal();
    }
}
