/*
 * File:                VectorFactoryTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 25, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.matrix;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for VectorFactoryTest.
 *
 * @author krdixon
 */
public class VectorFactoryTest
    extends VectorFactoryTestHarness
{

    /**
     * Tests for class VectorFactoryTest.
     * @param testName Name of the test.
     */
    public VectorFactoryTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public VectorFactory<?> createFactory()
    {
        return VectorFactory.getDefault();
    }

    /**
     * Tests getDefault()
     */
    public void testGetDefault()
    {
        System.out.println("getDefault");
        VectorFactory<?> f = VectorFactory.getDefault();
        assertNotNull(f);
        assertSame(f, VectorFactory.DEFAULT_DENSE_INSTANCE);
    }

    /**
     * Tests getDenseDefault()
     */
    public void testGetDenseDefault()
    {
        System.out.println("getDenseDefault");
        VectorFactory<?> f = VectorFactory.getDenseDefault();
        assertNotNull(f);
        assertSame(VectorFactory.DEFAULT_DENSE_INSTANCE, f);
    }

    /**
     * Tests getSparseDefault()
     */
    public void testGetSparseDefault()
    {
        System.out.println("getSparseDefault");
        SparseVectorFactory<?> f = VectorFactory.getSparseDefault();
        assertNotNull(f);
        assertSame(VectorFactory.DEFAULT_SPARSE_INSTANCE, f);
    }

    /**
     * Tests copyValues
     */
    public void testCreateVectorCollection()
    {

        List<Double> values = Arrays.asList(
            RANDOM.nextGaussian(), RANDOM.nextGaussian(), RANDOM.nextGaussian() );
        Vector v = this.createFactory().copyValues(values);
        assertEquals( values.size(), v.getDimensionality() );
        for( int i = 0; i < v.getDimensionality(); i++ )
        {
            assertEquals( values.get(i), v.getElement(i) );
        }

    }
    
    public void testCopyMap()
    {
        VectorFactory<?> instance = this.createFactory();
        Map<Integer, Double> map = new LinkedHashMap<>();
        Vector empty = instance.copyMap(0, map);
        assertEquals(0, empty.getDimensionality());
        
        Vector zeros = instance.copyMap(10, map);
        assertEquals(10, zeros.getDimensionality());
        assertTrue(zeros.isZero());

        map.put(0, RANDOM.nextGaussian());
        map.put(2, RANDOM.nextGaussian());
        map.put(3, RANDOM.nextGaussian());
        map.put(7, RANDOM.nextGaussian());
        int d = 8;
        
        Vector v = instance.copyMap(d, map);
        assertEquals(d, v.getDimensionality());
        for (int i = 0; i < d; i++)
        {
            Double entry = map.get(i);
            double expectedValue = entry == null ? 0.0 : entry;
            assertEquals(expectedValue, v.get(i));
        }
        
        // Negative values should throw an exception.
        int[] badIndices = {-1, d, d+1};
        for (int badIndex : badIndices)
        {
            map.put(badIndex, 1.0);
            boolean exceptionThrown = false;
            try
            {
                instance.copyMap(d, map);
            }
            catch (Exception e)
            {
                exceptionThrown = true;
            }
            finally
            {
                assertTrue(exceptionThrown);
            }
            map.remove(badIndex);
        }
        
        // Negative dimensionalities not allowed.
        boolean exceptionThrown = false;
        try
        {
            instance.copyMap(-1, new LinkedHashMap<Integer, Double>());
        }
        catch (Exception e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        // Empty maps not allowed.
        exceptionThrown = false;
        try
        {
            instance.copyMap(d, null);
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

}
