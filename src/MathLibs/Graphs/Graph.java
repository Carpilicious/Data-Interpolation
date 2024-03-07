package MathLibs.Graphs;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;

public class Graph extends JPanel implements ChangeListener {

    private double min_x;
    private double max_x;
    private double min_y;
    private double max_y;

    private final int width = 500;
    private final int height = 500;

    private JSlider sliderScale;

    private int origin_y;
    private int origin_x;

    private double px_IncrementH;
    private double px_IncrementV;

    private double[] polynomial;
    private int previousVal;

    private final int sampleRate = 1; //for every n pixels, a point is generated on the graph

    public Graph(double[] polynomial){

        this.polynomial = polynomial;
        this.setSize(new Dimension(500,500));
        this.setVisible(true);
        setBackgroundDecoration();
    }

    private void setBackgroundDecoration(){
        this.setBackground(new Color(31,31,46));
        sliderScale = new JSlider(0,5,0);
        sliderScale.setBackground(Color.WHITE);
        sliderScale.addChangeListener(this);

        JLabel sliderText = new JLabel("Scale Slider ➡");
        sliderText.setForeground(Color.WHITE);
    }

    private void calculateOrigin(){
        origin_x = (int) (-min_x/px_IncrementH);
        origin_y = (int) (height - (-min_y/px_IncrementV));
    }

    public void paintComponent(Graphics g) {

        calculateOrigin();
        super.paintComponent(g);
        // draw axis //

        System.out.println(origin_x + " is the origin of the x-axis");
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        g2d.drawLine(0, origin_y, width, origin_y);
        g2d.drawLine(origin_x, 0, origin_x, height);

        // draw labels //
        int offset = (int) Math.ceil(0.03 * width);
        //x-axis labels //
        g2d.drawString(Double.toString(min_x), 0, origin_y + offset);
        g2d.drawString("0", origin_x - offset, origin_y + offset);
        g2d.drawString(Double.toString(max_x), width - offset, origin_y + offset);

        //y-axis labels
        g2d.drawString(Double.toString(max_y), origin_x, offset);
        g2d.drawString(Double.toString(min_y), origin_x, height - offset);

        //drawing the polynomial
        if (polynomial != null) {
            g2d.setColor(Color.YELLOW);
            Point previousPoint = null;

            for(int i=0; i<=width;i+=sampleRate){
                double currentYVal = 0;
                double currentXVal = min_x + i*px_IncrementH;

                for(int v=0; v<polynomial.length; v++){currentYVal+=polynomial[v]*Math.pow(currentXVal,v);} // coefficient multiplied by x-values

                int relativeYPos = (int) (origin_y - (currentYVal/px_IncrementV));  // relative to the axis of the graph
                int relativeXPos = i;


                if(previousPoint != null) {
                    //draw a line connecting the two points
                    g2d.drawLine((int)previousPoint.getX(), (int)previousPoint.getY(), relativeXPos, relativeYPos);
                }
                previousPoint = new Point(relativeXPos,relativeYPos);
            }
        }
        else {
            System.out.println("Unable to draw polynomial");
        }
    }

    public void drawPolynomial(){
        this.repaint();
    }

    public void addCustomText(String text, Point pos) {
        JLabel newText = new JLabel(text);
        newText.setForeground(Color.WHITE);
        newText.setVisible(true);
        this.add(newText);
        newText.setLocation(pos);
    };

    public void setXBoundary(double[] xVals){
        double[] minMax = maxMinValues(xVals);
        min_x = minMax[0];
        max_x = minMax[1];
        px_IncrementH = (double) Math.abs(max_x - min_x) /width;
    }

    public void setYBoundary(){
        double[] yVals = new double[width + 1];
        double[] minMax;
        for(int i=0; i<=width; i+=sampleRate){for(int v=0; v<polynomial.length; v++){yVals[i]+=polynomial[v]*Math.pow(min_x + i*px_IncrementH,v);}}
        minMax = maxMinValues(yVals);

        min_y = minMax[0];
        max_y = minMax[1];

        px_IncrementV = (double) Math.abs(max_y - min_y) / height;
    }


    /*maximum and minimum values must be obtained from the polynomial to scale the graph correctly
     * this ensures every part of the polynomial is visible within the screen frame */
    private double[] maxMinValues(double[] arr){
       double minimum=arr[0], maximum=arr[0];
        for (double v : arr) {
            if (v > maximum) {
                maximum = v;
            } else if (v < minimum) {
                minimum = v;
            }
        }
        return new double[]{minimum,maximum};
    }


    /*maximum and minimum values must be obtained from the polynomial to scale the graph correctly
    * this ensures every part of the polynomial is visible within the screen frame */
    private void maxMinVals(){

        for(int i =0; i<=width; i+= sampleRate){
            double yVal = 0;
            for(int v=0; v<polynomial.length; v++){yVal+=polynomial[v]*Math.pow(min_x + i*px_IncrementH,v);}

            if (i == 0){
                min_y = yVal; max_y = yVal;
            }
            else {
                if(yVal > max_y){
                    max_y = yVal;
                }
                else if(yVal < min_y){
                    min_y = yVal;
                }
            }

        }

    }


    @Override
    public void stateChanged(ChangeEvent e) {
        if(e.getSource() == sliderScale){
            int currentNum = sliderScale.getValue();
            if(currentNum>previousVal){
                this.min_y *=1.5;
                this.max_y *=1.5;
            }
            else {
                this.min_y /= 1.5;
                this.max_y /=1.5;
            }

            calculateOrigin();
            this.repaint();
            previousVal = currentNum;
        }

    }
}
