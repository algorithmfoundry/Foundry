/*
 * File:                AbstractBinaryConfusionMatrixTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright February 19, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.performance.categorization;

import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class AbstractBinaryConfusionMatrix.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class AbstractBinaryConfusionMatrixTest
{
    public static final double EPSILON = 1e-10;

    protected Random random = new Random(211);
    
    /**
     * Creates a new test.
     */
    public AbstractBinaryConfusionMatrixTest()
    {
    }

    protected AbstractBinaryConfusionMatrix createEmptyInstance()
    {
        return new DefaultBinaryConfusionMatrix();
    }

    private AbstractBinaryConfusionMatrix createExampleInstance()
    {
        AbstractBinaryConfusionMatrix instance = this.createEmptyInstance();
        instance.add(true, true, 4);
        instance.add(true, false, 3);
        instance.add(false, true, 2);
        instance.add(false, false, 1);
        return instance;
    }

    /**
     * Test of getCategories method, of class AbstractBinaryConfusionMatrix.
     */
    @Test
    public void testGetCategories()
    {
        AbstractBinaryConfusionMatrix instance = this.createExampleInstance();
        assertEquals(2, instance.getCategories().size());
        assertTrue(instance.getCategories().contains(true));
        assertTrue(instance.getCategories().contains(false));
    }

    /**
     * Test of getActualCategories method, of class AbstractBinaryConfusionMatrix.
     */
    @Test
    public void testGetActualCategories()
    {
        AbstractBinaryConfusionMatrix instance = this.createExampleInstance();
        assertEquals(2, instance.getActualCategories().size());
        assertTrue(instance.getActualCategories().contains(true));
        assertTrue(instance.getActualCategories().contains(false));
    }

    /**
     * Test of getPredictedCategories method, of class AbstractBinaryConfusionMatrix.
     */
    @Test
    public void testGetPredictedCategories()
    {
        AbstractBinaryConfusionMatrix instance = this.createExampleInstance();
        assertEquals(2, instance.getPredictedCategories().size());
        assertTrue(instance.getPredictedCategories().contains(true));
        assertTrue(instance.getPredictedCategories().contains(false));
        
        assertEquals(2, instance.getPredictedCategories(true).size());
        assertTrue(instance.getPredictedCategories(true).contains(true));
        assertTrue(instance.getPredictedCategories(true).contains(false));

        assertEquals(2, instance.getPredictedCategories(false).size());
        assertTrue(instance.getPredictedCategories(false).contains(true));
        assertTrue(instance.getPredictedCategories(false).contains(false));
    }

    /**
     * Test of getCount method, of class AbstractBinaryConfusionMatrix.
     */
    @Test
    public void testGetCount()
    {
        AbstractBinaryConfusionMatrix instance = this.createEmptyInstance();
        assertEquals(0.0, instance.getCount(true, true), 0.0);
        assertEquals(0.0, instance.getCount(true, false), 0.0);
        assertEquals(0.0, instance.getCount(false, true), 0.0);
        assertEquals(0.0, instance.getCount(false, false), 0.0);

        instance = this.createExampleInstance();
        assertEquals(instance.getTruePositivesCount(), instance.getCount(true, true), 0.0);
        assertEquals(instance.getFalseNegativesCount(), instance.getCount(true, false), 0.0);
        assertEquals(instance.getFalsePositivesCount(), instance.getCount(false, true), 0.0);
        assertEquals(instance.getTrueNegativesCount(), instance.getCount(false, false), 0.0);
    }

    /**
     * Test of getActualCount method, of class AbstractBinaryConfusionMatrix.
     */
    @Test
    public void testGetActualCount()
    {
        AbstractBinaryConfusionMatrix instance = this.createEmptyInstance();
        assertEquals(0.0, instance.getActualCount(true), 0.0);
        assertEquals(0.0, instance.getActualCount(false), 0.0);

        instance = this.createExampleInstance();
        assertEquals(7.0, instance.getActualCount(true), EPSILON);
        assertEquals(3.0, instance.getActualCount(false), EPSILON);
    }

    /**
     * Test of getTruePositivesFraction method, of class AbstractBinaryConfusionMatrix.
     */
    @Test
    public void testGetTruePositivesFraction()
    {
        AbstractBinaryConfusionMatrix instance = this.createEmptyInstance();
        assertTrue(Double.isNaN(instance.getTruePositivesFraction()));

        instance = this.createExampleInstance();
        assertEquals(4.0 / 10.0, instance.getTruePositivesFraction(), EPSILON);
    }

    /**
     * Test of getFalsePositivesFraction method, of class AbstractBinaryConfusionMatrix.
     */
    @Test
    public void testGetFalsePositivesFraction()
    {
        AbstractBinaryConfusionMatrix instance = this.createEmptyInstance();
        assertTrue(Double.isNaN(instance.getFalsePositivesFraction()));

        instance = this.createExampleInstance();
        assertEquals(2.0 / 10.0, instance.getFalsePositivesFraction(), EPSILON);
    }

    /**
     * Test of getTrueNegativesFraction method, of class AbstractBinaryConfusionMatrix.
     */
    @Test
    public void testGetTrueNegativesFraction()
    {
        AbstractBinaryConfusionMatrix instance = this.createEmptyInstance();
        assertTrue(Double.isNaN(instance.getTrueNegativesFraction()));

        instance = this.createExampleInstance();
        assertEquals(1.0 / 10.0, instance.getTrueNegativesFraction(), EPSILON);
    }

    /**
     * Test of getFalseNegativesFraction method, of class AbstractBinaryConfusionMatrix.
     */
    @Test
    public void testGetFalseNegativesFraction()
    {
        AbstractBinaryConfusionMatrix instance = this.createEmptyInstance();
        assertTrue(Double.isNaN(instance.getFalseNegativesFraction()));

        instance = this.createExampleInstance();
        assertEquals(3.0 / 10.0, instance.getFalseNegativesFraction(), EPSILON);
    }

    /**
     * Test of getSensitivity method, of class AbstractBinaryConfusionMatrix.
     */
    @Test
    public void testGetSensitivity()
    {
        AbstractBinaryConfusionMatrix instance = this.createEmptyInstance();
        assertTrue(Double.isNaN(instance.getSensitivity()));

        instance = this.createExampleInstance();
        assertEquals(4.0 / 7.0, instance.getSensitivity(), EPSILON);
    }

    /**
     * Test of getSpecificity method, of class AbstractBinaryConfusionMatrix.
     */
    @Test
    public void testGetSpecificity()
    {
        AbstractBinaryConfusionMatrix instance = this.createEmptyInstance();
        assertTrue(Double.isNaN(instance.getSpecificity()));

        instance = this.createExampleInstance();
        assertEquals(1.0 / 3.0, instance.getSpecificity(), EPSILON);
    }

    /**
     * Test of getPrecision method, of class AbstractBinaryConfusionMatrix.
     */
    @Test
    public void testGetPrecision()
    {
        AbstractBinaryConfusionMatrix instance = this.createEmptyInstance();
        assertTrue(Double.isNaN(instance.getPrecision()));

        instance = this.createExampleInstance();
        assertEquals(4.0 / 6.0, instance.getPrecision(), EPSILON);
    }

    /**
     * Test of getRecall method, of class AbstractBinaryConfusionMatrix.
     */
    @Test
    public void testGetRecall()
    {
        AbstractBinaryConfusionMatrix instance = this.createEmptyInstance();
        assertTrue(Double.isNaN(instance.getRecall()));

        instance = this.createExampleInstance();
        assertEquals(4.0 / 7.0, instance.getRecall(), EPSILON);
    }

    /**
     * Test of getFScore method, of class AbstractBinaryConfusionMatrix.
     */
    @Test
    public void testGetFScore()
    {
        AbstractBinaryConfusionMatrix instance = this.createEmptyInstance();
        assertTrue(Double.isNaN(instance.getFScore()));
        assertTrue(Double.isNaN(instance.getFScore(0.2)));

        instance = this.createExampleInstance();
        double precision = 4.0 / 6.0;
        double recall = 4.0 / 7.0;
        assertEquals(2.0 * (precision * recall) / (precision + recall),
            instance.getFScore(), EPSILON);
        assertEquals(instance.getFScore(), instance.getFScore(1.0), EPSILON);

        double beta = 2 * random.nextDouble();
        assertEquals((1.0 + beta * beta) * (precision * recall)
            / (beta * beta * precision + recall),
            instance.getFScore(beta), EPSILON);

        double[] badValues = { -1.0, -0.1, -random.nextDouble() * 10.0 };
        for (double badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.getFScore(badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                assertTrue(exceptionThrown);
            }
        }
    }

}