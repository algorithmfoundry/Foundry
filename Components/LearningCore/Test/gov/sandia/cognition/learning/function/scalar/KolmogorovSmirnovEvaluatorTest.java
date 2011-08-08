/*
 * File:                KolmogorovSmirnovEvaluatorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Apr 30, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.statistics.CumulativeDistributionFunction;
import gov.sandia.cognition.statistics.distribution.ChiSquareDistribution;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author jdmorr
 */
public class KolmogorovSmirnovEvaluatorTest
    extends TestCase
{
    
    /**
     * 
     * @param testName
     */
    public KolmogorovSmirnovEvaluatorTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );

        KolmogorovSmirnovEvaluator instance = new KolmogorovSmirnovEvaluator();
        KolmogorovSmirnovEvaluator clone = instance.clone();
        assertNotSame( instance, clone );
        assertNotSame( instance.getCDF(), clone.getCDF() );
        assertNotNull( clone.getCDF() );
        assertEquals( instance.getCapacity(), clone.getCapacity() );

    }

    /**
     * Test of evaluate method, of class KolmogorovSmirnovEvaluator.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        Random random = new Random(1);
        Double result =0.0 ;
        int N = 100;
        KolmogorovSmirnovEvaluator instance = new KolmogorovSmirnovEvaluator();
        instance.evaluate(null);
        ChiSquareDistribution chisq = new ChiSquareDistribution(3);
        for(int i=0;i<N;i++)
        {
            result = instance.evaluate(chisq.sample(random));
            System.out.printf("KS: %f\n",result);
        }

        System.out.println("3 DOF Chi Sqr KS test: " + result );
        assertTrue( result > 0.1 );
    }


    /**
     * Test of setCDF method, of class KolmogorovSmirnovEvaluator.
     */
    public void testSetCdf()
    {
        System.out.println("setCdf");
        CumulativeDistributionFunction<Double> cdf =
                new ChiSquareDistribution.CDF();
        KolmogorovSmirnovEvaluator instance = new KolmogorovSmirnovEvaluator();
        instance.setCDF(cdf);
        assertEquals(cdf, instance.getCDF());
    }

}
