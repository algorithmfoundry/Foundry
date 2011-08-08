/*
 * File:                MultivariateStudentTDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 29, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.MultivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.MultivariateClosedFormComputableDistributionTestHarness;
import gov.sandia.cognition.statistics.method.KolmogorovSmirnovConfidence;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;

/**
 * Unit tests for MultivariateStudentTDistributionTest.
 *
 * @author krdixon
 */
public class MultivariateStudentTDistributionTest
    extends MultivariateClosedFormComputableDistributionTestHarness<Vector>
{

    /**
     * Tests for class MultivariateStudentTDistributionTest.
     * @param testName Name of the test.
     */
    public MultivariateStudentTDistributionTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class MultivariateStudentTDistributionTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        MultivariateStudentTDistribution instance =
            new MultivariateStudentTDistribution();
        assertEquals( MultivariateStudentTDistribution.DEFAULT_DIMENSIONALITY, instance.getInputDimensionality() );
        assertNotNull( instance.getMean() );
        assertNotNull( instance.getPrecision() );
        assertEquals( MultivariateStudentTDistribution.DEFAULT_DEGREES_OF_FREEDOM, instance.getDegreesOfFreedom() );

        final int dim = 3;
        instance = new MultivariateStudentTDistribution( dim );
        assertEquals( dim, instance.getInputDimensionality() );
        assertEquals( dim, instance.getMean().getDimensionality() );
        assertEquals( dim, instance.getPrecision().getNumRows() );
        assertEquals( MultivariateStudentTDistribution.DEFAULT_DEGREES_OF_FREEDOM,
            instance.getDegreesOfFreedom() );

        MultivariateStudentTDistribution i2 =
            new MultivariateStudentTDistribution( instance );
        assertNotSame( instance.getMean(), i2.getMean() );
        assertEquals( instance.getMean(), i2.getMean() );
        assertNotSame( instance.getPrecision(), i2.getPrecision() );
        assertEquals( instance.getPrecision(), i2.getPrecision() );
        assertEquals( instance.getDegreesOfFreedom(), i2.getDegreesOfFreedom() );

        MultivariateStudentTDistribution i3 =
            new MultivariateStudentTDistribution(
                i2.getDegreesOfFreedom(), i2.getMean(), i2.getPrecision() );
        assertEquals( i2.getDegreesOfFreedom(), i3.getDegreesOfFreedom() );
        assertSame( i2.getMean(), i3.getMean() );
        assertSame( i3.getPrecision(), i3.getPrecision() );

    }

    @Override
    public void testProbabilityFunctionConstructors()
    {
        System.out.println( "PDF Constructors" );

        MultivariateStudentTDistribution.PDF instance =
            new MultivariateStudentTDistribution.PDF();
        assertEquals( MultivariateStudentTDistribution.DEFAULT_DIMENSIONALITY, instance.getInputDimensionality() );
        assertNotNull( instance.getMean() );
        assertNotNull( instance.getPrecision() );
        assertEquals( MultivariateStudentTDistribution.DEFAULT_DEGREES_OF_FREEDOM, instance.getDegreesOfFreedom() );

        final int dim = 3;
        instance = new MultivariateStudentTDistribution.PDF( dim );
        assertEquals( dim, instance.getInputDimensionality() );
        assertEquals( dim, instance.getMean().getDimensionality() );
        assertEquals( dim, instance.getPrecision().getNumRows() );
        assertEquals( MultivariateStudentTDistribution.DEFAULT_DEGREES_OF_FREEDOM,
            instance.getDegreesOfFreedom() );

        MultivariateStudentTDistribution.PDF i2 =
            new MultivariateStudentTDistribution.PDF( instance );
        assertNotSame( instance.getMean(), i2.getMean() );
        assertEquals( instance.getMean(), i2.getMean() );
        assertNotSame( instance.getPrecision(), i2.getPrecision() );
        assertEquals( instance.getPrecision(), i2.getPrecision() );
        assertEquals( instance.getDegreesOfFreedom(), i2.getDegreesOfFreedom() );

        MultivariateStudentTDistribution.PDF i3 =
            new MultivariateStudentTDistribution.PDF(
                i2.getDegreesOfFreedom(), i2.getMean(), i2.getPrecision() );
        assertEquals( i2.getDegreesOfFreedom(), i3.getDegreesOfFreedom() );
        assertSame( i2.getMean(), i3.getMean() );
        assertSame( i3.getPrecision(), i3.getPrecision() );
    }

    /**
     * Test of getDegreesOfFreedom method, of class MultivariateStudentTDistribution.
     */
    public void testGetDegreesOfFreedom()
    {
        System.out.println("getDegreesOfFreedom");
        MultivariateStudentTDistribution instance = this.createInstance();
        assertTrue( instance.getDegreesOfFreedom() > 0.0 );
    }

    /**
     * Test of setDegreesOfFreedom method, of class MultivariateStudentTDistribution.
     */
    public void testSetDegreesOfFreedom()
    {
        System.out.println("setDegreesOfFreedom");
        MultivariateStudentTDistribution instance = this.createInstance();
        double degreesOfFreedom = RANDOM.nextDouble() * 10.0;
        instance.setDegreesOfFreedom(degreesOfFreedom);
        assertEquals( degreesOfFreedom, instance.getDegreesOfFreedom() );
        try
        {
            instance.setDegreesOfFreedom(0.0);
            fail( "DOFs must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
    }

    /**
     * Test of setMean method, of class MultivariateStudentTDistribution.
     */
    public void testSetMean()
    {
        System.out.println("setMean");
        MultivariateStudentTDistribution instance = this.createInstance();
        Vector mean = instance.getMean().scale( RANDOM.nextGaussian() );
        instance.setMean(mean);
        assertSame( mean, instance.getMean() );
    }

    /**
     * Test of getPrecision method, of class MultivariateStudentTDistribution.
     */
    public void testGetPrecision()
    {
        System.out.println("getPrecision");
        MultivariateStudentTDistribution instance = this.createInstance();
        assertEquals( instance.getMean().getDimensionality(),
            instance.getPrecision().getNumRows() );
    }

    /**
     * Test of setPrecision method, of class MultivariateStudentTDistribution.
     */
    public void testSetPrecision()
    {
        System.out.println("setPrecision");
        MultivariateStudentTDistribution instance =
            this.createInstance();
        Matrix p = instance.getPrecision().clone();
        instance.setPrecision(p);
        assertSame( p, instance.getPrecision() );

        p.setElement(0, 1, p.getElement(0,1)+1.0);
        try
        {
            instance.setPrecision(p);
            fail( "Precision must be symmetric" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getCovariance method, of class MultivariateStudentTDistribution.
     */
    public void testGetCovariance()
    {
        System.out.println("getCovariance");
        MultivariateStudentTDistribution instance = this.createInstance();

        ArrayList<? extends Vector> samples = instance.sample(RANDOM, NUM_SAMPLES);
        Pair<Vector,Matrix> pair =
            MultivariateStatisticsUtil.computeMeanAndCovariance(samples);
        System.out.println( "Empirical:\n" + pair.getSecond() );
        System.out.println( "Result:\n" + instance.getCovariance() );
        assertTrue( instance.getCovariance().equals( pair.getSecond(), 3e-1 ) );
    }

    /**
     * Test of getInputDimensionality method, of class MultivariateStudentTDistribution.
     */
    public void testGetInputDimensionality()
    {
        System.out.println("getInputDimensionality");
        MultivariateStudentTDistribution instance = this.createInstance();
        assertEquals( instance.getMean().getDimensionality(), instance.getInputDimensionality() );
    }

    @Override
    public MultivariateStudentTDistribution createInstance()
    {
        Vector mean = VectorFactory.getDefault().copyValues(
            RANDOM.nextGaussian(), RANDOM.nextGaussian() );
        Matrix R = MatrixFactory.getDefault().createUniformRandom(
            2,2, -1.0, 1.0, RANDOM);
        Matrix C = R.times( R.transpose() );
        return new MultivariateStudentTDistribution(12.0, mean, C);
    }

    @Override
    public void testProbabilityFunctionKnownValues()
    {
        System.out.println( "PDF Known Values" );

        StudentTDistribution.PDF scalar =
            new StudentTDistribution.PDF( 3.0, 2.0, 0.1 );

        MultivariateStudentTDistribution.PDF instance =
            new MultivariateStudentTDistribution.PDF(
                scalar.getDegreesOfFreedom(),
                VectorFactory.getDefault().copyValues(scalar.getMean()),
                MatrixFactory.getDefault().createIdentity(1,1).scale(scalar.getPrecision()) );

        ArrayList<Double> xs = scalar.sample(RANDOM, NUM_SAMPLES);
        for( Double x : xs )
        {
            Vector X = VectorFactory.getDefault().copyValues(x);
            assertEquals( scalar.evaluate(x), instance.evaluate(X), TOLERANCE );
        }

    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "Known convertToVector" );

        MultivariateStudentTDistribution instance = this.createInstance();
        Vector parameters = instance.convertToVector();
        int dim = instance.getInputDimensionality();
        assertEquals( 1+dim+dim*dim, parameters.getDimensionality() );
        assertEquals( instance.getDegreesOfFreedom(), parameters.getElement(0) );
        assertEquals( instance.getMean(), parameters.subVector(1, dim) );
        assertEquals( instance.getPrecision().convertToVector(),
            parameters.subVector(dim+1, parameters.getDimensionality()-1) );
    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "Known Values" );

        StudentTDistribution.CDF cdf =
            new StudentTDistribution.CDF( 6.0, 2.0, 0.1 );

        MultivariateStudentTDistribution instance =
            new MultivariateStudentTDistribution(
                cdf.getDegreesOfFreedom(),
                VectorFactory.getDefault().copyValues(cdf.getMean()),
                MatrixFactory.getDefault().createIdentity(1,1).scale(cdf.getPrecision()) );

        ArrayList<Vector> samples = instance.sample(RANDOM,NUM_SAMPLES);
        ArrayList<Double> scalars = new ArrayList<Double>(NUM_SAMPLES);
        for( Vector X : samples )
        {
            scalars.add( X.getElement(0) );
        }

        KolmogorovSmirnovConfidence.Statistic kstest =
            KolmogorovSmirnovConfidence.evaluateNullHypothesis(scalars, cdf);
        System.out.println( "K-S Test: " + kstest );
        assertEquals( 1.0, kstest.getNullHypothesisProbability(), 0.95 );
    }

    @Override
    public void testGetMean()
    {
        double temp = TOLERANCE;
        TOLERANCE = 1.1e-1;
        super.testGetMean();
        TOLERANCE = temp;
    }

}
