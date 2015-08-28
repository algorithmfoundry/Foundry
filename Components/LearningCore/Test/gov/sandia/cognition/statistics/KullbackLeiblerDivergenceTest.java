package gov.sandia.cognition.statistics;

import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for Kullback Leibler Divergence.
 * 
 * @author trbroun
 */
public class KullbackLeiblerDivergenceTest
{
    
    static private KullbackLeiblerDivergence<Integer> goodKL;
    static private DefaultDataDistribution<Integer> firstDistribution;
    static private DefaultDataDistribution<Integer> secondDistribution;
    static private DefaultDataDistribution<Integer> thirdDistribution;
    static private DefaultDataDistribution<Integer> fourthDistribution;
    
    public KullbackLeiblerDivergenceTest()
    {
        firstDistribution = new DefaultDataDistribution<Integer>();
        secondDistribution = new DefaultDataDistribution<Integer>();
        thirdDistribution = new DefaultDataDistribution<Integer>();
        fourthDistribution = new DefaultDataDistribution<Integer>();
        Double[] firstDistValues = {5.0,10.0,3.0};
        Double[] secondDistValues = {7.0,2.0,1.0};
        for (int ii = 0; ii < 3; ii++) {
            firstDistribution.set(ii, firstDistValues[ii]);
            secondDistribution.set(ii, secondDistValues[ii]);
            fourthDistribution.set(ii, ii);
        }
        thirdDistribution.set(5,6.0);
        
        goodKL = new KullbackLeiblerDivergence<Integer>(firstDistribution, secondDistribution);
    }
 
    /**
     * Test of getDomain method, of class KullbackLeiblerDivergence.
     */
    @Test
    public void testGetDomain()
    {
        Integer[] answer = {0,1,2};
        Assert.assertArrayEquals(answer, goodKL.getDomain().toArray());
    }

    /**
     * Test of compute method, of class KullbackLeiblerDivergence.
     */
    @Test
    public void testCompute()
    {
        assertEquals(0.3959830, goodKL.compute(), 0.00001);
        KullbackLeiblerDivergence<Integer> otherGoodKL = new KullbackLeiblerDivergence<Integer>(fourthDistribution, firstDistribution);
        assertEquals(0.7539210, otherGoodKL.compute(), 0.00001);
    }
    
    /**
     * Test of various failure cases.
     */
    @Test
    public void testFailures()
    {
        try 
        {
            KullbackLeiblerDivergence<Integer> evilKL = new KullbackLeiblerDivergence<Integer>(null, secondDistribution);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
        }
        
        try 
        {
            KullbackLeiblerDivergence<Integer> evilKL = new KullbackLeiblerDivergence<Integer>(firstDistribution, null);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
        }
        
        try 
        {
            KullbackLeiblerDivergence<Integer> evilKL = new KullbackLeiblerDivergence<Integer>(firstDistribution, thirdDistribution);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
        }
        
        try
        {
            KullbackLeiblerDivergence<Integer> evilKL = new KullbackLeiblerDivergence<Integer>(firstDistribution, fourthDistribution);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
        }
        
        assertTrue(true);
    }
}