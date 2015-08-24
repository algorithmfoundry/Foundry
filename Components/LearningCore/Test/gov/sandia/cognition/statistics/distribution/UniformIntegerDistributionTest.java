/*
 * File:            UniformIntegerDistributionTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2015 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector1;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.statistics.ClosedFormIntegerDistributionTestHarness;

/**
 * Unit tests for class {@link UniformIntegerDistribution}.
 * 
 * @author  Justin Basilico
 * @since   3.4.3
 */
public class UniformIntegerDistributionTest
    extends ClosedFormIntegerDistributionTestHarness
{
   
    /**
     * Creates a new test.
     * 
     * @param testName The test name.
     */
    public UniformIntegerDistributionTest(
        final String testName)
    {
        super(testName);
        
        NUM_SAMPLES = 2000;
    }

    @Override
    public UniformIntegerDistribution createInstance()
    {
        int l = RANDOM.nextInt(20) - 10;
        int u = l + RANDOM.nextInt(10);
        return new UniformIntegerDistribution(l, u);
    }
    
    @Override
    public void testKnownGetDomain()
    {
        for (int i = 0; i < 100; i++)
        {
            UniformIntegerDistribution instance = 
                this.createInstance();
            int a = instance.getMinSupport();
            int b = instance.getMaxSupport();
            
            int expected = a;
            for (Number x : instance.getDomain())
            {
                assertEquals(expected, x);
                expected++;
            }
            assertEquals(b + 1, expected);
        }
    }

    @Override
    public void testPMFKnownValues()
    {
        for (int i = 0; i < 100; i++)
        {
            UniformIntegerDistribution.PMF pmf = 
                this.createInstance().getProbabilityFunction();
            int a = pmf.getMinSupport();
            int b = pmf.getMaxSupport();
            
            assertEquals(0.0, pmf.evaluateAsDouble(a - 1), 0.0);
            assertEquals(0.0, pmf.evaluateAsDouble(b + 1), 0.0);
            double expected = 1.0 / (b - a + 1);
            for (int x = a; x <= b; x++)
            {
                assertEquals(expected, pmf.evaluate(x), 0.0);
                assertEquals(expected, pmf.evaluateAsDouble(x), 0.0);
                assertEquals(expected, pmf.evaluate(x + RANDOM.nextDouble() * Math.signum(x)), 0.0);
            }
        }
    }

    @Override
    public void testKnownConvertToVector()
    {
        this.testConvertToVector();
        this.testConvertFromVector();
    }

    @Override
    public void testDistributionConstructors()
    {
        int minSupport = 0;
        int maxSupport = 0;
        UniformIntegerDistribution instance = new UniformIntegerDistribution();
        assertEquals(minSupport, (int) instance.getMinSupport());
        assertEquals(maxSupport, (int) instance.getMaxSupport());
        
        minSupport = -10;
        maxSupport = 33;
        instance = new UniformIntegerDistribution(minSupport, maxSupport);
        
        instance = new UniformIntegerDistribution(instance);
        assertEquals(minSupport, (int) instance.getMinSupport());
        assertEquals(maxSupport, (int) instance.getMaxSupport());
    }
    
    @Override
    public void testPMFConstructors()
    {
        int minSupport = 0;
        int maxSupport = 0;
        UniformIntegerDistribution.PMF instance = new UniformIntegerDistribution.PMF();
        assertEquals(minSupport, (int) instance.getMinSupport());
        assertEquals(maxSupport, (int) instance.getMaxSupport());
        
        minSupport = RANDOM.nextInt(20);
        maxSupport = minSupport + RANDOM.nextInt(20);
        instance = new UniformIntegerDistribution.PMF(minSupport, maxSupport);
        
        instance = new UniformIntegerDistribution.PMF(instance);
        assertEquals(minSupport, (int) instance.getMinSupport());
        assertEquals(maxSupport, (int) instance.getMaxSupport());
    }


    @Override
    public void testCDFConstructors()
    {
        int minSupport = 0;
        int maxSupport = 0;
        UniformIntegerDistribution.CDF instance = new UniformIntegerDistribution.CDF();
        assertEquals(minSupport, (int) instance.getMinSupport());
        assertEquals(maxSupport, (int) instance.getMaxSupport());
        
        minSupport = RANDOM.nextInt(20);
        maxSupport = minSupport + RANDOM.nextInt(20);
        instance = new UniformIntegerDistribution.CDF(minSupport, maxSupport);
        
        instance = new UniformIntegerDistribution.CDF(instance);
        assertEquals(minSupport, (int) instance.getMinSupport());
        assertEquals(maxSupport, (int) instance.getMaxSupport());
    }

    @Override
    public void testCDFKnownValues()
    {
        for (int i = 0; i < 100; i++)
        {
            UniformIntegerDistribution.CDF cdf = 
                this.createInstance().getCDF();
            int a = cdf.getMinSupport();
            int b = cdf.getMaxSupport();
            
            assertEquals(0.0, cdf.evaluateAsDouble(a - 1), 0.0);
            assertEquals(1.0, cdf.evaluateAsDouble(b + 1), 0.0);
            double expected = 1.0 / (b - a + 1);
            double sum = expected;
            for (int x = a; x <= b; x++)
            {
                assertEquals(sum, cdf.evaluate(x), TOLERANCE);
                assertEquals(sum, cdf.evaluateAsDouble(x), TOLERANCE);
                assertEquals(sum, cdf.evaluate(x + RANDOM.nextDouble() * Math.signum(x)), TOLERANCE);
                sum += expected;
            }
        }
    }
    
    /**
     * Test of clone method, of class UniformIntegerDistribution.
     */
    public void testClone()
    {
        UniformIntegerDistribution instance = this.createInstance();
        UniformIntegerDistribution clone = instance.clone();
        assertEquals(instance.getMinSupport(), clone.getMinSupport());
        assertEquals(instance.getMaxSupport(), clone.getMaxSupport());
        assertNotSame(instance, clone);
        assertNotSame(clone, instance.clone());
    }

    /**
     * Test of getProbabilityFunction method, of class UniformIntegerDistribution.
     */
    public void testGetProbabilityFunction()
    {
        UniformIntegerDistribution instance = this.createInstance();
        UniformIntegerDistribution.PMF result = instance.getProbabilityFunction();
        
        assertEquals(instance.getMinSupport(), result.getMinSupport());
        assertEquals(instance.getMaxSupport(), result.getMaxSupport());
    }

    /**
     * Test of getCDF method, of class UniformIntegerDistribution.
     */
    public void testGetCDF()
    {
        UniformIntegerDistribution instance = this.createInstance();
        UniformIntegerDistribution.CDF result = instance.getCDF();
        assertEquals(instance.getMinSupport(), result.getMinSupport());
        assertEquals(instance.getMaxSupport(), result.getMaxSupport());
    }

    /**
     * Test of getMean method, of class UniformIntegerDistribution.
     */
    public void testGetMean()
    {
        UniformIntegerDistribution instance = new UniformIntegerDistribution();
        assertEquals(0.0, (double) instance.getMean(), 0.0);
        
        instance.setMinSupport(5);
        instance.setMaxSupport(9);
        assertEquals(7.0, (double) instance.getMean());
        
        instance.setMaxSupport(6);
        assertEquals(5.5, (double) instance.getMean());
    }

    /**
     * Test of convertToVector method, of class UniformIntegerDistribution.
     */
    public void testConvertToVector()
    {
        UniformIntegerDistribution instance = new UniformIntegerDistribution();
        assertEquals(new Vector2(), instance.convertToVector());
        
        instance = new UniformIntegerDistribution(
            9, 12);
        assertEquals(new Vector2(9, 12), instance.convertToVector());
    }

    /**
     * Test of convertFromVector method, of class UniformIntegerDistribution.
     */
    public void testConvertFromVector()
    {
        UniformIntegerDistribution base = this.createInstance();
        Vector parameters = base.convertToVector();
        
        UniformIntegerDistribution instance = new UniformIntegerDistribution();
        instance.convertFromVector(parameters);
        assertEquals(base.getMinSupport(), instance.getMinSupport());
        assertEquals(base.getMaxSupport(), instance.getMaxSupport());
    }

    /**
     * Test of getMinSupport method, of class UniformIntegerDistribution.
     */
    public void testGetMinSupport()
    {
        this.testSetMinSupport();
    }

    /**
     * Test of setMinSupport method, of class UniformIntegerDistribution.
     */
    public void testSetMinSupport()
    {
        int minSupport = 0;
        UniformIntegerDistribution instance = new UniformIntegerDistribution();
        assertEquals(minSupport, (int) instance.getMinSupport());

        minSupport = 8;
        instance.setMinSupport(minSupport);
        assertEquals(minSupport, (int) instance.getMinSupport());
    }

    /**
     * Test of getMaxSupport method, of class UniformIntegerDistribution.
     */
    
    public void testGetMaxSupport()
    {
        this.testSetMaxSupport();
    }

    /**
     * Test of setMaxSupport method, of class UniformIntegerDistribution.
     */
    public void testSetMaxSupport()
    {
        int maxSupport = 0;
        UniformIntegerDistribution instance = new UniformIntegerDistribution();
        assertEquals(maxSupport, (int) instance.getMaxSupport());
        
        maxSupport = 12;
        instance.setMaxSupport(maxSupport);
        assertEquals(maxSupport, (int) instance.getMaxSupport());
    }

    /**
     * Test of getMeanAsDouble method, of class UniformIntegerDistribution.
     */
    public void testGetMeanAsDouble()
    {
        UniformIntegerDistribution instance = new UniformIntegerDistribution();
        assertEquals(0.0, instance.getMeanAsDouble(), 0.0);
        
        instance.setMinSupport(5);
        instance.setMaxSupport(9);
        assertEquals(7.0, instance.getMeanAsDouble());
        
        instance.setMaxSupport(6);
        assertEquals(5.5, instance.getMeanAsDouble());
    }
    
    /**
     * Test of getEstimator method, of class UniformIntegerDistribution.
     */
    public void testGetEstimator()
    {
        UniformIntegerDistribution instance = new UniformIntegerDistribution();
        assertNotNull(instance.getEstimator());
    }

    @Override
    public void testCDFConvertFromVector()
    {
        UniformIntegerDistribution cdf = this.createInstance();
        Vector x1 = cdf.convertToVector();
        assertNotNull(x1);
        int d = x1.getDimensionality();
        
        Vector y1 = new Vector2(RANDOM.nextInt(10), 10 + RANDOM.nextInt(20));
        cdf.convertFromVector(y1);
        Vector y2 = cdf.convertToVector();
        assertNotSame(y1, y2);
        assertEquals(y1, y2);
        
        cdf.convertFromVector(x1);
        Vector x2 = cdf.convertToVector();
        assertNotSame(x1, x2);
        assertEquals(x1, x2);
        
        boolean exceptionThrown = false;
        try
        {
            cdf.convertFromVector(null);
        }
        catch (Exception e)
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
            cdf.convertFromVector(new Vector3());
        }
        catch (Exception e)
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
            cdf.convertFromVector(new Vector1());
        }
        catch (Exception e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }
    
    public void testMaximumLikelihoodEstimator()
    {
        UniformIntegerDistribution.MaximumLikelihoodEstimator learner =
            new UniformIntegerDistribution.MaximumLikelihoodEstimator();
        this.distributionEstimatorTest(learner);
    }
}
