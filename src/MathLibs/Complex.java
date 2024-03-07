
package MathLibs;

/* class represents a complex number. A number of the form a+bi where a & b are real numbers and i is the imaginary component
   Required as Java maths library doesn't support complex numbers*/
public class Complex {
    private double realNum;
    private double imNum;

    public Complex(double realNum, double imNum) {
        this.realNum = realNum;
        this.imNum = imNum;
    }

    public void print() {
        System.out.print(this.realNum + "+" + this.imNum + "i ");
    }

    public double getRealPart() {
        return this.realNum;
    }

    public double getImaginaryPart() {
        return this.imNum;
    }

    public Complex add(Complex num2) {
        return new Complex(this.realNum + num2.getRealPart(), this.imNum + num2.getImaginaryPart());
    }

    public Complex sub(Complex num2) {
        return new Complex(this.realNum - num2.getRealPart(), this.imNum - num2.getImaginaryPart());
    }

    public Complex multiply(Complex num2) {
        return new Complex(this.realNum * num2.getRealPart() - this.imNum * num2.getImaginaryPart(), this.realNum * num2.getImaginaryPart() + this.imNum * num2.getRealPart());
    }

    public void realFactor(Double factor){
       realNum *= factor;
       imNum *= factor;
    }

    public void conjugate() {
        this.imNum = -this.imNum;
    }

    public void convertRealOnly(){
        imNum = 0;
    }

}
