package gov.sandia.cognition.statistics;

import gov.sandia.cognition.math.matrix.optimized.DenseVector;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for chi-squared similarity.
 * 
 * @author trbroun
 */
public class ChiSquaredSimilarityTest
{
    double[] value1 = {1,0,0,1,0,0,0,1,0,0};
    DenseVector vector1 = new DenseVector(value1);
    double[] value2 = {0,1,0,0,1,0,0,0,1,0};
    DenseVector vector2 = new DenseVector(value2);
    double[] value3 = {1,1,1,1,1,1,1,1,1,1};
    DenseVector vector3 = new DenseVector(value3);
    double[] value4 = {0,0};
    DenseVector vector4 = new DenseVector(value4);
    double[] value5 = {0,-1,0,0,1,0,0,0,-1,0};
    DenseVector vector5 = new DenseVector(value2);
    double[] value6 = {0,0,0,0,0,0,0,0,0,0};
    DenseVector vector6 = new DenseVector(value6);
    
    ChiSquaredSimilarity v12 = new ChiSquaredSimilarity(vector1, vector2);
    ChiSquaredSimilarity v11 = new ChiSquaredSimilarity(vector1, vector1);
    ChiSquaredSimilarity v21 = new ChiSquaredSimilarity(vector2, vector1);
    ChiSquaredSimilarity v13 = new ChiSquaredSimilarity(vector1, vector3);
    ChiSquaredSimilarity v14 = new ChiSquaredSimilarity(vector1, vector4);
    ChiSquaredSimilarity v15 = new ChiSquaredSimilarity(vector1, vector5);
    ChiSquaredSimilarity v16 = new ChiSquaredSimilarity(vector1, vector6);

    /**
     * Test of compute method, of class ChiSquaredSimilarity.
     */
    @Test
    public void testCompute()
    {
        assertEquals(6., v12.compute(), 0.0000001);
        assertEquals(6., v21.compute(), 0.0000001);
        assertEquals(4.55, v13.compute(), 0.0000001);
        assertEquals(6., v15.compute(), 0.0000001);
        assertEquals(0., v11.compute(), 0.0000001);
        try {
            v14.compute();
            assertTrue(false);
        } catch(RuntimeException e) {
        }
        try {
            v16.compute();
            assertTrue(false);
        } catch(RuntimeException e) {
        }
    }

    /**
     * Test of completeCompute method, of class ChiSquaredSimilarity.
     */
    @Test
    public void testComputeCumulativeProbabilityValue()
    {
        assertEquals(0.26008169, v12.computeCumulativeProbabilityValue(), 0.0000001);
        assertEquals(0.12835824, v13.computeCumulativeProbabilityValue(), 0.0000001);
        assertEquals(0., v11.computeCumulativeProbabilityValue(), 0.0000001);
    }


    /**
     * Test of setTestVector method, of class ChiSquaredSimilarity.
     */
    @Test
    public void testSetters()
    {
        assertEquals(4.55, v13.compute(), 0.0000001);
        v13.setCategorizedVector(vector3);
        assertEquals(0, v13.compute(), 0.0000001);
        v13.setTestVector(vector1);
        double temp = v13.compute();
        assertEquals(4.55, v13.compute(), 0.0000001);
    }

    /**
     * Test of getCategorizedVector method, of class ChiSquaredSimilarity.
     */
    @Test
    public void testGetters()
    {
        assertTrue(v12.getCategorizedVector().equals(vector1, 0.0001));
        assertTrue(v12.getTestVector().equals(vector2, 0.0001));
    }
    
}
