/*
 * File:                AbstractConfusionMatrixTest.java
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

import gov.sandia.cognition.learning.performance.categorization.AbstractConfusionMatrix;
import gov.sandia.cognition.learning.performance.categorization.DefaultConfusionMatrix;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class AbstractConfusionMatrix.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class AbstractConfusionMatrixTest
{
    public static final double EPSILON = 1e-10;

    /**
     * Creates a new test.
     */
    public AbstractConfusionMatrixTest()
    {
    }

    protected AbstractConfusionMatrix<String> createEmptyInstance()
    {
        return new DefaultConfusionMatrix<String>();
    }

    protected AbstractConfusionMatrix<String> createExampleInstance()
    {
        // Based on the example here: http://www.colorado.edu/geography/gcraft/notes/manerror/html/kappa.html
        AbstractConfusionMatrix<String> instance = this.createEmptyInstance();
        instance.add("a", "a", 2);
        instance.add("a", "c", 2);
        instance.add("b", "b", 1);
        instance.add("c", "a", 1);
        instance.add("c", "c", 1);
        instance.add("d", "d", 2);
        instance.add("d", "e", 1);
        instance.add("e", "e", 6);
        return instance;
    }

    /**
     * Test of add method, of class AbstractConfusionMatrix.
     */
    @Test
    public void testAdd()
    {
        AbstractConfusionMatrix<String> instance = this.createEmptyInstance();
        assertEquals(0, instance.getTotalCount(), 0.0);
        assertEquals(0, instance.getCount("a", "a"), 0.0);
        assertEquals(0, instance.getCount("a", "b"), 0.0);
        assertEquals(0, instance.getCount("b", "a"), 0.0);
        assertEquals(0, instance.getCount("b", "b"), 0.0);

        instance.add("a", "b");
        assertEquals(1, instance.getTotalCount(), 0.0);
        assertEquals(0, instance.getCount("a", "a"), 0.0);
        assertEquals(1, instance.getCount("a", "b"), 0.0);
        assertEquals(0, instance.getCount("b", "a"), 0.0);
        assertEquals(0, instance.getCount("b", "b"), 0.0);

        instance.add("b", "b");
        assertEquals(2, instance.getTotalCount(), 0.0);
        assertEquals(0, instance.getCount("a", "a"), 0.0);
        assertEquals(1, instance.getCount("a", "b"), 0.0);
        assertEquals(0, instance.getCount("b", "a"), 0.0);
        assertEquals(1, instance.getCount("b", "b"), 0.0);

        instance.add("b", "a", 3);
        assertEquals(5, instance.getTotalCount(), 0.0);
        assertEquals(0, instance.getCount("a", "a"), 0.0);
        assertEquals(1, instance.getCount("a", "b"), 0.0);
        assertEquals(3, instance.getCount("b", "a"), 0.0);
        assertEquals(1, instance.getCount("b", "b"), 0.0);
        instance.add("b", "b", 4);

        assertEquals(9, instance.getTotalCount(), 0.0);
        assertEquals(0, instance.getCount("a", "a"), 0.0);
        assertEquals(1, instance.getCount("a", "b"), 0.0);
        assertEquals(3, instance.getCount("b", "a"), 0.0);
        assertEquals(5, instance.getCount("b", "b"), 0.0);
    }

    /**
     * Test of addAll method, of class AbstractConfusionMatrix.
     */
    @Test
    public void testAddAll()
    {
        AbstractConfusionMatrix<String> instance = this.createEmptyInstance();
        assertTrue(instance.isEmpty());
        instance.addAll(this.createEmptyInstance());
        assertTrue(instance.isEmpty());

        assertEquals(0, instance.getTotalCount(), 0.0);
        assertEquals(0, instance.getCount("a", "a"), 0.0);
        assertEquals(0, instance.getCount("a", "b"), 0.0);
        assertEquals(0, instance.getCount("b", "a"), 0.0);
        assertEquals(0, instance.getCount("b", "b"), 0.0);

        AbstractConfusionMatrix<String> other = this.createEmptyInstance();
        other.add("a", "b");
        instance.addAll(other);
        assertEquals(1, instance.getTotalCount(), 0.0);
        assertEquals(0, instance.getCount("a", "a"), 0.0);
        assertEquals(1, instance.getCount("a", "b"), 0.0);
        assertEquals(0, instance.getCount("b", "a"), 0.0);
        assertEquals(0, instance.getCount("b", "b"), 0.0);

        other.clear();
        other.add("b", "b");
        other.add("b", "a", 3);
        other.add("b", "b", 4);
        instance.addAll(other);
        assertEquals(9, instance.getTotalCount(), 0.0);
        assertEquals(0, instance.getCount("a", "a"), 0.0);
        assertEquals(1, instance.getCount("a", "b"), 0.0);
        assertEquals(3, instance.getCount("b", "a"), 0.0);
        assertEquals(5, instance.getCount("b", "b"), 0.0);


        instance = this.createExampleInstance();
        instance.addAll(this.createEmptyInstance());
        assertEquals(16.0, instance.getTotalCount(), 0.0);
        
        instance.addAll(instance);
        assertEquals(32.0, instance.getTotalCount(), 0.0);

        instance.addAll(this.createExampleInstance());
        assertEquals(48.0, instance.getTotalCount(), 0.0);
    }

    /**
     * Test of isEmpty method, of class AbstractConfusionMatrix.
     */
    @Test
    public void testIsEmpty()
    {
        AbstractConfusionMatrix<String> instance = this.createEmptyInstance();
        assertTrue(instance.isEmpty());

        instance.add("a", "b");
        assertFalse(instance.isEmpty());

        instance.clear();
        assertTrue(instance.isEmpty());

        instance = this.createExampleInstance();
        assertFalse(instance.isEmpty());
        
        instance.clear();
        assertTrue(instance.isEmpty());
    }

    /**
     * Test of getTotalCount method, of class AbstractConfusionMatrix.
     */
    @Test
    public void testGetTotalCount()
    {
        AbstractConfusionMatrix<String> instance = this.createEmptyInstance();
        assertEquals(0.0, instance.getTotalCount(), 0.0);

        instance = this.createExampleInstance();
        assertEquals(16.0, instance.getTotalCount(), 0.0);
    }

    /**
     * Test of getTotalCorrectCount method, of class AbstractConfusionMatrix.
     */
    @Test
    public void testGetTotalCorrectCount()
    {
        AbstractConfusionMatrix<String> instance = this.createEmptyInstance();
        assertEquals(0.0, instance.getTotalCorrectCount(), 0.0);

        instance = this.createExampleInstance();
        assertEquals(12.0, instance.getTotalCorrectCount(), 0.0);
    }

    /**
     * Test of getTotalIncorrectCount method, of class AbstractConfusionMatrix.
     */
    @Test
    public void testGetTotalIncorrectCount()
    {
        AbstractConfusionMatrix<String> instance = this.createEmptyInstance();
        assertEquals(0.0, instance.getTotalIncorrectCount(), 0.0);

        instance = this.createExampleInstance();
        assertEquals(4.0, instance.getTotalIncorrectCount(), 0.0);
    }

    /**
     * Test of getActualCount method, of class AbstractConfusionMatrix.
     */
    @Test
    public void testGetActualCount()
    {
        AbstractConfusionMatrix<String> instance = this.createEmptyInstance();
        assertEquals(0.0, instance.getActualCount("a"), 0.0);
        assertEquals(0.0, instance.getActualCount("b"), 0.0);
        assertEquals(0.0, instance.getActualCount("c"), 0.0);
        assertEquals(0.0, instance.getActualCount("d"), 0.0);
        assertEquals(0.0, instance.getActualCount("e"), 0.0);
        assertEquals(0.0, instance.getActualCount("f"), 0.0);


        instance = this.createExampleInstance();
        assertEquals(4.0, instance.getActualCount("a"), 0.0);
        assertEquals(1.0, instance.getActualCount("b"), 0.0);
        assertEquals(2.0, instance.getActualCount("c"), 0.0);
        assertEquals(3.0, instance.getActualCount("d"), 0.0);
        assertEquals(6.0, instance.getActualCount("e"), 0.0);
        assertEquals(0.0, instance.getActualCount("f"), 0.0);
    }

    /**
     * Test of getPredictedCount method, of class AbstractConfusionMatrix.
     */
    @Test
    public void testGetPredictedCount()
    {
        AbstractConfusionMatrix<String> instance = this.createEmptyInstance();
        assertEquals(0, instance.getPredictedCount("a"), 0.0);
        assertEquals(0, instance.getPredictedCount("b"), 0.0);
        assertEquals(0, instance.getPredictedCount("c"), 0.0);
        assertEquals(0, instance.getPredictedCount("d"), 0.0);
        assertEquals(0, instance.getPredictedCount("e"), 0.0);
        assertEquals(0, instance.getPredictedCount("f"), 0.0);

        instance = this.createExampleInstance();
        assertEquals(3, instance.getPredictedCount("a"), 0.0);
        assertEquals(1, instance.getPredictedCount("b"), 0.0);
        assertEquals(3, instance.getPredictedCount("c"), 0.0);
        assertEquals(2, instance.getPredictedCount("d"), 0.0);
        assertEquals(7, instance.getPredictedCount("e"), 0.0);
        assertEquals(0, instance.getPredictedCount("f"), 0.0);
    }

    /**
     * Test of getAccuracy method, of class AbstractConfusionMatrix.
     */
    @Test
    public void testGetAccuracy()
    {
        AbstractConfusionMatrix<String> instance = this.createEmptyInstance();
        assertTrue(Double.isNaN(instance.getAccuracy()));

        instance = this.createExampleInstance();
        assertEquals(0.75, instance.getAccuracy(), 0.0);
    }

    /**
     * Test of getCategoryAccuracy method, of class AbstractConfusionMatrix.
     */
    @Test
    public void testGetCategoryAccuracy()
    {
        AbstractConfusionMatrix<String> instance = this.createEmptyInstance();
        assertTrue(Double.isNaN(instance.getCategoryAccuracy("a")));
        assertTrue(Double.isNaN(instance.getCategoryAccuracy("b")));
        assertTrue(Double.isNaN(instance.getCategoryAccuracy("c")));
        assertTrue(Double.isNaN(instance.getCategoryAccuracy("d")));
        assertTrue(Double.isNaN(instance.getCategoryAccuracy("e")));
        assertTrue(Double.isNaN(instance.getCategoryAccuracy("f")));

        instance = this.createExampleInstance();
        assertEquals(2.0 / 4.0, instance.getCategoryAccuracy("a"), 0.0);
        assertEquals(1.0 / 1.0, instance.getCategoryAccuracy("b"), 0.0);
        assertEquals(1.0 / 2.0, instance.getCategoryAccuracy("c"), 0.0);
        assertEquals(2.0 / 3.0, instance.getCategoryAccuracy("d"), 0.0);
        assertEquals(6.0 / 6.0, instance.getCategoryAccuracy("e"), 0.0);
        assertTrue(Double.isNaN(instance.getCategoryAccuracy("f")));
    }

    /**
     * Test of getAverageCategoryAccuracy method, of class AbstractConfusionMatrix.
     */
    @Test
    public void testGetAverageCategoryAccuracy()
    {
        AbstractConfusionMatrix<String> instance = this.createEmptyInstance();
        assertTrue(Double.isNaN(instance.getAverageCategoryAccuracy()));

        instance = this.createExampleInstance();
        assertEquals((2.0 / 4.0 + 1.0 / 1.0 + 1.0 / 2.0 + 2.0 / 3.0 + 6.0 / 6.0) / 5.0,
            instance.getAverageCategoryAccuracy(), 0.0);
    }

    /**
     * Test of getErrorRate method, of class AbstractConfusionMatrix.
     */
    @Test
    public void testGetErrorRate()
    {
        AbstractConfusionMatrix<String> instance = this.createEmptyInstance();
        assertTrue(Double.isNaN(instance.getErrorRate()));

        instance = this.createExampleInstance();
        assertEquals(0.25, instance.getErrorRate(), 0.0);
    }

    /**
     * Test of getCategoryErrorRate method, of class AbstractConfusionMatrix.
     */
    @Test
    public void testGetCategoryErrorRate()
    {
        AbstractConfusionMatrix<String> instance = this.createEmptyInstance();
        assertTrue(Double.isNaN(instance.getCategoryErrorRate("a")));
        assertTrue(Double.isNaN(instance.getCategoryErrorRate("b")));
        assertTrue(Double.isNaN(instance.getCategoryErrorRate("c")));
        assertTrue(Double.isNaN(instance.getCategoryErrorRate("d")));
        assertTrue(Double.isNaN(instance.getCategoryErrorRate("e")));
        assertTrue(Double.isNaN(instance.getCategoryErrorRate("f")));

        instance = this.createExampleInstance();
        assertEquals(2.0 / 4.0, instance.getCategoryErrorRate("a"), EPSILON);
        assertEquals(0.0 / 1.0, instance.getCategoryErrorRate("b"), EPSILON);
        assertEquals(1.0 / 2.0, instance.getCategoryErrorRate("c"), EPSILON);
        assertEquals(1.0 / 3.0, instance.getCategoryErrorRate("d"), EPSILON);
        assertEquals(0.0 / 6.0, instance.getCategoryErrorRate("e"), EPSILON);
        assertTrue(Double.isNaN(instance.getCategoryErrorRate("f")));
    }

    /**
     * Test of getAverageCategoryErrorRate method, of class AbstractConfusionMatrix.
     */
    @Test
    public void testGetAverageCategoryErrorRate()
    {
        AbstractConfusionMatrix<String> instance = this.createEmptyInstance();
        assertTrue(Double.isNaN(instance.getAverageCategoryErrorRate()));

        instance = this.createExampleInstance();
        assertEquals((2.0 / 4.0 + 0.0 / 1.0 + 1.0 / 2.0 + 1.0 / 3.0 + 0.0 / 6.0) / 5.0,
            instance.getAverageCategoryErrorRate(), EPSILON);
    }

}