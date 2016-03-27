package com.company;

import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;


public class Main extends JPanel {
    static int width = 800;
    static int height = 600;
    int coef = 1;
    int xShift = 400;
    int yShift = 300;

    Vector<Point> polygon = new Vector<Point>();
    Vector<Section> bisectors = new Vector<Section>();
    Vector<Section> sections = new Vector<Section>();

    public void initPolygon()
    {
//        polygon.add(new Point(0, 1));
//        polygon.add(new Point(1, 0));
//        polygon.add(new Point(1, 2));
//        polygon.add(new Point(0, 2));

//        polygon.add(new Point(0, 0));
//        polygon.add(new Point(1, 0));
//        polygon.add(new Point(1, 2));
//        polygon.add(new Point(0, 1));

//        polygon.add(new Point(0,0));
//        polygon.add(new Point(1,0));
//        polygon.add(new Point(1,1));
//        polygon.add(new Point(0,2));

//        polygon.add(new Point(0, 1));
//        polygon.add(new Point(4, 0));
//        polygon.add(new Point(4, 3));
//        polygon.add(new Point(0, 3));
//        polygon.add(new Point(0, 2));

//        polygon.add(new Point(0, 0));
//        polygon.add(new Point(500, 0));
//        polygon.add(new Point(500, 100));
//        polygon.add(new Point(0, 100));

        polygon.add(new Point(0, 300));
        polygon.add(new Point(0, 0));
        polygon.add(new Point(300, -100));
        polygon.add(new Point(500, 0));
        polygon.add(new Point(500, 300));

    }

    private void addSection(Point a, Point b)
    {
        Section section = new Section(a, b);
        section.setMiddle();
        section.setNormal();
        sections.add(section);
    }

    public void initSections()
    {
        Point a = polygon.lastElement();
        Point b = polygon.firstElement();
        addSection(a, b);
        for(int i = 1; i < polygon.size(); i++)
        {
            a = polygon.get(i - 1);
            b = polygon.get(i);
            addSection(a, b);
        }
    }

    private double getSqrDistance(Point a, Point b)
    {
        return (b.x - a.x)*(b.x - a.x) + (b.y - a.y)*(b.y - a.y);
    }


    private Point getSection(double r, Point a, Point b)
    {
        double d = getSqrDistance(a, b);
        double x = a.x*Math.sqrt(r/d) + (1 - Math.sqrt(r/d))*b.x;
        double y = a.y*Math.sqrt(r/d) + (1 - Math.sqrt(r/d))*b.y;
        return new Point(x, y);
    }


    private boolean isParallelLines(Section ab, Section cd)
    {
        Point a = new Point(ab.a.x - ab.b.x, ab.a.y - ab.b.y);
        Point b = new Point(cd.a.x - cd.b.x, cd.a.y - cd.b.y);
        return VP(a, b) == 0;
    }

    private Point getLastPoint(Section ab, Point c)
    {
        if (ab.a.equals(c))
            return ab.b;
        if(ab.b.equals(c))
            return ab.a;
        double alpha = getAlpha(ab.a, new Section(ab.b, c));
        if (alpha <= 0 || alpha >= 1)
            return ab.a;
        else
            return ab.b;
    }

    private void findBisector(Section ab, Section bc)
    {
        double r1 = getSqrDistance(ab.b, ab.middle);
        double r2 = getSqrDistance(bc.a, bc.middle);
        double r = Math.min(r1, r2);

        Point sBA = getSection(r, ab.a, ab.b);
        Point sBC = getSection(r, bc.b, bc.a);

        Section intersect = new Section(sBA, sBC);
        intersect.setMiddle();
        intersect.setNormal();

        bisectors.add(new Section(ab.b, intersect.middle));
    }

    private void findBisectors()
    {
        int size = sections.size();
        for(int i = 0; i < size; i++)
        {
            Section ab = sections.get(i);
            for (int j = i+1; j < size; j++) {
                Section bc = sections.get(j);
                if (!isParallelLines(ab, bc))
                {
                    Point b = getPointIntersect(ab, bc);
                    Point a = getLastPoint(ab, b);
                    Point c = getLastPoint(bc, b);

                    findBisector(new Section(a, b), new Section(b, c));
                }
            }
        }
    }

    private Point getPointIntersect(Section ab, Section cd)
    {
        Point a = ab.a;
        Point b = ab.b;
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

    private double getDistToLine(Point point, Section bc)
    {
        Point a = bc.a;
        Point b = bc.b;
        double A = b.y - a.y;
        double B = -b.x + a.x;
        double C = a.y*(b.x - a.x) - a.x*(b.y - a.y);
        return Math.abs(A*point.x + B*point.y + C) / Math.sqrt(A*A + B*B);
    }

    private double getDistToPoly(Point point)
    {
        double dist = getDistToLine(point, sections.firstElement());
        for (Section  section: sections)
        {
            dist = Math.min(dist, getDistToLine(point, section));
        }
        return dist;
    }


    private Circle getMaxCircle(Graphics g)
    {
        Circle c = new Circle(new Point(0, 0), 0);
        for (int i = 0; i < bisectors.size(); i++)
        {
            Section bisectorA = bisectors.get(i);
            for(int j = i+1; j < bisectors.size(); j++) {
                Section bisectorB = bisectors.get(j);

                Point inter = getPointIntersect(bisectorA, bisectorB);

                if (inPolygon(inter)) {
                    double r = getDistToPoly(inter);
                    if (r > c.r) {
                        c.r = r;
                        c.center = inter;
                    }
                }
            }
        }
        return c;
    }

    private Pair<Double, Double> getX(Section ab, Circle c)
    {
        Point center = c.center;
        double r = c.r;
        Point a = ab.a;
        Point b = ab.b;
        double ySubSqr = (b.y - a.y)*(b.y - a.y);
        double xSubSqr = (b.x - a.x)*(b.x - a.x);

        double A = xSubSqr + ySubSqr;
        double B = -center.x*xSubSqr - a.x*ySubSqr + a.y*(b.x - a.x)*(b.y - a.y) - center.y*(b.x - a.x)*(b.y - a.y);
        double C = center.x*center.x*xSubSqr + a.x*a.x*ySubSqr + a.y*a.y*xSubSqr + center.y*center.y*xSubSqr
                - 2*a.x*center.y*(b.y - a.y)*(b.x - a.x) + 2*a.x*center.y*(b.y - a.y)*(b.x - a.x)
                - 2*a.y*center.y*xSubSqr - r*r*xSubSqr;

        double discr = B*B - A*C;
        if (discr < 0)
            return null;
        discr  = Math.sqrt(discr);

        double x1 = (-B + discr) / A;
        double x2 = (-B - discr) / A;
        return new Pair<>(x1, x2);
    }


    private boolean inCircle(Point a, Circle c)
    {
        Point center = c.center;
        return Math.abs((a.x - center.x)*(a.x - center.x) + (a.y - center.y)*(a.y - center.y) - c.r*c.r) < 0.01;
    }

    private Point getPointTouch(Circle circle, Section ab)
    {
        double d = getDistToLine(circle.center, ab);
        if (Math.abs(d - circle.r) < 0.001)
        {
            Point middle = ab.middle;
            Point normal = ab.normal;
            double c1 = circle.center.x - normal.x;
            double c2 = circle.center.y - normal.y;

            Point newMid = new Point(middle.x + c1, middle.y + c2);

            Point touch = getPointIntersect(ab, new Section(newMid, circle.center));
            return touch;
        }
        return null;
    }

    private Pair<Point, Point> getISectCircleLine(Circle circle, Section ab)
    {
        Point a = ab.a;
        Point b = ab.b;
        double x1 = 0, x2 = 0, y1 = 0, y2 = 0;
        if (a.x != b.x)
        {
            Pair<Double, Double> X = getX(new Section(new Point(a.x, a.y), new Point(b.x, b.y)), circle);
            if (X == null)
                return null;

            x1 = X.getKey();
            y1 = a.y + x1*(b.y - a.y)/(b.x - a.x) - a.x*(b.y - a.y)/(b.x - a.x);

            x2 = X.getValue();
            y2 = a.y + x2*(b.y - a.y)/(b.x - a.x) - a.x*(b.y - a.y)/(b.x - a.x);
        } else {
            Pair<Double, Double> Y = getX(new Section(new Point(a.y, a.x), new Point(b.y, b.x)),
                    new Circle(new Point(circle.center.y, circle.center.x), circle.r));
            if (Y == null)
                return null;

            y1 = Y.getKey();
            x1 = a.x + y1*(b.x - a.x)/(b.y - a.y) - a.y*(b.x - a.x)/(b.y - a.y);

            y2 = Y.getValue();
            x2 = a.x + y2*(b.x - a.x)/(b.y - a.y) - a.y*(b.x - a.x)/(b.y - a.y);
        }
        if (!inCircle(new Point(x1, y1), circle) || !inCircle(new Point(x2, y2), circle))
            return null;
        return new Pair<>(new Point(x1, y1), new Point(x2, y2));

    }

    private double VP(Point a, Point b)
    {
        return a.x*b.y - a.y*b.x;
    }

    private boolean inPolygon(Point a)
    {
        Point fP = sections.firstElement().a;
        Point sP = sections.firstElement().b;
        double z = VP(new Point(a.x - fP.x, a.y - fP.y),
                new Point(fP.x - sP.x, fP.y - sP.y));
        for (int i = 1; i < sections.size(); i++)
        {
            fP = sections.get(i).a;
            sP = sections.get(i).b;
            double z1 = VP(new Point(a.x - fP.x, a.y - fP.y),
                    new Point(fP.x - sP.x, fP.y - sP.y));
            if (z*z1 < 0)
                return false;

        }
        return true;
    }

    private Pair<Boolean, Boolean> searchPolyPart(boolean startSearch, boolean find,
                                                  Point a, Vector<Point> newPoly, Circle circle)
    {
        for (Section section: sections)
        {
            if (startSearch || section.a.equals(a))
            {
                startSearch = true;
                newPoly.add(section.a);
                Point point = getPointTouch(circle, section);
                if (point != null)
                {
                    if(!point.equals(a))
                    {
                        newPoly.add(point);
                        find = true;
                        break;
                    }
                }
            }
        }
        return new Pair<>(startSearch, find);
    }

    private Vector<Point> searchNextPoly(Point a, Circle circle, Point noThis)
    {
        Vector<Point> newPoly = new Vector<>();
        newPoly.add(noThis);
        Pair<Boolean, Boolean> flags = searchPolyPart(false, false, a, newPoly, circle);
        boolean startSearch = flags.getKey();
        boolean find = flags.getValue();

        if (!find)
        {
            searchPolyPart(startSearch, find, a, newPoly, circle);
        }
        return newPoly;
    }

    private void drawPoint(Graphics g, Point a)
    {
        Pair<Integer, Integer> p1 = transferCoords(a);
        int intR = 2;
        g.drawOval(p1.getKey() - intR, p1.getValue() - intR, intR*2, intR*2);
    }

    private boolean inVector(Vector<Point> points, Point point)
    {
        for(Point a : points){
            if (a.equals(point))
                return true;
        }
        return false;
    }

    private Pair<Point, Point> getISectLinePoly(Section ab)
    {
        Vector<Point> points = new Vector<>();
        for (Section section:sections)
        {
            if (VP(new Point(section.a.x - section.b.x, section.a.y - section.b.y),
                new Point(ab.a.x - ab.b.x, ab.a.y - ab.b.y)) == 0)
                continue;
            Point point = getPointIntersect(section, ab);
            if (inSectOrLine(point, ab) && inSectOrLine(point, section) && !inVector(points, point))
                points.add(point);
//            if (points.size() == 2)
//                break;
        }
        if (points.size() == 0)
            return null;
        return new Pair<>(points.firstElement(), points.lastElement());
    }

    private void drawLine(Section ab, Graphics g)
    {
        Pair<Integer, Integer> p1 = transferCoords(ab.a);
        Pair<Integer, Integer> p2 = transferCoords(ab.b);
        g.drawLine(p1.getKey(), p1.getValue(), p2.getKey(), p2.getValue());
    }

    private Vector<Point> getNewPoly(Section ab, Point c, Circle circle, Graphics g)
    {
        Point o = circle.center;
        Point a = ab.a;
        Point b = ab.b;

        Vector<Point> newPoly = searchNextPoly(b, circle, c);

        Point a1 = newPoly.firstElement();
        Point b1 = newPoly.lastElement();

        Section c1c2 = new Section(a1, b1);

        c1c2.setMiddle();

        Point k1 = c1c2.middle;
        Point k2;

        Section ok1;

        if (!k1.equals(o)) {
            ok1 = new Section(o, k1);

        } else {
            ok1 = new Section(k1, c1c2.normal);
        }
        Pair<Point, Point> points = getISectCircleLine(circle, ok1);

        if (points == null) {
            return null;
        }

        double d1 = getSqrDistance(b, points.getKey());
        double d2 = getSqrDistance(b, points.getValue());
        if (d1 < d2)
            k2 = points.getKey();
        else
            k2 = points.getValue();

        double c1 = k2.x - k1.x;
        double c2 = k2.y - k1.y;

        Point a2 = new Point(a1.x + c1, a1.y + c2);
        Point b2 = new Point(b1.x + c1, b1.y + c2);

        points = getISectLinePoly(new Section(a2, b2));

        newPoly.remove(0);
        newPoly.remove(newPoly.size() - 1);
        if (inSect(points.getKey(), ab)) {
            if (!newPoly.firstElement().equals(points.getKey()))
                newPoly.add(0, points.getKey());
            if (!newPoly.lastElement().equals(points.getValue()))
                newPoly.add(points.getValue());
        } else {
            if (!newPoly.firstElement().equals(points.getValue()))
                newPoly.add(0, points.getValue());
            if (!newPoly.lastElement().equals(points.getKey()))
                newPoly.add(points.getKey());
        }

        return newPoly;
    }

    private double getAlpha(Point a, Section bc)
    {
        double x1 = bc.a.x;
        double y1 = bc.a.y;
        double x2 = bc.b.x;
        double y2 = bc.b.y;
        double alpha =  x1 != x2 ? (a.x - x2) / (x1 - x2) : (a.y - y2) / (y1 - y2);
        return alpha;
    }

    private boolean inSect(Point a, Section bc){
        double alpha = getAlpha(a, bc);
        double y = alpha * bc.a.y + (1 - alpha) * bc.b.y;
        double x = alpha * bc.a.x + (1 - alpha) * bc.b.x;
        return alpha >= 0 && alpha <= 1 && Math.abs(x - a.x) < 0.001 && Math.abs(y - a.y) < 0.001;
    }

    private boolean inSectOrLine(Point a, Section bc)
    {
        double alpha = getAlpha(a, bc);
        return alpha >= 0 && alpha <= 1;
    }

    public Circle getCircle(Graphics g)
    {

        sections.clear();
        bisectors.clear();
        initSections();

        findBisectors();

        return getMaxCircle(g);
    }

    private Circle calculateNewCircle(Vector<Point> newPoly, Circle c2, Graphics g)
    {
        Vector<Point> tempPoly = polygon;

        polygon = newPoly;
        Circle c1 = getCircle(g);
        if (c1.r > c2.r)
            c2 = c1;

        g.setColor(Color.BLUE);
        drawPolygon(g);

        g.setColor(Color.black);

//        Pair<Integer, Integer> p2 = transferCoords(c1.center);
//        int intR1 = (int)(c1.r*coef);
//        g.drawOval(p2.getKey() - intR1, p2.getValue() - intR1, intR1*2, intR1*2);

        drawBisections(g);

        polygon = tempPoly;
        sections.clear();
        bisectors.clear();
        initSections();
        findBisectors();
        return c2;
    }

    private Circle oneCycle(Circle c, Graphics g)
    {
        Circle c2 = new Circle(new Point(0,0), 0);
        int i = 0;
        while(i < sections.size())
        {
            Section currSect = sections.get(i);

            Point iSect = getPointTouch(c, currSect);
            if (iSect == null)
            {
                i++;
                continue;
            }

            Vector<Point> newPoly = getNewPoly(currSect, iSect, c, g);
            if (newPoly == null)
            {
                i++;
                continue;
            }
            i += newPoly.size() - 2;

            c2 = calculateNewCircle(newPoly, c2, g);
        }
        return c2;
    }


    private void invertPoints()
    {
        Vector<Point> points = new Vector<>();
        for (Point point : polygon)
            points.add(0, point);
        polygon = points;
    }

    private void calculate(Graphics g)
    {
        polygon.clear();
        initPolygon();
        Circle c = getCircle(g);

        Pair<Integer, Integer> p1 = transferCoords(c.center);
        int intR = (int)(c.r*coef);
        g.setColor(Color.BLUE);
        g.drawOval(p1.getKey() - intR, p1.getValue() - intR, intR*2, intR*2);
        g.setColor(Color.black);

        Circle c2 = oneCycle(c, g);

        invertPoints();
        sections.clear();
        bisectors.clear();
        initSections();
        findBisectors();

        Circle c3 = oneCycle(c, g);

        if (c3.r > c2.r)
            c2 = c3;

        Pair<Integer, Integer> p2 = transferCoords(c2.center);
        int intR1 = (int)(c2.r*coef);
        g.drawOval(p2.getKey() - intR1, p2.getValue() - intR1, intR1*2, intR1*2);
    }

    private Pair<Integer, Integer> transferCoords(Point a)
    {
        int xx = (int)(a.x * coef) + xShift;
        int yy = (int)(a.y * coef);
        if (yy >=0)
            yy = yShift - yy;
        else
            yy = yShift + (-1) * yy;
        return new Pair<>(xx, yy);
    }
    
    private void drawPolygon(Graphics g)
    {
        int[] xxCoords = new int[polygon.size()];
        int[] yyCoords = new int[polygon.size()];
        int i = 0;
        for (Point a : polygon) {
            Pair<Integer, Integer> intPoint = transferCoords(a);
            xxCoords[i] = intPoint.getKey();
            yyCoords[i] = intPoint.getValue();
            i++;
        }
        g.drawPolygon(xxCoords, yyCoords, polygon.size());
    }

    private void drawBisections(Graphics g)
    {
        for (Section section : bisectors)
        {
            drawLine(section, g);
        }
    }

    private void drawNormals(Graphics g)
    {
        for (Section section : sections)
        {
            drawLine(new Section(section.middle, section.normal), g);
        }
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        
        calculate(g);

        g.drawLine(0, 300, 800, 300);
        g.drawLine(400, 0, 400, 600);
        g.setColor(Color.red);
        drawPolygon(g);
        g.setColor(Color.black);

//        g.setColor(Color.BLUE);
//        drawNormals(g);
//
//        g.setColor(Color.RED);
//        drawBisections(g);

        super.repaint();
    }

    public static void main(String[] args) {
        Main canv = new Main();
        canv.setPreferredSize(new Dimension(width, height));

        JFrame w=new JFrame("Function");
        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        w.getContentPane().add(canv);
        w.pack();

        w.setLocationRelativeTo(null);
        w.setVisible(true);
    }
}
