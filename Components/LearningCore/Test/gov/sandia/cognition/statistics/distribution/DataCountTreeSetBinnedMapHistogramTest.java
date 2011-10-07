/*
 * File:                DataCountTreeSetBinnedMapHistogramTest.java
 * Authors:             Zachary Benz
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 27, 2007, Sandia Corporation.  Under the terms of Contract 
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by 
 * or on behalf of the U.S. Government. Export of this program may require a 
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.CodeReview;

/**
 * Unit tests for class DataCountTreeSetBinnedMapHistogram.
 *
 * @author  Zachary Benz
 * @since   2.0
 */
@CodeReview(
    reviewer = "Justin Basilico",
    date = "2009-05-29",
    changesNeeded = false,
    comments = "Cleaned up the formatting and javadoc."
)
public class DataCountTreeSetBinnedMapHistogramTest
    extends MapBasedDataHistogramTest
{

    /**
     * Creates a new test.
     *
     * @param   testName
     *      The test name.
     */
    public DataCountTreeSetBinnedMapHistogramTest(
        final String testName)
    {
        super(testName);
    }

    /**
     * Test of add method, of class DataCountTreeSetBinnedMapHistogram.
     */
    @Override
    public void testAdd()
    {
        System.out.println("add");

        DataCountTreeSetBinnedMapHistogram<String> instance =
            new DataCountTreeSetBinnedMapHistogram<String>("q", "c", "b", "j");
        assertEquals(0.0, instance.getTotal());
        assertEquals(0.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(0.0, instance.get("j"));
        assertEquals(0, instance.getDomain().size());
        assertFalse(instance.getDomain().contains("b"));

        instance.increment("a");
        assertEquals(0.0, instance.getTotal());
        assertEquals(0.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(0.0, instance.get("j"));
        assertEquals(0, instance.getDomain().size());
        assertFalse(instance.getDomain().contains("b"));

        instance.increment("b");
        assertEquals(1.0, instance.getTotal());
        assertEquals(1.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(0.0, instance.get("j"));
        assertEquals(1, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));

        instance.increment("b");
        assertEquals(2.0, instance.getTotal());
        assertEquals(2.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(0.0, instance.get("j"));
        assertEquals(1, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));

        instance.increment("c");
        assertEquals(3.0, instance.getTotal());
        assertEquals(2.0, instance.get("b"));
        assertEquals(1.0, instance.get("c"));
        assertEquals(0.0, instance.get("j"));
        assertEquals(2, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));

        instance.increment("b");
        assertEquals(4.0, instance.getTotal());
        assertEquals(3.0, instance.get("b"));
        assertEquals(1.0, instance.get("c"));
        assertEquals(0.0, instance.get("j"));
        assertEquals(2, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));

        instance.increment("d", 7);
        assertEquals(11.0, instance.getTotal());
        assertEquals(3.0, instance.get("b"));
        assertEquals(8.0, instance.get("c"));
        assertEquals(0.0, instance.get("j"));
        assertEquals(2, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));

        instance.increment("m", 3);
        assertEquals(14.0, instance.getTotal());
        assertEquals(3.0, instance.get("b"));
        assertEquals(8.0, instance.get("c"));
        assertEquals(3.0, instance.get("j"));
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));
        assertTrue(instance.getDomain().contains("j"));

        instance.increment("j", 14);
        assertEquals(28.0, instance.getTotal());
        assertEquals(3.0, instance.get("b"));
        assertEquals(8.0, instance.get("c"));
        assertEquals(17.0, instance.get("j"));
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));
        assertTrue(instance.getDomain().contains("j"));

        instance.increment("z", 123);
        assertEquals(28.0, instance.getTotal());
        assertEquals(3.0, instance.get("b"));
        assertEquals(8.0, instance.get("c"));
        assertEquals(17.0, instance.get("j"));
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));
        assertTrue(instance.getDomain().contains("j"));
        assertFalse(instance.getDomain().contains("z"));
    }

    /**
     * Test of remove method, of class DataCountTreeSetBinnedMapHistogram.
     */
    @Override
    public void testRemove()
    {
        System.out.println("remove");

        DataCountTreeSetBinnedMapHistogram<String> instance =
            new DataCountTreeSetBinnedMapHistogram<String>("c", "j", "b", "q");
        assertEquals(0.0, instance.getTotal());
        assertEquals(0.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(0.0, instance.get("j"));
        assertEquals(0, instance.getDomain().size());

        instance.decrement("a");
        instance.decrement("d", 7);

        assertEquals(0.0, instance.getTotal());
        assertEquals(0.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(0.0, instance.get("j"));
        assertEquals(0, instance.getDomain().size());

        instance.increment("b");
        instance.increment("b");
        instance.increment("f");
        instance.increment("b");
        instance.increment("m", 7);
        instance.increment("c");
        assertEquals(12.0, instance.getTotal());
        assertEquals(3.0, instance.get("b"));
        assertEquals(2.0, instance.get("c"));
        assertEquals(7.0, instance.get("j"));
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));
        assertTrue(instance.getDomain().contains("j"));

        instance.decrement("e");
        assertEquals(11.0, instance.getTotal());
        assertEquals(3.0, instance.get("b"));
        assertEquals(1.0, instance.get("c"));
        assertEquals(7.0, instance.get("j"));
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));
        assertTrue(instance.getDomain().contains("j"));

        instance.decrement("c");
        assertEquals(10.0, instance.getTotal());
        assertEquals(3.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(7.0, instance.get("j"));
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));
        assertTrue(instance.getDomain().contains("j"));

        instance.decrement("h");
        assertEquals(10.0, instance.getTotal());
        assertEquals(3.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(7.0, instance.get("j"));
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));
        assertTrue(instance.getDomain().contains("j"));

        instance.decrement("j", 4);
        assertEquals(6.0, instance.getTotal());
        assertEquals(3.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(3.0, instance.get("j"));
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));
        assertTrue(instance.getDomain().contains("j"));

        instance.decrement("b", 100);
        assertEquals(3.0, instance.getTotal());
        assertEquals(0.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(3.0, instance.get("j"));
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));
        assertTrue(instance.getDomain().contains("j"));
        instance.compact();
        assertEquals(1, instance.getDomain().size());
        assertFalse(instance.getDomain().contains("b"));
        assertFalse(instance.getDomain().contains("c"));
        assertTrue(instance.getDomain().contains("j"));


        instance.decrement("z", 123123);
        assertEquals(3.0, instance.getTotal());
        assertEquals(0.0, instance.get("b"));
        assertEquals(0.0, instance.get("c"));
        assertEquals(3.0, instance.get("j"));
        assertEquals(1, instance.getDomain().size());
        assertFalse(instance.getDomain().contains("b"));
        assertFalse(instance.getDomain().contains("c"));
        assertTrue(instance.getDomain().contains("j"));

    }

    /**
     * Test of getNumBins method, of class DataCountTreeSetBinnedMapHistogram.
     */
    public void testGetBinCount()
    {
        System.out.println("getNumBins");

        DataCountTreeSetBinnedMapHistogram<String> instance =
            new DataCountTreeSetBinnedMapHistogram<String>("c", "j", "b", "q");
        assertEquals(3, instance.getBinCount());
    }

}
