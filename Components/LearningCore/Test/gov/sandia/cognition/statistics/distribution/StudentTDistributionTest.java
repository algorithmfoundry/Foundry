/*
 * File:                StudentTDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 9, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */


package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.SmoothScalarDistributionTestHarness;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import java.util.ArrayList;

/**
 *
 * @author Kevin R. Dixon
 */
public class StudentTDistributionTest
    extends SmoothScalarDistributionTestHarness
{

    /**
     * Constructor
     * @param testName name
     */
    public StudentTDistributionTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public StudentTDistribution createInstance()
    {
//        return new StudentTDistribution( RANDOM.nextDouble() * 10.0 );
        return new StudentTDistribution(
            RANDOM.nextDouble() * 10.0 + 5.0,
            RANDOM.nextDouble()*10.0 + 10.0,
            RANDOM.nextDouble() *0.1 + 0.1 );
//            RANDOM.nextGaussian(),
//            RANDOM.nextDouble() * 3.0 + 2.0 );
    }

    @Override
    public void testPDFKnownValues()
    {
        System.out.println( "PDF.evaluate" );
        
        // I got these values from octave's t_pdf() function
        StudentTDistribution.PDF instance = new StudentTDistribution.PDF( 5.0 );
        assertEquals( 0.3796066898, instance.evaluate( 0.0 ), TOLERANCE );
        instance.setDegreesOfFreedom( 100.0 );
        assertEquals( 0.3979461869, instance.evaluate( 0.0 ), TOLERANCE );
        instance.setDegreesOfFreedom( 10.0 );
        assertEquals( 0.2303619892, instance.evaluate( 1 ), TOLERANCE );
        assertEquals( 0.2303619892, instance.evaluate(-1 ), TOLERANCE );
        assertEquals( 0.0611457663, instance.evaluate( 2 ), TOLERANCE );
        assertEquals( 0.0611457663, instance.evaluate(-2 ), TOLERANCE );
        instance.setDegreesOfFreedom( 3.0 );
        assertEquals( 0.0675096607, instance.evaluate( 2 ), TOLERANCE );
        assertEquals( 0.0675096607, instance.evaluate(-2 ), TOLERANCE );

        instance.setMean(1.0);
        assertEquals( 0.0675096607, instance.evaluate( 3 ), TOLERANCE );
        assertEquals( 0.0675096607, instance.evaluate(-1 ), TOLERANCE );

    }

    @Override
    public void testCDFKnownValues()
    {
        System.out.println("CDF.evaluate");
        StudentTDistribution.CDF cdf = this.createInstance().getCDF();
        double mean = cdf.getMean();
        assertEquals( 0.5, cdf.evaluate( cdf.getMean() ), TOLERANCE  );

        cdf.setPrecision(1.0);
        cdf.setDegreesOfFreedom(1.0);
        final double OLD_TOLERANCE = TOLERANCE;
        TOLERANCE = 1e-3;
        assertEquals( 1-0.10/2, cdf.evaluate( 6.314+mean ), TOLERANCE  );
        assertEquals( 1-0.05/2, cdf.evaluate(12.706+mean ), TOLERANCE  );
        assertEquals( 1-0.01/2, cdf.evaluate(63.657+mean ), TOLERANCE  );

        // Try the reflected values here
        assertEquals( 0.10/2, cdf.evaluate( -6.314+mean ), TOLERANCE  );
        assertEquals( 0.05/2, cdf.evaluate(-12.706+mean ), TOLERANCE  );
        assertEquals( 0.01/2, cdf.evaluate(-63.657+mean ), TOLERANCE  );

        cdf.setDegreesOfFreedom(2.0);
        assertEquals( 1-0.10/2, cdf.evaluate( 2.920+mean ), TOLERANCE  );
        assertEquals( 1-0.05/2, cdf.evaluate( 4.303+mean ), TOLERANCE  );
        assertEquals( 1-0.01/2, cdf.evaluate( 9.925+mean ), TOLERANCE  );

        cdf.setDegreesOfFreedom(10.0);
        assertEquals( 1-0.10/2, cdf.evaluate( 1.812+mean ), TOLERANCE  );
        assertEquals( 1-0.05/2, cdf.evaluate( 2.228+mean ), TOLERANCE  );
        assertEquals( 1-0.01/2, cdf.evaluate( 3.169+mean ), TOLERANCE  );

        cdf.setDegreesOfFreedom(40.0);
        assertEquals( 1-0.10/2, cdf.evaluate( 1.684+mean ), TOLERANCE  );
        assertEquals( 1-0.05/2, cdf.evaluate( 2.021+mean ), TOLERANCE  );
        assertEquals( 1-0.01/2, cdf.evaluate( 2.704+mean ), TOLERANCE  );

        cdf.setDegreesOfFreedom(1000.0);
        assertEquals( 1-0.10/2, cdf.evaluate( 1.645+mean ), TOLERANCE  );
        assertEquals( 1-0.05/2, cdf.evaluate( 1.960+mean ), TOLERANCE  );
        assertEquals( 1-0.01/2, cdf.evaluate( 2.576+mean ), TOLERANCE  );
        TOLERANCE = OLD_TOLERANCE;
    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "CDF.convertToVector" );
        
        StudentTDistribution.CDF cdf = this.createInstance().getCDF();
        Vector x = cdf.convertToVector();
        assertEquals( 3, x.getDimensionality() );
        assertEquals( cdf.getDegreesOfFreedom(), x.getElement( 0 ) );
        assertEquals( cdf.getMean(), x.getElement( 1 ) );
        assertEquals( cdf.getPrecision(), x.getElement( 2 ) );
    }

    /**
     * Degenerate Variance
     */
    public void testStudentTVariance2()
    {

        System.out.println( "Degenerate variance=2" );

        // This method measures the empirical variance for dof=2.0
        // It is empically about 11.840391255691173, but can be anywhere
        // from 8-20.
        StudentTDistribution cdf = new StudentTDistribution.CDF( 2.0 );

        ArrayList<Double> samples = cdf.sample(RANDOM, NUM_SAMPLES);
        double variance = UnivariateStatisticsUtil.computeVariance(samples);
        System.out.println( "Dof = " + cdf.getDegreesOfFreedom() + ", Variance = " + variance );

    }

    /**
     * Degenerate variance
     */
    public void testStudentTVariance1()
    {

        System.out.println( "Degenerate variance=1" );

        // This method measures the empirical variance for dof=1.0
        // It is empically tending to infinity, about 245089.9606973919
        StudentTDistribution cdf = new StudentTDistribution.CDF( 1.0 );

        ArrayList<Double> samples = cdf.sample(RANDOM, NUM_SAMPLES);
        double variance = UnivariateStatisticsUtil.computeVariance(samples);
        System.out.println( "Dof = " + cdf.getDegreesOfFreedom() + ", Variance = " + variance );
    }

    /**
     * getDegreesOfFreedom
     */
    public void testGetDegreesOfFreedom()
    {
        System.out.println( "getDegreesOfFreedom" );
        double dof = RANDOM.nextDouble();
        StudentTDistribution t = new StudentTDistribution( dof );
        assertEquals( dof, t.getDegreesOfFreedom() );

        t = new StudentTDistribution.CDF();
        assertEquals( StudentTDistribution.DEFAULT_DEGREES_OF_FREEDOM, t.getDegreesOfFreedom() );
    }

    /**
     * setDegreesOfFreedom
     */
    public void testSetDegreesOfFreedom()
    {
        System.out.println( "setDegreesOfFreedom" );

        double dof = RANDOM.nextDouble();
        StudentTDistribution t = new StudentTDistribution( dof );
        assertEquals( dof, t.getDegreesOfFreedom() );

        double d2 = dof + RANDOM.nextDouble();
        t.setDegreesOfFreedom(d2);
        assertEquals( d2, t.getDegreesOfFreedom() );

        try
        {
            t.setDegreesOfFreedom(0.0);
            fail( "DOFs must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    @Override
    public void testPDFConstructors()
    {
        System.out.println( "PDF Constructors" );
        StudentTDistribution.PDF f = new StudentTDistribution.PDF();
        assertEquals( StudentTDistribution.DEFAULT_DEGREES_OF_FREEDOM, f.getDegreesOfFreedom() );

        double dof = Math.abs( RANDOM.nextGaussian() ) + 3.0;
        f = new StudentTDistribution.PDF( dof );
        assertEquals( dof, f.getDegreesOfFreedom() );

        StudentTDistribution.PDF f2 = new StudentTDistribution.PDF( f );
        assertEquals( f.getDegreesOfFreedom(), f2.getDegreesOfFreedom() );
    }

    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Constructors" );
        StudentTDistribution f = new StudentTDistribution();
        assertEquals( StudentTDistribution.DEFAULT_DEGREES_OF_FREEDOM, f.getDegreesOfFreedom() );

        double dof = Math.abs( RANDOM.nextGaussian() ) + 3.0;
        f = new StudentTDistribution( dof );
        assertEquals( dof, f.getDegreesOfFreedom() );

        StudentTDistribution f2 = new StudentTDistribution( f );
        assertEquals( f.getDegreesOfFreedom(), f2.getDegreesOfFreedom() );
    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "CDF Constructors" );
        StudentTDistribution.CDF f = new StudentTDistribution.CDF();
        assertEquals( StudentTDistribution.DEFAULT_DEGREES_OF_FREEDOM, f.getDegreesOfFreedom() );

        double dof = Math.abs( RANDOM.nextGaussian() ) + 3.0;
        f = new StudentTDistribution.CDF( dof );
        assertEquals( dof, f.getDegreesOfFreedom() );

        StudentTDistribution.CDF f2 = new StudentTDistribution.CDF( f );
        assertEquals( f.getDegreesOfFreedom(), f2.getDegreesOfFreedom() );
    }

    /**
     * setPrecision
     */
    public void testSetPrecision()
    {
        System.out.println( "setPrecision" );
        StudentTDistribution instance = this.createInstance();
        double precision = RANDOM.nextDouble();
        instance.setPrecision(precision);
        assertEquals( precision, instance.getPrecision() );
        try
        {
            instance.setPrecision(0.0);
            fail( "Precision must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    /**
     * Learner
     */
    public void testLearner()
    {
        System.out.println( "Learner" );

        StudentTDistribution.MaximumLikelihoodEstimator learner =
            new StudentTDistribution.MaximumLikelihoodEstimator();
        this.distributionEstimatorTest(learner);
    }

    /**
     * Weighted learner
     */
    public void testWeightedLearner()
    {
        System.out.println( "Weighted Learner" );

        StudentTDistribution.WeightedMaximumLikelihoodEstimator learner =
            new StudentTDistribution.WeightedMaximumLikelihoodEstimator();
        this.weightedDistributionEstimatorTest(learner);
    }

}
