/*
 * File:                MultinomialDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 16, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.MultivariateClosedFormComputableDiscreteDistributionTestHarness;
import gov.sandia.cognition.statistics.ProbabilityMassFunction;
import gov.sandia.cognition.statistics.method.ChiSquareConfidence;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Unit tests for MultinomialDistributionTest.
 *
 * @author krdixon
 */
public class MultinomialDistributionTest
    extends MultivariateClosedFormComputableDiscreteDistributionTestHarness<Vector>
{

    /**
     * Default confidence test, {@value}.
     */
    public double CONFIDENCE = 0.95;

    /**
     * Tests for class DirichletDistributionTest.
     * @param testName Name of the test.
     */
    public MultinomialDistributionTest(
        String testName)
    {
        super(testName);
    }


    @Override
    public MultinomialDistribution createInstance()
    {
        int numClasses = 3;
        Vector parameters = VectorFactory.getDefault().createVector(numClasses);
        for( int i = 0; i < numClasses; i++ )
        {
            parameters.setElement(i, i+1.0);
        }
        int numTrials = 6;
        return new MultinomialDistribution( parameters, numTrials );
    }

    @Override
    public void testConstructors()
    {
        System.out.println( "constructors" );
        MultinomialDistribution instance = new MultinomialDistribution();
        assertEquals( MultinomialDistribution.DEFAULT_NUM_TRIALS, instance.getNumTrials() );
        assertEquals( MultinomialDistribution.DEFAULT_NUM_CLASSES, instance.getParameters().getDimensionality() );
        for( int i = 0; i < MultinomialDistribution.DEFAULT_NUM_CLASSES; i++ )
        {
            assertEquals( 1.0, instance.getParameters().getElement(i) );
        }

        int nt = RANDOM.nextInt( 10 ) + 2;
        int nc = RANDOM.nextInt( 100 ) + nt + 1;
        instance = new MultinomialDistribution( nc, nt );
        assertEquals( nt, instance.getNumTrials() );
        assertEquals( nc, instance.getParameters().getDimensionality() );
        for( int i = 0; i < nc; i++ )
        {
            assertEquals( 1.0, instance.getParameters().getElement(i) );
        }

        Vector parameters = VectorFactory.getDefault().copyValues( 1.0, 2.0, 3.0 );
        instance = new MultinomialDistribution( parameters, nt );
        assertSame( parameters, instance.getParameters() );
        assertEquals( nt, instance.getNumTrials() );
    }

    /**
     * PMF constructors
     */
    public void testProbabilityFunctionConstructors()
    {
        System.out.println( "constructors.PMF" );

        MultinomialDistribution.PMF instance = new MultinomialDistribution.PMF();
        assertEquals( MultinomialDistribution.DEFAULT_NUM_TRIALS, instance.getNumTrials() );
        assertEquals( MultinomialDistribution.DEFAULT_NUM_CLASSES, instance.getParameters().getDimensionality() );
        for( int i = 0; i < MultinomialDistribution.DEFAULT_NUM_CLASSES; i++ )
        {
            assertEquals( 1.0, instance.getParameters().getElement(i) );
        }

        int nt = RANDOM.nextInt( 10 ) + 2;
        int nc = RANDOM.nextInt( 100 ) + nt + 1;
        instance = new MultinomialDistribution.PMF( nc, nt );
        assertEquals( nt, instance.getNumTrials() );
        assertEquals( nc, instance.getParameters().getDimensionality() );
        for( int i = 0; i < nc; i++ )
        {
            assertEquals( 1.0, instance.getParameters().getElement(i) );
        }

        Vector parameters = VectorFactory.getDefault().copyValues( 1.0, 2.0, 3.0 );
        instance = new MultinomialDistribution.PMF( parameters, nt );
        assertSame( parameters, instance.getParameters() );
        assertEquals( nt, instance.getNumTrials() );

    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "Known values" );

        MultinomialDistribution instance = this.createInstance();

        // Make sure that the samples are from the domain.
        Collection<Vector> data = instance.sample( RANDOM,NUM_SAMPLES );

        MultinomialDistribution.Domain domain = instance.getDomain();
        for( Vector x : data )
        {
            assertTrue( domain.contains(x) );
        }

    }

    @Override
    public void testGetMean()
    {
        double temp = TOLERANCE;
        TOLERANCE = 1e-1;
        super.testGetMean();
        TOLERANCE = temp;
    }

    /**
     * Test of getParameters method, of class MultinomialDistribution.
     */
    public void testGetParameters()
    {
        System.out.println("getParameters");
        MultinomialDistribution instance = this.createInstance();

        Vector parameters = instance.getParameters();
        assertNotNull( parameters );
    }

    /**
     * Test of setParameters method, of class MultinomialDistribution.
     */
    public void testSetParameters()
    {
        System.out.println("setParameters");
        MultinomialDistribution instance = this.createInstance();

        Vector parameters = instance.getParameters();
        assertNotNull( parameters );

        Vector p2 = parameters.scale( RANDOM.nextDouble() );
        instance.setParameters(p2);
        assertSame( p2, instance.getParameters() );

        Vector p3 = p2.clone();
        p3.setElement(0, 0.0);
        instance.setParameters(p3);

        Vector p4 = p3.clone();
        p4.setElement(0, -1.0);
        try
        {
            instance.setParameters(p4);
            fail( "Each element must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            instance.setParameters(null);
            fail( "Cannot be null" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getNumTrials method, of class MultinomialDistribution.
     */
    public void testGetNumTrials()
    {
        System.out.println("getNumTrials");
        MultinomialDistribution instance = this.createInstance();
        assertTrue( instance.getNumTrials() > 0 );
    }

    /**
     * Test of setNumTrials method, of class MultinomialDistribution.
     */
    public void testSetNumTrials()
    {
        System.out.println("setNumTrials");
        MultinomialDistribution instance = this.createInstance();
        assertTrue( instance.getNumTrials() > 0 );

        int numTrials = RANDOM.nextInt(10) + 10;
        instance.setNumTrials(numTrials);
        assertEquals( numTrials, instance.getNumTrials() );

        try
        {
            instance.setNumTrials(0);
            fail( "numTrials must be > 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getDomain method, of class MultinomialDistribution.
     */
    public void testKnownGetDomain()
    {
        System.out.println("getDomain");
        MultinomialDistribution instance = this.createInstance();

        int count = 0;
        MultinomialDistribution.Domain domain = instance.getDomain();
        System.out.println( "Enumerating all subsets: " + domain.size() );
        for( Vector subset : domain )
        {
            System.out.println( "Subset: " + subset );
            count++;
        }

        assertEquals( count, domain.size() );

        Iterator<Vector> iterator = domain.iterator();
        try
        {
            iterator.remove();
            fail( "Cannot remove" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            iterator = new MultinomialDistribution.Domain( 0, 10 ).iterator();
            fail( "numClasses must be > 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            iterator = new MultinomialDistribution.Domain( 3, -1 ).iterator();
            fail( "numTrials must be >= 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * PMF known values
     */
    public void testProbabilityFunctionKnownValues()
    {
        System.out.println( "PMF known values" );

        // http://en.wikipedia.org/wiki/Multinomial_distribution#Example
        Vector parameters = VectorFactory.getDefault().copyValues( 0.2, 0.3, 0.5 );
        MultinomialDistribution.PMF pmf = new MultinomialDistribution.PMF(
            parameters, 6 );

        Vector x = VectorFactory.getDefault().copyValues( 1.0, 2.0, 3.0 );
        assertEquals( 0.135, pmf.evaluate(x), TOLERANCE );

        Vector p = VectorFactory.getDefault().copyValues( 0.5, 0.5, 1.0 );
        int numTrials = 3;
        pmf = new MultinomialDistribution.PMF( p, numTrials );

        Vector xbad1 = VectorFactory.getDefault().copyValues( 1.0, 0.0, 1.0 );
        try
        {
            pmf.evaluate(xbad1);
            fail( "x doesn't add up to numTrials" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        Vector xbad2 = VectorFactory.getDefault().copyValues( 1.5, 0.5, 1.0 );
        try
        {
            pmf.evaluate(xbad2);
            fail( "int(x) doesn't add up to numTrials" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        p.setElement(0,0.0);
        pmf.setParameters(p);
        Vector xok1 = VectorFactory.getDefault().copyValues( 1.0, 0.0, 2.0 );
        assertEquals( 0.0, pmf.evaluate(xok1) );
        Vector xok2 = VectorFactory.getDefault().copyValues( 0.0, 1.0, 2.0 );
        assertEquals( 4.0/9.0, pmf.evaluate(xok2), TOLERANCE );

    }

    /**
     * PMF properties
     */
    public void testPMFProperties()
    {
        System.out.println( "PMF properties" );

        MultinomialDistribution.PMF pmf =
            new MultinomialDistribution.PMF( this.createInstance() );
        
        double sum = 0.0;
        for( Vector x : pmf.getDomain() )
        {
            double p = pmf.evaluate(x);
            assertTrue( 0.0 <= p );
            assertTrue( p <= 1.0 );

            sum += p;
        }

        assertEquals( 1.0, sum, TOLERANCE );
        
    }

    /**
     * PMF.sample
     */
    public void testPMFSample()
    {

        System.out.println( "PMF.sample" );

        MultinomialDistribution.PMF pmf =
            new MultinomialDistribution.PMF( this.createInstance() );

        // Make sure that the samples are from the domain.
        Collection<Vector> data = pmf.sample( RANDOM,NUM_SAMPLES );

        ChiSquareConfidence.Statistic chiSquare =
            ChiSquareConfidence.evaluateNullHypothesis(data, pmf);
        System.out.println( "Chi Square: " + chiSquare );
        assertEquals( 1.0, chiSquare.getNullHypothesisProbability(), CONFIDENCE );

    }

    /**
     * logEvaluate
     */
    public void testLogEvaluate()
    {
        System.out.println( "logEvaluate" );

        ProbabilityMassFunction<Vector> pmf =
            this.createInstance().getProbabilityFunction();
        ArrayList<? extends Vector> samples = pmf.sample(RANDOM, NUM_SAMPLES);

        for( Vector sample : samples )
        {
            double plog = pmf.logEvaluate(sample);
            double p = pmf.evaluate(sample);
            double phat = Math.exp(plog);
            assertEquals( p, phat, TOLERANCE );
        }

    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "Known convertToVector" );
        MultinomialDistribution instance = this.createInstance();
        Vector p = instance.convertToVector();
        assertEquals( instance.getParameters(), p );
        assertNotSame( instance.getParameters(), p );
    }

}
