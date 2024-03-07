

package MathLibs;

/*representation of euler's number in complex form.*/
public class EulersIdentity extends Complex {
    public EulersIdentity(int nRoots) {
        super(Math.cos(6.283185307179586 / (double)nRoots), Math.sin(6.283185307179586 / (double)nRoots));
    }

    public EulersIdentity(int nRoots, int factor) {
        super(Math.cos(6.283185307179586 * (double)factor / (double)nRoots), Math.sin(6.283185307179586 * (double)factor / (double)nRoots));
    }
}



