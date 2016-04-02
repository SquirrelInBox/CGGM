package com.company;

import javafx.util.Pair;

/**
 * Created by Helen on 22.03.2016.
 */
public class Circle {
    Point center;
    double r;
    public Circle(Point center, double r)
    {
        this.center = center;
        this.r = r;
    }

    public boolean inCircle(Point a)
    {
        double delta = Math.abs((a.x - center.x)*(a.x - center.x) + (a.y - center.y)*(a.y - center.y) - r*r);
        return delta < 0.01;
    }

    private Pair<Double, Double> getX(Section ab)
    {
        Point a = ab.a;
        Point b = ab.b;
        double x1 = a.x;
        double x2 = b.x;
        double y1 = a.y;
        double y2 = b.y;
        double ySubSqr = (y2 - y1)*(y2 - y1);
        double xSubSqr = (x2 - x1)*(x2 - x1);

        double cK = (y2 - y1) / (x2 - x1);
        double cB = -x1*cK + y1;

        double A = 1 + cK*cK;
        double B = center.x - cK*cB + cK*center.y;
        double C = center.x*center.x + cB*cB +center.y*center.y - 2*cB*center.y - r*r;

        double discr = B*B - A*C;
        if (discr < 0)
            return null;
        discr  = Math.sqrt(discr);

        double xr1 = (B + discr) / A;
        double xr2 = (B - discr) / A;
        return new Pair<>(xr1, xr2);
    }

    public Pair<Point, Point> getISectCircleLine(Section ab)
    {
        Point a = ab.a;
        Point b = ab.b;
        double x1 = 0, x2 = 0, y1 = 0, y2 = 0;
        if (a.x != b.x)
        {
            Pair<Double, Double> X = getX(new Section(new Point(a.x, a.y), new Point(b.x, b.y)));
            if (X == null)
                return null;

            x1 = X.getKey();
            y1 = a.y + x1*(b.y - a.y)/(b.x - a.x) - a.x*(b.y - a.y)/(b.x - a.x);

            x2 = X.getValue();
            y2 = a.y + x2*(b.y - a.y)/(b.x - a.x) - a.x*(b.y - a.y)/(b.x - a.x);
        } else {
            Pair<Double, Double> Y = new Circle(new Point(center.y, center.x), r).getX(new Section(new Point(a.y, a.x), new Point(b.y, b.x))                    );
            if (Y == null)
                return null;

            y1 = Y.getKey();
            x1 = a.x + y1*(b.x - a.x)/(b.y - a.y) - a.y*(b.x - a.x)/(b.y - a.y);

            y2 = Y.getValue();
            x2 = a.x + y2*(b.x - a.x)/(b.y - a.y) - a.y*(b.x - a.x)/(b.y - a.y);
        }
        if (!inCircle(new Point(x1, y1)) || !inCircle(new Point(x2, y2)))
            return null;
        return new Pair<>(new Point(x1, y1), new Point(x2, y2));
    }
}
