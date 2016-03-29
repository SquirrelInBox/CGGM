package com.company;

import javax.swing.*;
import java.awt.*;

public class Main extends JPanel{
    static int width = 800;
    static int height = 600;

    static double minx = 100000;
    static double maxx = -minx;
    static double maxy = maxx;
    static double miny = minx;

    static double x1 = -3;
    static double x2 = 3;
    static double y1 = -3;
    static double y2 = 3;

    double f(double x, double y)
    {
        return Math.cos(x*y);
    }

    double coord_x(double x, double y, double z)
    {
        return (y - x)*Math.sqrt(3)/2;
    }

    double coord_y(double x, double y, double z)
    {
        return (x + y)/2 - z;
    }

    void calculate(Graphics g)
    {
        double x, y, z, xx, yy;
        int n = 100, m = width*2;
        int top[]= new int[m];
        int bottom[] = new int[m];

        for(int i = 0; i < n; i++)
        {
            x = x2 + i*(x1 - x2)/n;
            for(int j = 0; j < m; j++)
            {
                y = y2 + j*(y1 - y2)/m;
                z = f(x, y);

                xx = coord_x(x, y, z);
                yy = coord_y(x, y, z);

                if (xx > maxx) maxx = xx;
                if (xx < minx) minx = xx;
                if (yy > maxy) maxy = yy;
                if (yy < miny) miny = yy;
            }
        }

        for(int i = 0; i < m; i++)
        {
            top[i] = height;
            bottom[i] = 0;
        }

        for(int i = 0; i < n; i++)
        {
            x = x2 + i*(x1 - x2)/n;
            for(int j = 0; j < m; j++) {
                y = y2 + j * (y1 - y2) / m;
                z = f(x, y);

                xx = coord_x(x, y, z);
                yy = coord_y(x, y, z);

                xx = (xx - minx) / (maxx - minx) * width;
                yy = (yy - miny) / (maxy - miny) * height;

                if (yy > bottom[(int) xx]) {
                    g.setColor(Color.BLUE);
                    g.drawLine((int) xx, (int) yy,
                            (int) xx, (int) yy);
                    bottom[(int)xx] = (int)yy;
                }
                if (yy < top[(int)xx]){
                    g.setColor(Color.RED);
                    g.drawLine((int) xx, (int) yy,
                            (int) xx, (int) yy);
                    top[(int)xx] = (int)yy;
                }
            }
        }
    }


    @Override
    public void paint(Graphics g)
    {
        super.paint(g);

        calculate(g);

        super.repaint();
    }

    public static void main(String[] args) {
        Main canv = new Main();
        canv.setPreferredSize(new Dimension(width+1, height+1));

        JFrame w=new JFrame("Function");
        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        w.getContentPane().add(canv);
        w.pack();

        w.setLocationRelativeTo(null);
        w.setVisible(true);
    }
}
