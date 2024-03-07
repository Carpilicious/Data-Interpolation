package ArrayPackage;

/*developed by myself: Thien Nguyen
available to use by anyone for any Java project.
Class handles generic data types, suitable for arrays and array lists*/

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;
public class CarpArrays {

    public CarpArrays() {
    }

    /*returns an array with values at even indexes of the inputted array*/
    public static <AnyType> AnyType[] splitEvenIndexes(AnyType[] arrInput) {
        ArrayList<AnyType> newList = new ArrayList();
        IntStream.range(0, Array.getLength(arrInput)).filter((i) -> {
            return i % 2 == 0;
        }).forEach((num) -> {
            newList.add((AnyType) Array.get(arrInput, num));
        });
        return newList.toArray(Arrays.copyOfRange(arrInput, 0, newList.size()));
    }

    /*returns an array with values at odd indexes of the inputted array*/
    public static <AnyType> AnyType[] splitOddIndexes(AnyType[] arrInput) {
        ArrayList<AnyType> newList = new ArrayList();
        IntStream.range(0, Array.getLength(arrInput)).filter((i) -> {
            return i % 2 != 0;
        }).forEach((num) -> {
            newList.add((AnyType) Array.get(arrInput, num));
        });
        return newList.toArray(Arrays.copyOfRange(arrInput, 0, newList.size()));
    }

    /*extracts all data within an array of the 2D array and returns a new array containing them (order preserved)
     * Type: Array*/
    public static <AnyType> AnyType[] pullIndex2D(Class<AnyType> className, AnyType[][] arrInput, int index ){
     AnyType[] newVals = (AnyType[]) Array.newInstance(className,arrInput.length);
     for(int i=0;i< arrInput.length;i++){ newVals[i] = arrInput[i][index]; };
     return newVals;
    }

    /*extracts all data within an array of the 2D array and returns a new array containing them (order preserved)
    * Type: ArrayList*/
    public static <AnyType> ArrayList<AnyType> pullIndex2D(Class<AnyType> className, ArrayList<AnyType[]> arrInput, int index ){
        ArrayList<AnyType> newVals =  new ArrayList<AnyType>(){};
        for(int i=0;i< arrInput.size();i++){ newVals.add(arrInput.get(i)[index]); };
        return newVals;
    }

    /*appends additional values to an array, ideal for mathematical operations such as matrix multiplication*/
    public static <AnyType> AnyType[] addPadding( AnyType[] arrInput, AnyType padVal, int num){
        AnyType[] newVals = Arrays.copyOf(arrInput,arrInput.length + num);
        for(int i=newVals.length - 1; i> newVals.length-num-1;i--){
            newVals[i] = padVal;
        }
        return newVals;
    }


}
