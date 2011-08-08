/*
 * File:                StandardNormalizerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 21, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.data.feature;

import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes: StandardDistributionNormalizer
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class StandardDistributionNormalizerTest
    extends TestCase
{

    public final Random RANDOM = new Random(1);

    public final double TOLERANCE = 1e-5;
    public StandardDistributionNormalizerTest(
        String testName)
    {
        super(testName);
    }

    public void testConstants()
    {
        assertEquals(0.0, StandardDistributionNormalizer.DEFAULT_MEAN);
        assertEquals(1.0, StandardDistributionNormalizer.DEFAULT_VARIANCE);
    }
    
    public void testConstructors()
    {
        StandardDistributionNormalizer instance = 
            new StandardDistributionNormalizer();

        assertEquals(StandardDistributionNormalizer.DEFAULT_MEAN, 
            instance.getMean());
        assertEquals(StandardDistributionNormalizer.DEFAULT_VARIANCE, 
            instance.getVariance());
        
        double mean = RANDOM.nextGaussian();
        double variance = RANDOM.nextDouble();
        instance = new StandardDistributionNormalizer(mean, variance);
        assertEquals(mean, instance.getMean());
        assertEquals(variance, instance.getVariance());
        
        UnivariateGaussian gaussian = new UnivariateGaussian(mean, variance);
        instance = new StandardDistributionNormalizer(gaussian);
        assertEquals(mean, instance.getMean());
        assertEquals(variance, instance.getVariance());
        
        instance = new StandardDistributionNormalizer(instance);
        assertEquals(mean, instance.getMean());
        assertEquals(variance, instance.getVariance());
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.normalization.StandardDistributionNormalizer.
     */
    public void testClone()
    {
        double mean = RANDOM.nextGaussian();
        double variance = RANDOM.nextDouble();
        StandardDistributionNormalizer instance = 
            new StandardDistributionNormalizer(mean, variance);
        
        StandardDistributionNormalizer clone = instance.clone();
        assertNotNull( clone );
        assertNotSame(instance, clone);
        assertEquals(mean, clone.getMean());
        assertEquals(variance, clone.getVariance());
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.normalization.StandardDistributionNormalizer.
     */
    public void testEvaluate()
    {
        double mean = RANDOM.nextGaussian();
        double variance = RANDOM.nextDouble();
        StandardDistributionNormalizer instance = 
            new StandardDistributionNormalizer(mean, variance);
        
        assertEquals(0.0, instance.evaluate(mean));
        assertEquals(+1.0, instance.evaluate(mean + Math.sqrt(variance)), TOLERANCE);
        assertEquals(-1.0, instance.evaluate(mean - Math.sqrt(variance)), TOLERANCE);
        
        
        int count = 100;
        for (int i = 0; i < count; i++)
        {
            double value = RANDOM.nextGaussian();
            double expected = (value - mean) / Math.sqrt(variance);
            assertEquals(expected, instance.evaluate(value), TOLERANCE);
        }
    }

    /**
     * Test of getMean method, of class gov.sandia.cognition.learning.normalization.StandardDistributionNormalizer.
     */
    public void testGetMean()
    {
        this.testSetMean();
    }

    /**
     * Test of setMean method, of class gov.sandia.cognition.learning.normalization.StandardDistributionNormalizer.
     */
    public void testSetMean()
    {
        StandardDistributionNormalizer instance = new StandardDistributionNormalizer();
        assertEquals(StandardDistributionNormalizer.DEFAULT_MEAN, instance.getMean());
        
        double mean = RANDOM.nextGaussian();
        instance.setMean(mean);
        assertEquals(mean, instance.getMean());
        
        mean = -RANDOM.nextGaussian();
        instance.setMean(mean);
        assertEquals(mean, instance.getMean());
        
        mean = 0.0;
        instance.setMean(mean);
        assertEquals(mean, instance.getMean());
    }

    /**
     * Test of getStandardDeviation method, of class gov.sandia.cognition.learning.normalization.StandardDistributionNormalizer.
     */
    public void testGetVariance()
    {
        this.testSetVariance();
    }

    /**
     * Test of setStandardDeviation method, of class gov.sandia.cognition.learning.normalization.StandardDistributionNormalizer.
     */
    public void testSetVariance()
    {
        StandardDistributionNormalizer instance = new StandardDistributionNormalizer();
        assertEquals(StandardDistributionNormalizer.DEFAULT_VARIANCE, 
            instance.getVariance());
        
        double variance = RANDOM.nextDouble();
        instance.setVariance(variance);
        assertEquals(variance, instance.getVariance());
        
        variance += RANDOM.nextDouble();
        instance.setVariance(variance);
        assertEquals(variance, instance.getVariance());
        
        boolean exceptionThrown = false;
        try
        {
            instance.setVariance(0.0);
        }
        catch ( IllegalArgumentException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        exceptionThrown = false;
        try
        {
            instance.setVariance(-Math.random());
        }
        catch ( IllegalArgumentException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

    /**
     * Test of learn method, of class gov.sandia.cognition.learning.normalization.StandardDistributionNormalizer.
     */
    public void testLearn()
    {
        Collection<Double> values = new ArrayList<Double>();
        
        values.add(4.0);
        values.add(4.5);
        values.add(6.5);
        values.add(7.0);
        
        StandardDistributionNormalizer instance = 
            StandardDistributionNormalizer.learn(values);
        assertEquals(5.5, instance.getMean(), TOLERANCE);
        assertEquals(1.625, instance.getVariance(), TOLERANCE );
        
        values.add(5.5);
        instance = 
            StandardDistributionNormalizer.learn(values);
        assertEquals(5.5, instance.getMean(), TOLERANCE);
        assertEquals(1.3, instance.getVariance(), TOLERANCE);
        
        values.add(-1000.0);
        values.add(1000000.0);
        instance = 
            StandardDistributionNormalizer.learn(values, 2.0 / ((double) values.size()));
        assertEquals(5.5, instance.getMean(), TOLERANCE);
        assertEquals(1.3, instance.getVariance(), TOLERANCE);
        
        values.clear();
        values.add(4.7);
        instance = StandardDistributionNormalizer.learn(values);
        assertEquals(4.7, instance.getMean());
        assertEquals(1.0, instance.getVariance());
        
        boolean exceptionThrown = false;
        try
        {
            instance = StandardDistributionNormalizer.learn(null);
        }
        catch ( NullPointerException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        
        exceptionThrown = false;
        try
        {
            instance = StandardDistributionNormalizer.learn(values, 1.0);
        }
        catch ( IllegalArgumentException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        exceptionThrown = false;
        try
        {
            instance = StandardDistributionNormalizer.learn(values, -RANDOM.nextDouble());
        }
        catch ( IllegalArgumentException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        values.clear();
        exceptionThrown = false;
        try
        {
            instance = StandardDistributionNormalizer.learn(values);
        }
        catch ( IllegalArgumentException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }
    

    public void testLearnerConstants()
    {
        assertEquals(0.0,
            StandardDistributionNormalizer.Learner.DEFAULT_OUTLIER_PERCENT);
    }

    public void testLearnerConstructors()
    {
        StandardDistributionNormalizer.Learner instance =
            new StandardDistributionNormalizer.Learner();
        assertEquals(StandardDistributionNormalizer.Learner.DEFAULT_OUTLIER_PERCENT,
            instance.getOutlierPercent());

        double outlierPercent = RANDOM.nextDouble();
        instance =
            new StandardDistributionNormalizer.Learner(outlierPercent);
        assertEquals(outlierPercent, instance.getOutlierPercent());
    }

    /**
     * Test of learn method, of class gov.sandia.cognition.learning.normalization.StandardDistributionNormalizer.Learner.
     */
    public void testLearner()
    {
        Collection<Double> values = new ArrayList<Double>();

        values.add(4.0);
        values.add(4.5);
        values.add(6.5);
        values.add(7.0);

        StandardDistributionNormalizer.Learner instance =
            new StandardDistributionNormalizer.Learner();

        StandardDistributionNormalizer normalizer = instance.learn(values);
        assertEquals(5.5, normalizer.getMean(), TOLERANCE);
        assertEquals(1.625, normalizer.getVariance(), TOLERANCE);

        values.add(5.5);
        normalizer = instance.learn(values);
        assertEquals(5.5, normalizer.getMean(), TOLERANCE);
        assertEquals(1.3, normalizer.getVariance(), TOLERANCE);

        values.add(-1000.0);
        values.add(1000000.0);
        instance.setOutlierPercent(2.0 / ((double) values.size()));
        normalizer = instance.learn(values);
        assertEquals(5.5, normalizer.getMean(), TOLERANCE);
        assertEquals(1.3, normalizer.getVariance(), TOLERANCE);

        values.clear();
        values.add(4.7);
        instance.setOutlierPercent(0.0);
        normalizer = instance.learn(values);
        assertEquals(4.7, normalizer.getMean());
        assertEquals(1.0, normalizer.getVariance());
    }

    /**
     * Test of getOutlierPercent method, of class gov.sandia.cognition.learning.normalization.StandardDistributionNormalizer.Learner.
     */
    public void testLearnerGetOutlierPercent()
    {
        this.testLearnerSetOutlierPercent();
    }

    /**
     * Test of setOutlierPercent method, of class gov.sandia.cognition.learning.normalization.StandardDistributionNormalizer.Learner.
     */
    public void testLearnerSetOutlierPercent()
    {
        StandardDistributionNormalizer.Learner instance =
            new StandardDistributionNormalizer.Learner();
        assertEquals(StandardDistributionNormalizer.Learner.DEFAULT_OUTLIER_PERCENT,
            instance.getOutlierPercent());

        double outlierPercent = RANDOM.nextDouble();
        instance.setOutlierPercent(outlierPercent);
        assertEquals(outlierPercent, instance.getOutlierPercent());

        outlierPercent *= RANDOM.nextDouble();
        instance.setOutlierPercent(outlierPercent);
        assertEquals(outlierPercent, instance.getOutlierPercent());

        boolean exceptionThrown = false;
        try
        {
            instance.setOutlierPercent(1.0);
        }
        catch ( IllegalArgumentException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }

        exceptionThrown = false;
        try
        {
            instance.setOutlierPercent(-RANDOM.nextDouble());
        }
        catch ( IllegalArgumentException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

}
