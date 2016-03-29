package com.company;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;
import java.util.regex.Matcher;

public class Main extends JPanel {
    static int width = 800;
    static int height = 600;

    int coef = 1;
    int xShift = 400;
    int yShift = 300;

    //    x = A*Sin t + B
    //    y = C* Cos t + D
    double A = 100;
    double B = 100;
    double C = -1;
    double D = 100;
    boolean shouldTurn = true;

    class Point
    {
        double delta;
        public void setDelta(double delta)
        {
            this.delta = delta;
        }
    }

    class IntPoint extends Point
    {
        int x;
        int y;
        public IntPoint(int x, int y)
        {
            this.x = x;
            this.y = y;
        }

        public IntPoint(int x, int y, double delta)
        {
            this.x = x;
            this.y = y;
            this.delta = delta;
        }
    }

    class RealPoint extends Point
    {
        double x;
        double y;
        public RealPoint(double x, double y)
        {
            this.x = x;
            this.y = y;
        }
    }

    class PointPairs
    {
        public IntPoint intPoint;
        public RealPoint realPoint;
        PointPairs(IntPoint intPoint) { this.intPoint = intPoint; }
        PointPairs(RealPoint realPoint) { this.realPoint = realPoint; }

        void calculateIntPoint()
        {
            int xx = (int)(realPoint.x);
            int yy = (int)(realPoint.y)*(-1);
            intPoint = new IntPoint(xx, yy);
        }

        void calculateRealPoint()
        {
            double x = (double)(intPoint.x);
            double y = (double)(intPoint.y*(-1));
//            double y = (double)(intPoint.y)/coef;
            realPoint = new RealPoint(x, y);
        }
    }

    Vector<IntPoint> intPoints = new Vector<>();
    Vector<IntPoint> correctPoints = new Vector<>();

    class Ellipse
    {
        double sum;
        double f1;
        double f2;

        public Ellipse(){
            if(Math.abs(C) < Math.abs(A))
            {
                double c1 = C;
                C = A;
                A = c1;
                c1 = B;
                B = D;
                D = c1;
                shouldTurn = false;
            }
            sum = Math.sqrt(C*C - A*A);
            f1 = -sum;
            f2 = sum;
        }

        double getDelta(RealPoint a)
        {
            double f1M = Math.pow(a.x - f1, 2) + Math.pow(a.y, 2);
            double f2M = Math.pow(a.x - f2, 2) + Math.pow(a.y, 2);
            double doubleSum = 2*Math.sqrt(f1M*f2M);
            double reference = 2*Math.abs(C);
            double result = Math.sqrt(f1M) + Math.sqrt(f2M) - reference;
            return result;
        }

        private void addPoint(IntPoint a)
        {
            intPoints.add(a);
            intPoints.add(new IntPoint(a.x, - a.y));
            intPoints.add(new IntPoint(-a.x, a.y));
            intPoints.add(new IntPoint(-a.x, -a.y));
        }

        PointPairs setC(PointPairs a, double deltaC)
        {
            IntPoint cI = new IntPoint(a.intPoint.x + 1, a.intPoint.y + 1, deltaC);
//            IntPoint cI = new IntPoint(a.intPoint.x - 1, a.intPoint.y + 1, deltaC);
            PointPairs c = new PointPairs(cI);
            c.calculateRealPoint();
            return c;
        }

        PointPairs bVSc(double deltaC, PointPairs a)
        {
            IntPoint bI = new IntPoint(a.intPoint.x + 1, a.intPoint.y);
//            IntPoint bI = new IntPoint(a.intPoint.x - 1, a.intPoint.y);
            PointPairs b = new PointPairs(bI);
            b.calculateRealPoint();
            double deltaB = getDelta(b.realPoint);
            if (Math.abs(deltaB) <= Math.abs(deltaC))
            {
                b.realPoint.setDelta(deltaB);
                a = b;
            } else {
                a = setC(a, deltaC);
            }
            addPoint(a.intPoint);
            return a;
        }

        PointPairs dVSc(double deltaC, PointPairs a)
        {
            IntPoint dI = new IntPoint(a.intPoint.x, a.intPoint.y + 1);
//            IntPoint dI = new IntPoint(a.intPoint.x, a.intPoint.y + 1);
            PointPairs d = new PointPairs(dI);
            d.calculateRealPoint();
            double deltaD = getDelta(d.realPoint);
            if (Math.abs(deltaC) >= Math.abs(deltaD))
            {
                d.realPoint.setDelta(deltaD);
                a = d;
            } else {
                a = setC(a, deltaC);
            }
            addPoint(a.intPoint);
            return a;
        }

        void addAllPoints()
        {
            double x = 0;
            double y = Math.abs(A);
//            double y = Math.abs(C);

            RealPoint dA = new RealPoint(x, y);
            double currDelta = getDelta(dA);
            dA.setDelta(currDelta);
            PointPairs a = new PointPairs(dA);
            a.calculateIntPoint();
            addPoint(a.intPoint);


            while (y >= 0)
            {
                IntPoint dI = new IntPoint(a.intPoint.x + 1, a.intPoint.y + 1);
                if (y == 0)
                    dI = new IntPoint(a.intPoint.x, a.intPoint.y);
                PointPairs d = new PointPairs(dI);
                d.calculateRealPoint();
                double deltaC = getDelta(d.realPoint);

                if (deltaC > 0)
                {
                    a = dVSc(deltaC, a);
                } else {
                    a = bVSc(deltaC, a);
                }
                dA = a.realPoint;
                y = dA.y;
                x = dA.x;
            }
            intPoints.remove(intPoints.size() - 1);
            intPoints.remove(intPoints.size() - 1);
            intPoints.remove(intPoints.size() - 1);
            intPoints.remove(intPoints.size() - 1);
        }
    }

    public void calculate()
    {
        Ellipse ellipse = new Ellipse();
        ellipse.addAllPoints();
    }

    public void drawPoints(Graphics g)
    {
        g.drawLine(0, 300, 800, 300);
        g.drawLine(400, 0, 400, 600);
        g.setColor(Color.red);
        for (IntPoint point:correctPoints) {

            g.drawLine(point.x, point.y, point.x, point.y);
        }
    }

    private void turnEllipse()
    {
        for(IntPoint point : intPoints)
        {
            correctPoints.add(new IntPoint(point.y, point.x));
        }
    }

    private void shiftPoints()
    {
        for(IntPoint point : correctPoints)
        {
            int x = point.x;
            int y = point.y;
//            point.x = x + xShift + (int)B*coef;
            point.x = x + xShift + (int)B;
            int yy = y + (int)D;
//            int yy = y + (int)D*coef;
            if (yy >= 0)
                point.y = yShift - yy;
            else
                point.y = (-1)*yy + yShift;
        }
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        correctPoints.clear();
        calculate();
        if (shouldTurn)
            turnEllipse();
        else
            correctPoints = intPoints;
        shiftPoints();


        drawPoints(g);
//        g.drawLine(0, 300 - (int)B*2, 800, 300 -(int)B*2);
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
