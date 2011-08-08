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
        assertEquals(0, instance.getTotalCount());
        assertEquals(0, instance.getCount("b"));
        assertEquals(0, instance.getCount("c"));
        assertEquals(0, instance.getCount("j"));
        assertEquals(0, instance.getDomain().size());
        assertFalse(instance.getDomain().contains("b"));

        instance.add("a");
        assertEquals(0, instance.getTotalCount());
        assertEquals(0, instance.getCount("b"));
        assertEquals(0, instance.getCount("c"));
        assertEquals(0, instance.getCount("j"));
        assertEquals(0, instance.getDomain().size());
        assertFalse(instance.getDomain().contains("b"));

        instance.add("b");
        assertEquals(1, instance.getTotalCount());
        assertEquals(1, instance.getCount("b"));
        assertEquals(0, instance.getCount("c"));
        assertEquals(0, instance.getCount("j"));
        assertEquals(1, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));

        instance.add("b");
        assertEquals(2, instance.getTotalCount());
        assertEquals(2, instance.getCount("b"));
        assertEquals(0, instance.getCount("c"));
        assertEquals(0, instance.getCount("j"));
        assertEquals(1, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));

        instance.add("c");
        assertEquals(3, instance.getTotalCount());
        assertEquals(2, instance.getCount("b"));
        assertEquals(1, instance.getCount("c"));
        assertEquals(0, instance.getCount("j"));
        assertEquals(2, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));

        instance.add("b");
        assertEquals(4, instance.getTotalCount());
        assertEquals(3, instance.getCount("b"));
        assertEquals(1, instance.getCount("c"));
        assertEquals(0, instance.getCount("j"));
        assertEquals(2, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));

        instance.add("d", 7);
        assertEquals(11, instance.getTotalCount());
        assertEquals(3, instance.getCount("b"));
        assertEquals(8, instance.getCount("c"));
        assertEquals(0, instance.getCount("j"));
        assertEquals(2, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));

        instance.add("m", 3);
        assertEquals(14, instance.getTotalCount());
        assertEquals(3, instance.getCount("b"));
        assertEquals(8, instance.getCount("c"));
        assertEquals(3, instance.getCount("j"));
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));
        assertTrue(instance.getDomain().contains("j"));

        instance.add("j", 14);
        assertEquals(28, instance.getTotalCount());
        assertEquals(3, instance.getCount("b"));
        assertEquals(8, instance.getCount("c"));
        assertEquals(17, instance.getCount("j"));
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));
        assertTrue(instance.getDomain().contains("j"));

        instance.add("z", 123);
        assertEquals(28, instance.getTotalCount());
        assertEquals(3, instance.getCount("b"));
        assertEquals(8, instance.getCount("c"));
        assertEquals(17, instance.getCount("j"));
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));
        assertTrue(instance.getDomain().contains("j"));
        assertFalse(instance.getDomain().contains("z"));

        boolean exceptionThrown = false;
        try
        {
            instance.add("d", -1);
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

    /**
     * Test of remove method, of class DataCountTreeSetBinnedMapHistogram.
     */
    @Override
    public void testRemove()
    {
        System.out.println("remove");

        DataCountTreeSetBinnedMapHistogram<String> instance =
            new DataCountTreeSetBinnedMapHistogram<String>("c", "j", "b", "q");
        assertEquals(0, instance.getTotalCount());
        assertEquals(0, instance.getCount("b"));
        assertEquals(0, instance.getCount("c"));
        assertEquals(0, instance.getCount("j"));
        assertEquals(0, instance.getDomain().size());

        instance.remove("a");
        instance.remove("d", 7);

        assertEquals(0, instance.getTotalCount());
        assertEquals(0, instance.getCount("b"));
        assertEquals(0, instance.getCount("c"));
        assertEquals(0, instance.getCount("j"));
        assertEquals(0, instance.getDomain().size());

        instance.add("b");
        instance.add("b");
        instance.add("f");
        instance.add("b");
        instance.add("m", 7);
        instance.add("c");
        assertEquals(12, instance.getTotalCount());
        assertEquals(3, instance.getCount("b"));
        assertEquals(2, instance.getCount("c"));
        assertEquals(7, instance.getCount("j"));
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));
        assertTrue(instance.getDomain().contains("j"));

        instance.remove("e");
        assertEquals(11, instance.getTotalCount());
        assertEquals(3, instance.getCount("b"));
        assertEquals(1, instance.getCount("c"));
        assertEquals(7, instance.getCount("j"));
        assertEquals(3, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertTrue(instance.getDomain().contains("c"));
        assertTrue(instance.getDomain().contains("j"));

        instance.remove("c");
        assertEquals(10, instance.getTotalCount());
        assertEquals(3, instance.getCount("b"));
        assertEquals(0, instance.getCount("c"));
        assertEquals(7, instance.getCount("j"));
        assertEquals(2, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertFalse(instance.getDomain().contains("c"));
        assertTrue(instance.getDomain().contains("j"));

        instance.remove("h");
        assertEquals(10, instance.getTotalCount());
        assertEquals(3, instance.getCount("b"));
        assertEquals(0, instance.getCount("c"));
        assertEquals(7, instance.getCount("j"));
        assertEquals(2, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertFalse(instance.getDomain().contains("c"));
        assertTrue(instance.getDomain().contains("j"));

        instance.remove("j", 4);
        assertEquals(6, instance.getTotalCount());
        assertEquals(3, instance.getCount("b"));
        assertEquals(0, instance.getCount("c"));
        assertEquals(3, instance.getCount("j"));
        assertEquals(2, instance.getDomain().size());
        assertTrue(instance.getDomain().contains("b"));
        assertFalse(instance.getDomain().contains("c"));
        assertTrue(instance.getDomain().contains("j"));

        instance.remove("b", 100);
        assertEquals(3, instance.getTotalCount());
        assertEquals(0, instance.getCount("b"));
        assertEquals(0, instance.getCount("c"));
        assertEquals(3, instance.getCount("j"));
        assertEquals(1, instance.getDomain().size());
        assertFalse(instance.getDomain().contains("b"));
        assertFalse(instance.getDomain().contains("c"));
        assertTrue(instance.getDomain().contains("j"));

        instance.remove("z", 123123);
        assertEquals(3, instance.getTotalCount());
        assertEquals(0, instance.getCount("b"));
        assertEquals(0, instance.getCount("c"));
        assertEquals(3, instance.getCount("j"));
        assertEquals(1, instance.getDomain().size());
        assertFalse(instance.getDomain().contains("b"));
        assertFalse(instance.getDomain().contains("c"));
        assertTrue(instance.getDomain().contains("j"));

        boolean exceptionThrown = false;
        try
        {
            instance.remove("d", -1);
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
