package com.company;

import java.util.Vector;

/**
 * Created by Helen on 01.04.2016.
 */
public class Polygon {
    Vector<Point> polygon;
    Vector<Section> sections;
    Vector<Section> bisectors;

    public Polygon()
    {
        polygon = new Vector<>();
        sections = new Vector<>();
        bisectors = new Vector<>();
        initPolygon();
        initSections();
        findBisectors();
    }

    public void initPolygon()
    {
        if (polygon != null)
            polygon.clear();
//        polygon.add(new Point(0, 1));
//        polygon.add(new Point(1, 0));
//        polygon.add(new Point(1, 2));
//        polygon.add(new Point(0, 2));
//
//        polygon.add(new Point(0, 0));
//        polygon.add(new Point(1, 0));
//        polygon.add(new Point(1, 2));
//        polygon.add(new Point(0, 1));

//        polygon.add(new Point(0,200));
//        polygon.add(new Point(0,-100));
//        polygon.add(new Point(200,-100));
//        polygon.add(new Point(100,200));
//        polygon.add(new Point(50,300));
//
        polygon.add(new Point(0,0));
        polygon.add(new Point(-200,0));
        polygon.add(new Point(-100,300));
        polygon.add(new Point(0,300));
//
//        polygon.add(new Point(0, 100));
//        polygon.add(new Point(400, 0));
//        polygon.add(new Point(400, 300));
//        polygon.add(new Point(0, 300));
//        polygon.add(new Point(0, 200));

//        polygon.add(new Point(0, 0));
//        polygon.add(new Point(500, 0));
//        polygon.add(new Point(500, 100));
//        polygon.add(new Point(0, 100));

//        polygon.add(new Point(0, 250));
//        polygon.add(new Point(0, 0));
//        polygon.add(new Point(250, -30));
//        polygon.add(new Point(300, 0));
//        polygon.add(new Point(300, 250));

//        polygon.add(new Point(0, 0));
//        polygon.add(new Point(0, 100));
//        polygon.add(new Point(100, 100));
//        polygon.add(new Point(100, 0));
    }


    public void setPoly(Vector<Point> newPoly)
    {
        polygon = newPoly;
        initSections();
        findBisectors();
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

        sections.clear();
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

    private Point getSection(double r, Point a, Point b)
    {
        double d = a.getSqrDistance(b);
        double x = a.x*Math.sqrt(r/d) + (1 - Math.sqrt(r/d))*b.x;
        double y = a.y*Math.sqrt(r/d) + (1 - Math.sqrt(r/d))*b.y;
        return new Point(x, y);
    }


    private boolean isParallelLines(Section ab, Section cd)
    {
        Point a = new Point(ab.a.x - ab.b.x, ab.a.y - ab.b.y);
        Point b = new Point(cd.a.x - cd.b.x, cd.a.y - cd.b.y);
        return a.VP(b) == 0;
    }

    private Point getLastPoint(Section ab, Point c)
    {
        if (ab.a.equals(c))
            return ab.b;
        if(ab.b.equals(c))
            return ab.a;
        double alpha = new Section(ab.b, c).getAlpha(ab.a);
        if (alpha <= 0 || alpha >= 1)
            return ab.a;
        else
            return ab.b;
    }

    public void findBisector(Section ab, Section bc)
    {
        double r1 = ab.b.getSqrDistance(ab.middle);
        double r2 = bc.a.getSqrDistance(bc.middle);
        double r = Math.min(r1, r2);

        Point sBA = getSection(r, ab.a, ab.b);
        Point sBC = getSection(r, bc.b, bc.a);

        Section intersect = new Section(sBA, sBC);
        intersect.setMiddle();
        intersect.setNormal();

        bisectors.add(new Section(ab.b, intersect.middle));
    }

    public void findBisectors()
    {
        bisectors.clear();
        int size = sections.size();
        for(int i = 0; i < size; i++)
        {
            Section ab = sections.get(i);
            for (int j = i+1; j < size; j++) {
                Section bc = sections.get(j);
                if (!isParallelLines(ab, bc))
                {
                    Point b = ab.getPointIntersect(bc);
                    Point a = getLastPoint(ab, b);
                    Point c = getLastPoint(bc, b);

                    findBisector(new Section(a, b), new Section(b, c));
                }
            }
        }
    }

    public double getDistToPoly(Point point)
    {
        double dist = point.getDistToLine(sections.firstElement());
        for (Section  section: sections)
        {
            dist = Math.min(dist, point.getDistToLine(section));
        }
        return dist;
    }

    public boolean inPolygon(Point a)
    {
        Point fP = sections.firstElement().a;
        Point sP = sections.firstElement().b;
        double z = new Point(a.x - fP.x, a.y - fP.y).VP(new Point(fP.x - sP.x, fP.y - sP.y));
        for (int i = 1; i < sections.size(); i++)
        {
            fP = sections.get(i).a;
            sP = sections.get(i).b;
            double z1 = new Point(a.x - fP.x, a.y - fP.y).VP(new Point(fP.x - sP.x, fP.y - sP.y));
            if (z*z1 < 0)
                return false;

        }
        return true;
    }
}
