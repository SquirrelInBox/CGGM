package com.company;


public class Point {
    double x;
    double y;
    public Point(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Point a)
    {
        double delta1 = Math.abs(x - a.x);
        double delta2 = Math.abs(y - a.y);
        return delta1 < 0.001 && delta2 < 0.001;
    }

    public double VP(Point b)
    {
        return x*b.y - y*b.x;
    }

    public double getSqrDistance(Point b)
    {
        return (b.x - x)*(b.x - x) + (b.y - y)*(b.y - y);
    }

    public double getDistToLine(Section bc)
    {
        Point a = bc.a;
        Point b = bc.b;
        double A = b.y - a.y;
        double B = -b.x + a.x;
        double C = a.y*(b.x - a.x) - a.x*(b.y - a.y);
        return Math.abs(A*x + B*y + C) / Math.sqrt(A*A + B*B);
    }
}
