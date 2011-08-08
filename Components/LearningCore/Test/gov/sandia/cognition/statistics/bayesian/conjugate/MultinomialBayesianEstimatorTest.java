/*
 * File:                MultinomialBayesianEstimatorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 17, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian.conjugate;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.distribution.DirichletDistribution;
import gov.sandia.cognition.statistics.distribution.MultinomialDistribution;

/**
 * Unit tests for MultinomialBayesianEstimatorTest.
 *
 * @author krdixon
 */
public class MultinomialBayesianEstimatorTest
    extends ConjugatePriorBayesianEstimatorTestHarness<Vector,Vector,DirichletDistribution>
{

    /**
     * Tests for class MultinomialBayesianEstimatorTest.
     * @param testName Name of the test.
     */
    public MultinomialBayesianEstimatorTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class MultinomialBayesianEstimatorTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        MultinomialBayesianEstimator instance = new MultinomialBayesianEstimator();
        assertEquals( MultinomialBayesianEstimator.DEFAULT_NUM_TRIALS, instance.getNumTrials() );
        assertEquals( MultinomialBayesianEstimator.DEFAULT_NUM_CLASSES, instance.getInitialBelief().getParameters().getDimensionality() );

        int numTrials = RANDOM.nextInt(100) + 10;
        instance = new MultinomialBayesianEstimator( numTrials );
        assertEquals( numTrials, instance.getNumTrials() );
        assertEquals( MultinomialBayesianEstimator.DEFAULT_NUM_CLASSES, instance.getInitialBelief().getParameters().getDimensionality() );

        DirichletDistribution d = new DirichletDistribution();
        instance = new MultinomialBayesianEstimator( d, numTrials );
        assertEquals( numTrials, instance.getNumTrials() );
        assertSame( d, instance.getInitialBelief() );

    }

    /**
     * Test of getNumTrials method, of class MultinomialBayesianEstimator.
     */
    public void testGetNumTrials()
    {
        System.out.println("getNumTrials");
        MultinomialBayesianEstimator instance = this.createInstance();
        int numTrials = instance.getNumTrials();
        assertTrue( numTrials > 0 );
    }

    /**
     * Test of setNumTrials method, of class MultinomialBayesianEstimator.
     */
    public void testSetNumTrials()
    {
        System.out.println("setNumTrials");
        MultinomialBayesianEstimator instance = this.createInstance();
        int numTrials = instance.getNumTrials();
        assertTrue( numTrials > 0 );
        int nt2 = numTrials + 1;
        instance.setNumTrials(nt2);
        assertEquals( nt2, instance.getNumTrials() );

        try
        {
            instance.setNumTrials(0);
            fail( "numTrials must be > 0");
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    final int NUM_CLASSES = 3;
    final int NUM_TRIALS = 6;

    @Override
    public MultinomialBayesianEstimator createInstance()
    {
        return new MultinomialBayesianEstimator( NUM_CLASSES, NUM_TRIALS );
    }

    @Override
    public MultinomialDistribution createConditionalDistribution()
    {
        Vector parameters = VectorFactory.getDefault().createVector(NUM_CLASSES);
        for( int i = 0; i < NUM_CLASSES; i++ )
        {
            parameters.setElement(i, i+1.0);
        }
        return new MultinomialDistribution( parameters, NUM_TRIALS );
    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "Known Values" );

        int numTrials = 4;
        Vector a = VectorFactory.getDefault().copyValues( 1.0, 1.0, 1.0 );
        MultinomialBayesianEstimator instance = new MultinomialBayesianEstimator(
            new DirichletDistribution(a.clone()), numTrials );

        Vector x0 = VectorFactory.getDefault().copyValues( 2.0, 2.0, 0.0 );
        DirichletDistribution belief = instance.getInitialBelief();
        instance.update( belief, x0 );
        assertEquals( a.plus(x0), belief.getParameters() );

        Vector x1 = VectorFactory.getDefault().copyValues( 1.0, 2.0, 1.0 );
        instance.update( belief, x1 );
        assertEquals( a.plus(x0).plus(x1), belief.getParameters() );
    }

}
