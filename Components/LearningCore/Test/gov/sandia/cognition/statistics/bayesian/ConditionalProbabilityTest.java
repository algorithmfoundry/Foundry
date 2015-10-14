package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for conditional probability class.
 * 
 * @author trbroun
 */
public class ConditionalProbabilityTest
{
     /**
     * Test of computeConditionalProbability method, of class ConditionalProbability.
     */
    @Test
    public void testComputeConditionalProbability_4args_1()
    {
        DefaultDataDistribution<Collection<Integer>> postDist = new DefaultDataDistribution<Collection<Integer>>();
        DefaultDataDistribution<Collection<Integer>> priorDist = new DefaultDataDistribution<Collection<Integer>>();
        ArrayList<Integer> values = new ArrayList<Integer>();
        values.add(0);
        values.add(0);
        values.add(0);
        postDist.increment(values, 7);
        values = new ArrayList<Integer>();
        values.add(0);
        values.add(0);
        values.add(1);
        postDist.increment(values, 3);
        values = new ArrayList<Integer>();
        values.add(0);
        values.add(1);
        values.add(1);
        postDist.increment(values, 5);
        
        values = new ArrayList<Integer>();
        values.add(0);
        values.add(0);
        priorDist.increment(values, 10);
        values = new ArrayList<Integer>();
        values.add(0);
        values.add(1);
        priorDist.increment(values, 5);
        
        ArrayList<Integer> prior = new ArrayList<Integer>();
        prior.add(0);
        prior.add(0);
        
        Integer posterior = 1;
        
        ConditionalProbability<Integer> instance = new ConditionalProbability<Integer>();
        double expResult = 0.3;
        double result = instance.computeConditionalProbability(prior, posterior, priorDist, postDist);
        assertEquals(expResult, result, 0.00001);
    }

    /**
     * Test of computeConditionalProbability method, of class ConditionalProbability.
     */
    @Test
    public void testComputeConditionalProbability_4args_2()
    {
        DefaultDataDistribution<Collection<Integer>> postDist = new DefaultDataDistribution<Collection<Integer>>();
        DefaultDataDistribution<Integer> priorDist = new DefaultDataDistribution<Integer>();
        
        ArrayList<Integer> values = new ArrayList<Integer>(2);
        values.add(0);
        values.add(0);
        postDist.increment(values, 3);
        values = new ArrayList<Integer>(2);
        values.add(0);
        values.add(1);
        postDist.increment(values, 2);
        values = new ArrayList<Integer>(2);
        values.add(1);
        values.add(0);
        postDist.increment(values, 2);
        values = new ArrayList<Integer>(2);
        values.add(1);
        values.add(1);
        postDist.increment(values, 2);
        
        priorDist.increment(0,5);
        priorDist.increment(1,4);
        
        Integer prior = 0;
        Integer posterior = 0;
        
        ConditionalProbability<Integer> instance = new ConditionalProbability<Integer>();
        double expResult = 0.6;
        double result = instance.computeConditionalProbability(prior, posterior, priorDist, postDist);
        assertEquals(expResult, result, 0.00001);
    }
    
}
