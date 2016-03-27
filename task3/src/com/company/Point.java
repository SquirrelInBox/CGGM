package com.company;

/**
 * Created by Helen on 22.03.2016.
 */
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
        return Math.abs(x - a.x) < 0.001 && Math.abs(y - a.y) < 0.001;
    }
}
