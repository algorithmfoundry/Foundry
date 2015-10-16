package gov.sandia.cognition.statistics;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.DenseVector;
import gov.sandia.cognition.statistics.TransferEntropy.TransferEntropyDistributionObject;
import gov.sandia.cognition.statistics.TransferEntropy.TransferEntropyPartialSumObject;
import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Unit test for transfer entropy.
 * 
 * @author trbroun
 */
public class TransferEntropyTest
{

    /**
     * Test of computeTE method, of class TransferEntropy.
     */
    @Test
    public void testComputeTE()
    {
        VectorFactory vectorFactory = VectorFactory.getDenseDefault();
        final double[] value1 = {1,0,0,1,0,0,0,1,0,0};
        Vector vector1 = vectorFactory.copyArray(value1);
        final double[] value2 = {0,1,0,0,1,0,0,0,1,0};
        Vector vector2 = vectorFactory.copyArray(value2);
        final double[] value3 = {1,1,1,1,1,1,1,1,1,1};
        Vector vector3 = vectorFactory.copyArray(value3);
        
        TransferEntropy te12 = new TransferEntropy(vector1, vector2, 1);
        TransferEntropy te21 = new TransferEntropy(vector2, vector1, 1);
        TransferEntropy te13 = new TransferEntropy(vector1, vector3, 1);
        TransferEntropy te31 = new TransferEntropy(vector3, vector1, 1);
        
        assertEquals(0.2121713, te21.compute(), 0.000001);
        assertEquals(0.4620981, te12.compute(), 0.000001);
        assertEquals(0., te13.compute(), 0.000001);
        assertEquals(0., te31.compute(), 0.000001);
        
        DefaultDataDistribution<TransferEntropyDistributionObject> oneTermPriorDist 
            = new DefaultDataDistribution<TransferEntropyDistributionObject>();
        DefaultDataDistribution<TransferEntropyDistributionObject> oneTermPostDist 
            = new DefaultDataDistribution<TransferEntropyDistributionObject>();
        DefaultDataDistribution<TransferEntropyDistributionObject> twoTermPriorDist 
            = new DefaultDataDistribution<TransferEntropyDistributionObject>();
        DefaultDataDistribution<TransferEntropyDistributionObject> twoTermPostDist 
            = new DefaultDataDistribution<TransferEntropyDistributionObject>();
        
        for (int ii = 0; ii< 9; ii++) {
            ArrayList<Double> first = new ArrayList<Double>(1);
            ArrayList<Double> second = new ArrayList<Double>(1);
            first.add(value1[ii]);
            second.add(value2[ii]);
            double temp = value1[ii+1];
            oneTermPriorDist.increment(new TransferEntropyDistributionObject(first));
            oneTermPostDist.increment(new TransferEntropyDistributionObject(first, temp));
            twoTermPriorDist.increment(new TransferEntropyDistributionObject(first, second));
            twoTermPostDist.increment(new TransferEntropyDistributionObject(first, second, temp));
        }
        ArrayList<Double> first = new ArrayList<Double>(1);
        ArrayList<Double> second = new ArrayList<Double>(1);
        first.add(value1[9]);
        second.add(value2[9]);
        oneTermPriorDist.increment(new TransferEntropyDistributionObject(first));
        twoTermPriorDist.increment(new TransferEntropyDistributionObject(first, second));
        
        TransferEntropy te = new TransferEntropy(oneTermPriorDist, oneTermPostDist,
            twoTermPriorDist, twoTermPostDist);
        
        assertEquals(0.2190444, te.compute(), 0.000001);
        
        List<Double> list1 = new ArrayList<Double>(10);
        List<Double> list2 = new ArrayList<Double>(10);
        List<Double> list3 = new ArrayList<Double>(10);
        
        for (int ii = 0; ii < 10; ii++) {
            list1.add(value1[ii]);
            list2.add(value2[ii]);
            list3.add(value3[ii]);
        }
        
        te12 = new TransferEntropy(list1, list2, 1);
        te21 = new TransferEntropy(list2, list1, 1);
        te13 = new TransferEntropy(list1, list3, 1);
        te31 = new TransferEntropy(list3, list1, 1);
        
        assertEquals(0.2121713, te21.compute(), 0.000001);
        assertEquals(0.4620981, te12.compute(), 0.000001);
        assertEquals(0., te13.compute(), 0.000001);
        assertEquals(0., te31.compute(), 0.000001);
        
        List<Boolean> booleanList1 = new ArrayList<Boolean>(10);
        List<Boolean> booleanList2 = new ArrayList<Boolean>(10);
        
        for (int ii = 0; ii < 10; ii++) {
            if (value1[ii] != 0.) {
                booleanList1.add(true);
            }
            else {
                booleanList1.add(false);
            }
            if (value2[ii] != 0.) {
                booleanList2.add(true);
            }
            else {
                booleanList2.add(false);
            }
        }
        
        te12 = new TransferEntropy(booleanList1, booleanList2, 1);
        te21 = new TransferEntropy(booleanList2, booleanList1, 1);
        
        assertEquals(0.2121713, te21.compute(), 0.000001);
        assertEquals(0.4620981, te12.compute(), 0.000001);
        
        
    }

    /**
     * Test of computeTEWithPartialSums method, of class TransferEntropy.
     */
    @Test
    public void testComputeTEWithPartialSums()
    {
        VectorFactory vectorFactory = VectorFactory.getDenseDefault();
        double[] value1 = {1,0,0,1,0,0,0,1,0,0};
        Vector vector1 = vectorFactory.copyArray(value1);
        double[] value2 = {0,1,0,0,1,0,0,0,1,0};
        Vector vector2 = vectorFactory.copyArray(value2);
        
        TransferEntropy te12 = new TransferEntropy(vector1, vector2, 1);
        
        Pair<Double, HashMap<TransferEntropyDistributionObject, TransferEntropyPartialSumObject>> answer = te12.computeWithPartialSums();
        
        assertEquals(0.4620981, answer.getFirst(), 0.000001);
        
        ArrayList<Integer> first = new ArrayList<Integer>(1);
        ArrayList<Integer> second = new ArrayList<Integer>(1);
        first.add(0);
        second.add(1);
        
        TransferEntropyDistributionObject triad0 = new TransferEntropyDistributionObject(first, second, 1);
        TransferEntropyDistributionObject triad1 = new TransferEntropyDistributionObject(first, second, 0);
        
        assertEquals(0.2310490, answer.getSecond().get(triad0).partialSum, 0.000001);
        assertEquals(triad0, answer.getSecond().get(triad0).state);
        assertEquals(3.0, answer.getSecond().get(triad0).numberOfAppearances, 0.000001);
        assertEquals(null, answer.getSecond().get(triad1));
                
    }
    
    /**
     * Test of equals method for TransferEntropyPartialSumObjects
     */
    @Test
    public void testEqualsForTransferEntropyPartialSumObjects()
    {
        ArrayList<Integer> first = new ArrayList<Integer>(1);
        ArrayList<Integer> second = new ArrayList<Integer>(1);
        first.add(0);
        second.add(1);
        
        TransferEntropyDistributionObject triad0 = new TransferEntropyDistributionObject(first, second, 1);
        TransferEntropyDistributionObject triad1 = new TransferEntropyDistributionObject(first, second, 1);
        
        TransferEntropyPartialSumObject test0 = new TransferEntropyPartialSumObject(triad0, 0.3, 5.0);
        TransferEntropyPartialSumObject test1 = new TransferEntropyPartialSumObject(triad1, 0.3, 5.0);
        
        assertTrue(test0.equals(test1));
    }
    
}
