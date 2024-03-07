package Lagrange_Package;
import FFT_Package.FFT;
import ArrayPackage.*;
import MathLibs.*;
import MathLibs.Graphs.Graph;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

/* enum for variations in colour. Ideal for UI elements that required a background and a border (colours complement each other)   */
enum customColors{
    NAVYBLUE(new Color(31, 31, 46), new Color(23, 23, 35)),      //2nd colour darker than 1st for contrast
    WHITE(new Color(241, 241, 241), new Color(161, 157, 157)),
    GREEN(new Color(18, 183, 9),new Color(11, 101, 4))
    ;
    final Color backColor;
    final Color borderColor;

    customColors(Color backcolor, Color borderColor) {
        this.backColor = backcolor;
        this.borderColor = borderColor;
    }
}

/*Lagrange Interpolation finds a polynomial which connects discrete data points. It can be used to estimate data within a range (interpolation)
* This program works for 2D data points, however it can be extended to work with 3D data.*/

public class Lagrange_Interpolation implements ActionListener {

    private ArrayList<Complex[]> coordinates;
    private double[] finalPolynomial;
    private FFT fastFourier;
    private JFrame frame;
    private JButton addButton;
    private JButton startButton;
    private JTextField addXCoord;
    private JTextField addYCoord;
    private JLabel statusLabel;

    private ArrayList<Double> xValsRecorded;

    public Lagrange_Interpolation(){
        this.coordinates =  new ArrayList<Complex[]>();
        this.fastFourier = new FFT();
        this.finalPolynomial = new double[]{};
        this.xValsRecorded = new ArrayList<Double>();
        setUpUI();
    }

    public void addCoordinate(Complex[] coordinates){
        this.coordinates.add(coordinates);
    }

    private void runInterpolation(){

        for(int i=0; i<coordinates.size();i++){
            Complex currX = coordinates.get(i)[0];
            Complex currY = coordinates.get(i)[1];

            Complex[] resultPolynomial;

            ArrayList<Complex> allXTerms = CarpArrays.pullIndex2D(Complex.class, coordinates,0);
            allXTerms.remove(i);  //excluding the current term

            Complex constTerm = calculateConstant(allXTerms,currX,currY);  //calculate the constant term in the expansion of lagrange interpolation for each part

            for(int v=0; v<coordinates.size(); v++){
                if(i!=v){
                    //add numerator terms of formula to FFT for multiplication afterwards
                    fastFourier.addPolynomial(new Complex[]{new Complex(-coordinates.get(v)[0].getRealPart(),0), new Complex(1,0)});
                }
            }

            Complex[] result = fastFourier.multiplyAllPolynomial();
            result = Arrays.stream(result).map((e)->e.multiply(constTerm)).toArray(Complex[]::new); //multiply each coefficient with the constant term
            double[] tempPoly = Arrays.stream(result).mapToDouble(Complex::getRealPart).toArray(); //converting complex array into double array

            addPolynomails(tempPoly);
            fastFourier.clearPolynomial();
        }

        StringBuilder finalPolyStr = new StringBuilder();
        for(int i=0; i<finalPolynomial.length; i++){
            finalPolyStr.append(finalPolynomial[i]).append("x^").append(i).append("+");
        }

        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setSize(700,700);
        frame.setBackground(customColors.NAVYBLUE.backColor);

        double[] xVals = coordinates.stream().mapToDouble((e)->e[0].getRealPart()).toArray();

        Graph graph = new Graph(finalPolynomial);
        graph.setXBoundary(xVals);
        graph.setYBoundary();
        graph.drawPolynomial();
        graph.addCustomText("Polynomial: "+finalPolyStr.toString(),new Point(450,10));

        frame.add(graph, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private Complex calculateConstant(ArrayList<Complex>lessXTerms, Complex currX, Complex currY){
        return new Complex(currY.getRealPart() * 1/(lessXTerms.stream().map(Complex::getRealPart).reduce(1.0,(accumulator, element)->accumulator*(currX.getRealPart()-element))),0);
    }

    /*Interface which user sees. Allows them to add data points for interpolation */
    private void setUpUI(){

        frame = new JFrame();
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        ////////////     UPPER FRAME    ////////////////////

        JPanel upperFrame = new JPanel();
        upperFrame.setBackground(customColors.NAVYBLUE.backColor);
        upperFrame.setPreferredSize(new Dimension(500, 80));

        JLabel title = new JLabel("Lagrange Interpolation", SwingConstants.HORIZONTAL);
        title.setFont(new Font("Arial",Font.BOLD, 30));
        title.setForeground(Color.WHITE);


        ////////////    MIDDLE FRAME   //////////////////

        JPanel middleFrame = new JPanel();
        middleFrame.setBackground(customColors.NAVYBLUE.backColor);
        middleFrame.setPreferredSize(new Dimension(500, 40));
        statusLabel = new JLabel(" ", SwingConstants.CENTER);         //message to indicate whether coordinate is valid/invalid
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial",Font.BOLD, 20));


        ////////////    LOWER FRAME   //////////////////
        /*lower frame divided into two parts: lowerUpperFrame (for inputtng coords) and lowerLowerFrame (for starting interpolation) */

        JPanel lowerFrame = new JPanel();   // container to hold upper and lower frames
        lowerFrame.setPreferredSize(new Dimension(500, 120));
        lowerFrame.setLayout(new BorderLayout());

        JPanel lowerUpperFrame = new JPanel();
        lowerUpperFrame.setBackground(customColors.NAVYBLUE.backColor);
        lowerUpperFrame.setPreferredSize(new Dimension(500, 80));

        JLabel xLabel = new JLabel("x=");
        JLabel yLabel = new JLabel("y=");
        xLabel.setForeground(Color.WHITE);
        yLabel.setForeground(Color.WHITE);

        addButton = new JButton();
        addButton.setText("Add Point");
        addButton.addActionListener(this);

        addXCoord = new JTextField();
        addYCoord = new JTextField();
        addXCoord.setPreferredSize(new Dimension(50,25));
        addYCoord.setPreferredSize(new Dimension(50,25));

        JPanel superLowerPanel = new JPanel();
        superLowerPanel.setBackground(Color.WHITE);
        superLowerPanel.setPreferredSize(new Dimension(500,60));

        JPanel lowerLowerFrame = new JPanel();
        lowerLowerFrame.setBackground(customColors.NAVYBLUE.backColor);
        lowerLowerFrame.setPreferredSize(new Dimension(500, 40));
        startButton = new JButton("Start Interpolation");
        startButton.addActionListener(this);

        /////////////////////////////////////////////

        // Set layout manager for the content pane
        frame.setLayout(new BorderLayout());

        // Add panels to the content pane
        frame.add(upperFrame, BorderLayout.NORTH);
        frame.add(middleFrame, BorderLayout.CENTER);
        frame.add(lowerFrame, BorderLayout.SOUTH);

        upperFrame.add(title);
        middleFrame.add(statusLabel);

        lowerFrame.add(lowerUpperFrame, BorderLayout.NORTH);
        lowerFrame.add(lowerLowerFrame, BorderLayout.SOUTH);

        lowerUpperFrame.add(xLabel);
        lowerUpperFrame.add(addXCoord);
        lowerUpperFrame.add(yLabel);
        lowerUpperFrame.add(addYCoord);
        lowerUpperFrame.add(addButton);
        lowerLowerFrame.add(startButton);

        frame.setVisible(true);
    }

    /* listener to handle inputs */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == addButton) {
            //get the x & y coordinates from text labels
            if((!addXCoord.getText().isBlank()) && (!addYCoord.getText().isBlank())){

                try {
                    double xCoord = Double.parseDouble(addXCoord.getText().trim());
                    double yCoord = Double.parseDouble(addYCoord.getText().trim());

                    if(xValsRecorded.contains(xCoord)){
                        statusLabel.setText("You cannot contain the same x-coordinates");
                    }
                    else {
                        statusLabel.setText("Coordinate added: (" + xCoord+","+yCoord+")");

                        //reset the text fields
                        addXCoord.setText(null);
                        addYCoord.setText(null);

                        //add new values onto coordinate array list
                        coordinates.add(new Complex[]{new Complex(xCoord,0), new Complex(yCoord,0)});
                        xValsRecorded.add(xCoord);
                    }
                }
                catch (NumberFormatException x){
                    statusLabel.setText("Invalid numbers entered! Please try again");
                }

            } else {
                statusLabel.setText("You can't enter blank characters!");
            }
        }
        else if(e.getSource() ==startButton){

            if(coordinates.size() >= 3) {
                System.out.println("Starting interpolation of data");
                runInterpolation();
            }
            else{
                statusLabel.setText("At least three points are required for interpolation");
            }
        }
    }

    private void addPolynomails(double[] polynomial){

        double[] largerArray;

        if(finalPolynomial.length>0){
            int smallestLength = Math.min(finalPolynomial.length, polynomial.length);
            //add padding to both arrays for addition
            if(finalPolynomial.length > polynomial.length){
                largerArray = finalPolynomial;
            }
            else {
                largerArray = polynomial;
            }
            for(int i=0; i<smallestLength;i++){
                largerArray[i] = polynomial[i] + finalPolynomial[i];
            }
            finalPolynomial = largerArray;
        }
        else{
           finalPolynomial = polynomial;
        }
    }
}
