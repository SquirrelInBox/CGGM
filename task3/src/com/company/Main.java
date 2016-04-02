package com.company;

import javafx.util.Pair;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.BitSet;
import java.util.Vector;


public class Main extends JPanel {
    static int width = 800;
    static int height = 600;
    int coef = 1;
    int xShift = 400;
    int yShift = 300;


    Polygon polygon = new Polygon();
    Vector<Section> bisectors = polygon.bisectors;

    private Circle getMaxCircle(Graphics g)
    {
        Circle c = new Circle(new Point(0, 0), 0);
        for (int i = 0; i < bisectors.size(); i++)
        {
            Section bisectorA = bisectors.get(i);
            for(int j = i+1; j < bisectors.size(); j++) {
                Section bisectorB = bisectors.get(j);

                Point inter = bisectorA.getPointIntersect(bisectorB);

                if (polygon.inPolygon(inter)) {
                    double r = polygon.getDistToPoly(inter);
                    if (r > c.r) {
                        c.r = r;
                        c.center = inter;
                    }
                }
            }
        }
        return c;
    }


    private Point getPointTouch(Circle circle, Section ab)
    {
        double d = circle.center.getDistToLine(ab);
        if (Math.abs(d - circle.r) < 0.001)
        {
            Point middle = ab.middle;
            Point normal = ab.normal;
            double c1 = circle.center.x - normal.x;
            double c2 = circle.center.y - normal.y;

            Point newMid = new Point(middle.x + c1, middle.y + c2);

            Point touch = ab.getPointIntersect(new Section(newMid, circle.center));
            return touch;
        }
        return null;
    }


    private Pair<Boolean, Boolean> searchPolyPart(boolean startSearch, boolean find,
                                                  Point a, Vector<Point> newPoly, Circle circle)
    {
        for (Section section: polygon.sections)
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

    @Nullable
    private Pair<Point, Point> getISectLinePoly(Section ab)
    {
        Vector<Point> points = new Vector<>();
        for (Section section:polygon.sections)
        {
            Point tempPoint = new Point(section.a.x - section.b.x, section.a.y - section.b.y);
            if (tempPoint.VP(new Point(ab.a.x - ab.b.x, ab.a.y - ab.b.y)) == 0)
                continue;
            Point point = section.getPointIntersect(ab);
            if (inSect(point, section) && !inVector(points, point))
                points.add(point);
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

    private Polygon getOnePoly(Vector<Point> newPoly, Section bk, Point k, Section ab)
    {
        bk.setMiddle(k.x, k.y);
        bk.setNormal();

        Pair<Point, Point> points = getISectLinePoly(new Section(k, bk.normal));

        Point a2 = points.getKey();
        Point b2 = points.getValue();

        newPoly.remove(0);
        newPoly.remove(newPoly.size() - 1);
        if (inSect(a2, ab)) {
            if (!newPoly.firstElement().equals(a2))
                newPoly.add(0, a2);
            if (!newPoly.lastElement().equals(b2))
                newPoly.add(b2);
        } else {
            if (!newPoly.firstElement().equals(b2))
                newPoly.add(0, b2);
            if (!newPoly.lastElement().equals(a2))
                newPoly.add(a2);
        }

        Polygon poly = new Polygon();
        poly.setPoly(newPoly);

        return poly;
    }


    private Point getNearestPoint(Circle circle, Section ab, Point b)
    {
        Pair<Point, Point> tempPoints = circle.getISectCircleLine(ab);

        double d1 = b.getSqrDistance(tempPoints.getKey());
        double d2 = b.getSqrDistance(tempPoints.getValue());
        if (d1 < d2)
            return tempPoints.getKey();
        else
            return tempPoints.getValue();

    }

    private Vector<Polygon> getNewPoly(Section ab, Point c, Circle circle, Graphics g)
    {
        Vector<Polygon> polygons = new Vector<>();
        Point o = circle.center;
        Point k;
        Point b = ab.b;

        Section bk;

        Vector<Point> newPoly = searchNextPoly(b, circle, c);

        for (int i = 0; i < newPoly.size(); i++)
        {
            b = newPoly.get(i);
            for (int m = 1; m < newPoly.size(); m++) {
                polygon.findBisector(new Section(newPoly.get(i), o),
                        new Section(o, newPoly.get(m)));
                bk = bisectors.lastElement();
                bisectors.remove(bisectors.size() - 1);
                k = getNearestPoint(circle, bk, b);

                Vector<Point> tempP = getOnePoly(newPoly, bk, k, ab).polygon;

                Vector<Point> polyReturn = new Vector<>();

                Point lastPoint = tempP.lastElement();
                for (int j = 1; j < tempP.size() - 1; j++) {
                    polyReturn.add(tempP.get(j));
                    if (inSect(lastPoint, new Section(tempP.get(j), tempP.get(j + 1))))
                        break;
                }
                polyReturn.add(0, tempP.firstElement());
                polyReturn.add(lastPoint);

                Polygon res = new Polygon();
                res.setPoly(polyReturn);
                polygons.add(res);
            }
        }

        if (newPoly.size() > 3) {
            polygon.findBisector(new Section(newPoly.get(1), o), new Section(o, newPoly.get(newPoly.size() - 1)));
            bk = bisectors.lastElement();
            bisectors.remove(bisectors.size() - 1);

            k = getNearestPoint(circle, bk, b);

            Polygon result = getOnePoly(newPoly, bk, k, ab);
            polygons.add(result);
        }

        return polygons;
    }


    private boolean inSect(Point a, Section bc){
        double alpha = bc.getAlpha(a);
        double y = alpha * bc.a.y + (1 - alpha) * bc.b.y;
        double x = alpha * bc.a.x + (1 - alpha) * bc.b.x;
        return alpha >= 0 && alpha <= 1 && Math.abs(x - a.x) < 0.001 && Math.abs(y - a.y) < 0.001;
    }

    public Circle getCircle(Graphics g)
    {
        polygon.initSections();
        polygon.findBisectors();

        return getMaxCircle(g);
    }

    private Circle calculateNewCircle(Vector<Point> newPoly, Circle c2, Graphics g)
    {
        Vector<Point> tempPoly = polygon.polygon;

        polygon.polygon = newPoly;
        Circle c1 = getCircle(g);
        if (c1.r > c2.r)
            c2 = c1;

        g.setColor(Color.BLUE);
//        drawPolygon(g);
        g.setColor(Color.black);

//        Pair<Integer, Integer> p2 = transferCoords(c1.center);
//        int intR1 = (int)(c1.r*coef);
//        g.drawOval(p2.getKey() - intR1, p2.getValue() - intR1, intR1*2, intR1*2);

//        drawBisections(g);

        polygon.polygon = tempPoly;
        polygon.initSections();
        polygon.findBisectors();

        return c2;
    }

    private Circle oneCycle(Circle c, Graphics g)
    {
        Circle c2 = new Circle(new Point(0,0), 0);
        int i = 0;
        while(i < polygon.sections.size())
        {
            Section currSect = polygon.sections.get(i);

            Point iSect = getPointTouch(c, currSect);
            if (iSect == null)
            {
                i++;
                continue;
            }

            Vector<Polygon> newPoly = getNewPoly(currSect, iSect, c, g);
            if (newPoly == null)
            {
                i++;
                continue;
            }
            i += newPoly.firstElement().polygon.size() - 2;

            for (Polygon poly : newPoly)
                c2 = calculateNewCircle(poly.polygon, c2, g);
        }
        return c2;
    }


    private void invertPoints()
    {
        Vector<Point> points = new Vector<>();
        for (Point point : polygon.polygon)
            points.add(0, point);
        polygon.polygon = points;
    }

    private void calculate(Graphics g)
    {
        polygon.initPolygon();
        Circle c = getCircle(g);

        Pair<Integer, Integer> p1 = transferCoords(c.center);
        int intR = (int)(c.r*coef);
        g.setColor(Color.BLUE);
        g.drawOval(p1.getKey() - intR, p1.getValue() - intR, intR*2, intR*2);
        g.setColor(Color.black);

        Circle c2 = oneCycle(c, g);

        invertPoints();
        polygon.initSections();


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
        int[] xxCoords = new int[polygon.polygon.size()];
        int[] yyCoords = new int[polygon.polygon.size()];
        int i = 0;
        for (Point a : polygon.polygon) {
            Pair<Integer, Integer> intPoint = transferCoords(a);
            xxCoords[i] = intPoint.getKey();
            yyCoords[i] = intPoint.getValue();
            i++;
        }
        g.drawPolygon(xxCoords, yyCoords, polygon.polygon.size());
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
        for (Section section : polygon.sections)
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

//        polygon.clear();
//        initPolygon();
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
