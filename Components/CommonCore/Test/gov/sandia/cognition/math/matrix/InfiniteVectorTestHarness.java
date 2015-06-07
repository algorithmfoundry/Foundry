/*
 * File:                InfiniteVectorTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 26, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.collection.ScalarMap;
import gov.sandia.cognition.collection.CollectionUtil;
import java.util.Iterator;
import java.util.Set;

/**
 * Tests for class InfiniteVectorTestHarness.
 * @param <DataType>
 * Type of key
 * @author krdixon
 */
public abstract class InfiniteVectorTestHarness<DataType>
    extends VectorSpaceTestHarness<InfiniteVector<DataType>>
{

    /**
     * Default Constructor
     */
    public InfiniteVectorTestHarness(
        String name )
    {
        super( name );
    }

    abstract protected InfiniteVector<DataType> createZero();
    
    /**
     * Random2
     * @return
     * random2
     */
    abstract protected InfiniteVector<DataType> createRandom2();

    /**
     * Test of increment method, of class InfiniteVector.
     */
    public void testIncrement()
    {
        System.out.println("increment");
        InfiniteVector<DataType> instance = this.createRandom();
        double min = instance.getMinValue();
        double delta = 1.0;
        DataType minKey = instance.getMinValueKey();
        instance.increment(minKey, delta);
        assertEquals(min + delta, instance.get(minKey), TOLERANCE);

        instance.set(minKey, 0.0);
        instance.increment(minKey, delta);
        assertEquals(delta, instance.get(minKey), TOLERANCE);

        instance.set(minKey, 0.0);
        instance.compact();
        instance.increment(minKey, delta);
        assertEquals(delta, instance.get(minKey), TOLERANCE);

        instance.increment(minKey);
        assertEquals(delta + 1.0, instance.get(minKey), TOLERANCE);

    }

    /**
     * Test of getMax method, of class InfiniteVector.
     */
    public void testGetMax()
    {
        System.out.println("getMax");

        InfiniteVector<DataType> instance = this.createRandom();
        double result = instance.getMaxValue();
        double max = Double.NEGATIVE_INFINITY;
        for (ScalarMap.Entry<DataType> entry : instance.entrySet())
        {
            double value = entry.getValue();
            if (max < value)
            {
                max = value;
            }
        }
        assertEquals(max, result, TOLERANCE);
    }

    /**
     * Test of getMaxKey method, of class InfiniteVector.
     */
    public void testGetMaxKey()
    {
        System.out.println("getMaxKey");

        InfiniteVector<DataType> instance = this.createRandom();
        double max = instance.getMaxValue();
        DataType result = instance.getMaxValueKey();
        assertEquals(max, instance.get(result), TOLERANCE);

        DataType minkey = instance.getMinValueKey();
        instance.set(minkey, max);
        result = instance.getMaxValueKey();
        assertEquals(max, instance.get(instance.getMaxValueKey()), TOLERANCE);
        assertEquals(max, instance.get(minkey), TOLERANCE);
    }

    /**
     * Test of getMaxKeys method, of class InfiniteVector.
     */
    public void testGetMaxKeys()
    {
        System.out.println("getMaxKeys");
        InfiniteVector<DataType> instance = this.createRandom();
        double max = instance.getMaxValue();
        Set<DataType> result = instance.getMaxValueKeys();
        assertEquals(1, result.size());
        assertEquals(max, instance.get(CollectionUtil.getElement(result, 0)),
            TOLERANCE);

        DataType minkey = instance.getMinValueKey();
        instance.set(minkey, max);
        result = instance.getMaxValueKeys();
        assertEquals(2, result.size());
        assertEquals(max, instance.get(CollectionUtil.getElement(result, 0)),
            TOLERANCE);
        assertEquals(max, instance.get(CollectionUtil.getElement(result, 1)),
            TOLERANCE);
    }

    /**
     * Test of getMin method, of class InfiniteVector.
     */
    public void testGetMin()
    {
        System.out.println("getMin");
        InfiniteVector<DataType> instance = this.createRandom();
        double min = instance.getMinValue();
        DataType result = instance.getMinValueKey();
        assertEquals(min, instance.get(result), TOLERANCE);

        DataType maxkey = instance.getMaxValueKey();
        instance.set(maxkey, min);
        result = instance.getMinValueKey();
        assertEquals(min, instance.get(instance.getMinValueKey()), TOLERANCE);
        assertEquals(min, instance.get(maxkey), TOLERANCE);
    }

    /**
     * Test of getMinKey method, of class InfiniteVector.
     */
    public void testGetMinKey()
    {
        System.out.println("getMinKey");

        InfiniteVector<DataType> instance = this.createRandom();
        double min = instance.getMinValue();
        DataType result = instance.getMinValueKey();
        assertEquals(min, instance.get(result), TOLERANCE);

        DataType maxkey = instance.getMaxValueKey();
        instance.set(maxkey, min);
        result = instance.getMinValueKey();
        assertEquals(min, instance.get(instance.getMinValueKey()), TOLERANCE);
        assertEquals(min, instance.get(maxkey), TOLERANCE);
    }

    /**
     * Test of getMinKeys method, of class InfiniteVector.
     */
    public void testGetMinKeys()
    {
        System.out.println("getMinKeys");
        InfiniteVector<DataType> instance = this.createRandom();
        double min = instance.getMinValue();
        Set<DataType> result = instance.getMinValueKeys();
        assertEquals(1, result.size());
        assertEquals(min, instance.get(CollectionUtil.getElement(result, 0)),
            TOLERANCE);

        DataType maxkey = instance.getMaxValueKey();
        instance.set(maxkey, min);
        result = instance.getMinValueKeys();
        assertEquals(2, result.size());
        assertEquals(min, instance.get(CollectionUtil.getElement(result, 0)),
            TOLERANCE);
        assertEquals(min, instance.get(CollectionUtil.getElement(result, 1)),
            TOLERANCE);
    }

    /**
     * Test of compact method, of class InfiniteVector.
     */
/*
    @Test
    public void testCompact()
    {
        System.out.println("compact");

        InfiniteVector<DataType> instance = this.createRandom();
        int s = instance.size();
        DataType minkey = instance.getMinValueKey();
        instance.set(minkey, 0.0);
        assertEquals(s, instance.size());
        instance.compact();
        assertEquals(s - 1, instance.size());

        instance.set(minkey, 1.0);
        assertEquals(s, instance.size());
        instance.compact();
        assertEquals(s, instance.size());

        instance.set(minkey, 0.0);
        assertEquals(s - 1, instance.size());

        instance.set(minkey, 1.0);
        assertEquals(s, instance.size());
        instance.set(minkey, 0.0);
        instance.set(instance.getMaxValueKey(), 0.0);
        instance.compact();
        assertEquals(s - 2, instance.size());


    }
*/
    @Override
    public void testScaleEquals()
    {
        InfiniteVector<DataType> v1 = this.createRandom();
        InfiniteVector<DataType> v1clone = v1.clone();
        double scale = RANDOM.nextDouble();
        v1.scaleEquals(scale);

        for (ScalarMap.Entry<DataType> entry : v1.entrySet())
        {
            double p = v1clone.get(entry.getKey());
            assertEquals(p * scale, entry.getValue(), TOLERANCE);
        }

    }

    @Override
    public void testPlusEquals()
    {
        System.out.println("plusEquals");
        InfiniteVector<DataType> v1 = this.createRandom2();
System.out.println(v1);
        InfiniteVector<DataType> v2 = this.createRandom();
        InfiniteVector<DataType> v1clone = v1.clone();
System.out.println(v1);
System.out.println(v2);
System.out.println(v1clone);
        v1.plusEquals(v2);
        for (ScalarMap.Entry<DataType> entry : v1.entrySet())
        {
            assertEquals(v1.get(entry.getKey()), v1clone.get(entry.getKey()) + v2.get(
                entry.getKey()), TOLERANCE);
        }
        // Iterate through the keys in v1clone just to make sure we've got them all
        for (ScalarMap.Entry<DataType> entry : v1clone.entrySet())
        {
            assertEquals(v1.get(entry.getKey()), v1clone.get(entry.getKey()) + v2.get(
                entry.getKey()), TOLERANCE);
        }

        // Iterate through the keys in v2 just to make sure we've got them all
        for (ScalarMap.Entry<DataType> entry : v2.entrySet())
        {
            assertEquals(v1.get(entry.getKey()), v1clone.get(entry.getKey()) + v2.get(
                entry.getKey()), 0.0);
        }

    }

    @Override
    public void testDotTimesEquals()
    {
        System.out.println("dotTimeEquals");

        InfiniteVector<DataType> v1 = this.createRandom2();
        InfiniteVector<DataType> v2 = this.createRandom();
        InfiniteVector<DataType> v1clone = v1.clone();

        v1.dotTimesEquals(v2);
        for (ScalarMap.Entry<DataType> entry : v1.entrySet())
        {
            assertEquals(v1.get(entry.getKey()), v1clone.get(entry.getKey()) * v2.get(
                entry.getKey()), 0.0);
        }
        // Iterate through the keys in v1clone just to make sure we've got them all
        for (ScalarMap.Entry<DataType> entry : v1clone.entrySet())
        {
            assertEquals(v1.get(entry.getKey()), v1clone.get(entry.getKey()) * v2.get(
                entry.getKey()), 0.0);
        }

        // Iterate through the keys in v2 just to make sure we've got them all
        for (ScalarMap.Entry<DataType> entry : v2.entrySet())
        {
            assertEquals(v1.get(entry.getKey()), v1clone.get(entry.getKey()) * v2.get(
                entry.getKey()), 0.0);
        }

    }
    
    public void testForEachEntry()
    {
        InfiniteVector<DataType> zero = this.createZero();
        zero.forEachEntry(new InfiniteVector.KeyValueConsumer<DataType>()
        {
            @Override
            public void consume(
                final DataType key,
                final double value)
            {
                fail("Should not be called.");
            }
        });
        
        InfiniteVector<DataType> instance = this.createRandom();
        InfiniteVector<DataType> clone = instance.clone();
        {
            final Iterator<InfiniteVector.Entry<DataType>> it = 
                instance.iterator();
            instance.forEachEntry(new InfiniteVector.KeyValueConsumer<DataType>()
            {
                @Override
                public void consume(
                    final DataType key,
                    final double value)
                {
                    assertTrue(it.hasNext());
                    InfiniteVector.Entry<DataType> entry = it.next();
                    assertEquals(entry.getKey(), key);
                    assertEquals(entry.getValue(), value);
                }
            });
            assertFalse(it.hasNext());
            assertEquals(clone, instance);
        }
        
        
        boolean exceptionThrown = false;
        try
        {
            this.createRandom().forEachEntry(null);
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
    
    public void testForEachNonZero()
    {
        InfiniteVector<DataType> zero = this.createZero();
        zero.forEachNonZero(new InfiniteVector.KeyValueConsumer<DataType>()
        {
            @Override
            public void consume(
                final DataType key,
                final double value)
            {
                fail("Should not be called.");
            }
        });
        
        InfiniteVector<DataType> instance = this.createRandom();
        instance.set(instance.getMinValueKey(), 0.0);
        InfiniteVector<DataType> clone = instance.clone();
        {
            final Iterator<InfiniteVector.Entry<DataType>> it = 
                instance.iterator();
            instance.forEachNonZero(new InfiniteVector.KeyValueConsumer<DataType>()
            {
                @Override
                public void consume(
                    final DataType key,
                    final double value)
                {
                    assertTrue(it.hasNext());
                    InfiniteVector.Entry<DataType> entry = it.next();
                    while (entry.getValue() == 0.0)
                    {
                        entry = it.next();
                    }
                    assertEquals(entry.getKey(), key);
                    assertEquals(entry.getValue(), value);
                }
            });
            // All the remaining values in the iterator must be zero.
            while (it.hasNext())
            {
                InfiniteVector.Entry<DataType> entry = it.next();
                assertEquals(0.0, entry.getValue());
            }
            assertEquals(clone, instance);
        }
        
        
        boolean exceptionThrown = false;
        try
        {
            this.createRandom().forEachNonZero(null);
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
