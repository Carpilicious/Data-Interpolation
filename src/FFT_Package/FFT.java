
package FFT_Package;
import ArrayPackage.CarpArrays;
import MathLibs.Complex;
import MathLibs.EulersIdentity;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class FFT {

    private ArrayList<Complex[]> allCoeffs; //all coefficients of all polynomials that are to be multiplied together

    public FFT() {
        allCoeffs = new ArrayList<Complex[]>();
    }

    public void addPolynomial(Complex[] Coeffs ){  //inserting the polynomials to multiply.
        allCoeffs.add(Coeffs);
    }

    public void clearPolynomial(){
        allCoeffs.clear();
    }

    /* handles multiplication of all polynomials added to 'allCoeffs'
    * Suppose two polynomials are multiplied A(x) and B(x) to result in C(x), they're converted to their point-value representations using FFT
    * algorithm. Subsequently, pass result to IFFT to obtain coefficients of C(x). Run Time - O(nlogn) */

    public Complex[] multiplyAllPolynomial(){
        Complex[] result = allCoeffs.removeFirst();
        Complex padding = new Complex(0,0);

        for(Complex[] currPoly: allCoeffs){
            //calculate how long each coefficiant array should be for divide & conquer to work
            int noFinalTerms = currPoly.length+result.length - 1; // if C(x) has a degree of d, then d+1 points are required
            int newArrLength = (int)Math.pow(2.0, Math.ceil(Math.log((double)noFinalTerms) / Math.log(2.0)));

            //add padding to each array
            result = CarpArrays.addPadding(result,padding,newArrLength-result.length);
            currPoly = CarpArrays.addPadding(currPoly, padding, newArrLength- currPoly.length);

            //calculate FFT for each - obtain point value representation for each polynomial.
            result = runFFT(result);
            currPoly = runFFT(currPoly);

            //calculate C(x) point-values
            Complex[] yVals = new Complex[newArrLength];
            for(int i=0; i<yVals.length;i++){
                yVals[i] = result[i].multiply(currPoly[i]);
            }
            Arrays.stream(yVals).forEach(Complex::conjugate);

            //calculate IFFT
            result = runIFFT(yVals);

            //adjust result size so it has length of noFinalTerms
            result = Arrays.copyOfRange(result,0,noFinalTerms);
            //take the real part only of the result
            Arrays.stream(result).forEach(Complex::convertRealOnly);
        }
       return result;
    }

    /*recursive algorithm to calculate FFT values of a polynomial (point-value representation)*/
    public Complex[] runFFT(Complex[] coeffs) {
        int n = (int)Math.pow(2.0, Math.ceil(Math.log((double)coeffs.length) / Math.log(2.0)));
        if (n == 1) {
            return coeffs;
        } else {
            Complex[] evenCoeffs = (Complex[])CarpArrays.splitEvenIndexes(coeffs);
            Complex[] oddCoeffs = (Complex[])CarpArrays.splitOddIndexes(coeffs);
            Complex[] evens = this.runFFT(evenCoeffs);
            Complex[] odds = this.runFFT(oddCoeffs);
            Complex[] pointVals = new Complex[n];

            for(int i = 0; i < n / 2; ++i) {
                EulersIdentity omegaPowered = new EulersIdentity(n, i);
                pointVals[i] = evens[i].add(omegaPowered.multiply(odds[i]));
                pointVals[i + n / 2] = evens[i].sub(omegaPowered.multiply(odds[i]));
            }
            return pointVals;
        }
    }

    public Complex[] runIFFT(Complex[] values) {
        Complex[] Coefficiants = this.IFFT_Run(values);
        Arrays.stream(Coefficiants).forEach((e)->e.realFactor(1/(double)values.length));
        return Coefficiants;
    }

    /*recursive algorithm to calculate IFFT values of a polynomial (conversion between point and coefficient representation)*/
    private Complex[] IFFT_Run(Complex[] values) {
        int n = (int)Math.pow(2.0, Math.ceil(Math.log((double)values.length) / Math.log(2.0)));
        if (n == 1) {
            return values;
        } else {
            Complex[] evenCoeffs = (Complex[])CarpArrays.splitEvenIndexes(values);
            Complex[] oddCoeffs = (Complex[])CarpArrays.splitOddIndexes(values);
            Complex[] evens = this.runFFT(evenCoeffs);
            Complex[] odds = this.runFFT(oddCoeffs);
            Complex[] Coeff = new Complex[n];

            for(int i = 0; i < n / 2; ++i) {
                EulersIdentity omegaPowered = new EulersIdentity(n, i);
                Coeff[i] = evens[i].add(omegaPowered.multiply(odds[i]));
                Coeff[i + n / 2] = evens[i].sub(omegaPowered.multiply(odds[i]));
            }

            return Coeff;
        }
    }
}
