/*
 * File:                MutableLongTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 14, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.math.matrix.DimensionalityMismatchException;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.Vector1;
import gov.sandia.cognition.math.matrix.mtj.Vector2;

/**
 * Unit tests for class MutableLong.
 * 
 * @author  Justin Basilico
 * @since   3.1.2
 */
public class MutableLongTest
    extends RingTestHarness<MutableLong>
{
    
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public MutableLongTest(
        final String testName)
    {
        super(testName);
    }

    protected long randomLong()
    {
        return (long) ((this.RANDOM.nextDouble() - 0.5) * (1.0 + this.RANGE));
    }

    @Override
    protected MutableLong createRandom()
    {
        return new MutableLong(this.randomLong());
    }

    protected long[] getGoodValues()
    {
        return new long[] { 1, 2, 3, 4, 5, 10, -1, -2, -3, -4, -10,
            Long.MAX_VALUE, Long.MAX_VALUE, 0, this.randomLong(),
            this.randomLong() };
    }

    /**
     * Test of constructors of class MutableLong.
     */
    public void testConstructors()
    {
        long value = 0;
        MutableLong instance = new MutableLong();
        assertEquals(value, instance.getValue());

        value = this.randomLong();
        instance = new MutableLong(value);
        assertEquals(value, instance.getValue());

        instance = new MutableLong(instance);
        assertEquals(value, instance.getValue());
    }

    /**
     * Test of equals method, of class MutableLong.
     */
    public void testEquals()
    {
        long[] values = this.getGoodValues();
        for (long xValue : values)
        {
            MutableLong x = new MutableLong(xValue);
            assertTrue(x.equals(x));

            for (long yValue : values)
            {
                MutableLong y = new MutableLong(yValue);

                assertEquals(xValue == yValue, x.equals(y));
                assertEquals(new Long(xValue).equals(new Long(yValue)),
                    x.equals(y));
            }
        }
    }

    /**
     * Test of compareTo method, of class MutableLong.
     */
    public void testCompareTo()
    {
        long[] values = this.getGoodValues();
        for (long xValue : values)
        {
            MutableLong x = new MutableLong(xValue);
            assertEquals(0, x.compareTo(x));

            for (long yValue : values)
            {
                MutableLong y = new MutableLong(yValue);

                assertEquals(new Long(xValue).compareTo(new Long(yValue)),
                    x.compareTo(y));
            }
        }
    }

    /**
     * Test of hashCode method, of class MutableLong.
     */
    public void testHashCode()
    {
        MutableLong instance = new MutableLong();
        assertEquals(0, instance.hashCode());

        for (long value : this.getGoodValues())
        {
            instance.setValue(value);
            long expected = new Long(value).hashCode();
            assertEquals(expected, instance.hashCode());
            assertEquals(expected, instance.hashCode());
            assertEquals(expected, new MutableLong(value).hashCode());
        }
    }


    /**
     * Test of intValue method, of class MutableLong.
     */
    public void testIntValue()
    {
        long value = 0;
        MutableLong instance = new MutableLong();
        int expected = (int) value;
        assertEquals(expected, instance.intValue());

        for (long goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            expected = (int) value;
            assertEquals(expected, instance.intValue());
        }
    }

    /**
     * Test of longValue method, of class MutableLong.
     */
    public void testLongValue()
    {
        long value = 0;
        MutableLong instance = new MutableLong();
        long expected = value;
        assertEquals(expected, instance.longValue());

        for (long goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            expected = value;
            assertEquals(expected, instance.longValue());
        }
    }

    /**
     * Test of floatValue method, of class MutableLong.
     */
    public void testFloatValue()
    {
        long value = 0;
        MutableLong instance = new MutableLong();
        float expected = (float) value;
        assertEquals(expected, instance.floatValue());

        for (long goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            expected = (float) value;
            assertEquals(expected, instance.floatValue());
        }
    }

    /**
     * Test of doubleValue method, of class MutableLong.
     */
    public void testDoubleValue()
    {
        long value = 0;
        MutableLong instance = new MutableLong();
        double expected = (double) value;
        assertEquals(expected, instance.doubleValue());

        for (long goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            expected = (double) value;
            assertEquals(expected, instance.doubleValue());
        }
    }

    /**
     * Test of getValue method, of class MutableLong.
     */
    public void testGetValue()
    {
        this.testSetValue();
    }

    /**
     * Test of setValue method, of class MutableLong.
     */
    public void testSetValue()
    {
        long value = 0;
        MutableLong instance = new MutableLong();
        assertEquals(value, instance.value);
        assertEquals(value, instance.getValue());

        for (long goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            assertEquals(value, instance.value);
            assertEquals(value, instance.getValue());
        }
    }

    /**
     * Test of toString method, of class MutableLong.
     */
    public void testToString()
    {
        String expected = "0";
        MutableLong instance = new MutableLong();
        assertEquals(expected, instance.toString());

        for (long value : this.getGoodValues())
        {
            instance.setValue(value);
            expected = new Long(value).toString();
            assertEquals(expected, instance.toString());
        }
    }

    /**
     * Test of convertToVector method, of class MutableLong.
     */
    public void testConvertToVector()
    {
        MutableLong instance = new MutableLong();
        Vector1 result = instance.convertToVector();
        assertEquals(0, (long) result.getX());

        long value = this.randomLong();
        instance.setValue(value);
        assertEquals(0, (long) result.getX());

        result = instance.convertToVector();
        assertEquals(value, (long) result.getX());

        result.setX(this.randomLong());
        assertEquals(value, instance.getValue());
    }

    /**
     * Test of convertFromVector method, of class MutableLong.
     */
    public void testConvertFromVector()
    {
        MutableLong instance = new MutableLong();

        Vector vector = VectorFactory.getDefault().createVector(1);
        long value = 0;
        instance.convertFromVector(vector);
        assertEquals(value, instance.getValue());

        value = this.randomLong();
        vector.setElement(0, value);
        instance.convertFromVector(vector);
        assertEquals(value, instance.getValue());

        boolean exceptionThrown = false;
        try
        {
            instance.convertFromVector(new Vector2());
        }
        catch (DimensionalityMismatchException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }

    }

    @Override
    public void testScaleEquals()
    {
        long value = 0;
        long scale = this.randomLong();
        long expected = 0;
        MutableLong instance = new MutableLong();
        expected = value * scale;
        instance.scaleEquals(scale);
        assertEquals(expected, instance.getValue());

        value = this.randomLong();
        scale = this.randomLong();
        instance.setValue(value);
        expected = value * scale;
        instance.scaleEquals(scale);
        assertEquals(expected, instance.getValue());

        for (long i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            scale = this.randomLong();
            expected *= scale;
            instance.scaleEquals(scale);
            assertEquals(expected, instance.getValue());
        }

    }

    @Override
    public void testPlusEquals()
    {
        long value = 0;
        long otherValue = this.randomLong();
        long expected = 0;
        MutableLong instance = new MutableLong();
        expected = value + otherValue;
        instance.plusEquals(new MutableLong(otherValue));
        assertEquals(expected, instance.getValue());

        value = this.randomLong();
        otherValue = this.randomLong();
        instance.setValue(value);
        expected = value + otherValue;
        instance.plusEquals(new MutableLong(otherValue));
        assertEquals(expected, instance.getValue());

        for (long i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            otherValue = this.randomLong();
            MutableLong other = new MutableLong(otherValue);
            expected += otherValue;
            instance.plusEquals(other);
            assertEquals(expected, instance.getValue());
            assertEquals(otherValue, other.getValue());
        }
    }

    @Override
    public void testDotTimesEquals()
    {
        long value = 0;
        long otherValue = this.randomLong();
        long expected = 0;
        MutableLong instance = new MutableLong();
        expected = value * otherValue;
        instance.dotTimesEquals(new MutableLong(otherValue));
        assertEquals(expected, instance.getValue());

        value = this.randomLong();
        otherValue = this.randomLong();
        instance.setValue(value);
        expected = value * otherValue;
        instance.dotTimesEquals(new MutableLong(otherValue));
        assertEquals(expected, instance.getValue());

        for (long i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            otherValue = this.randomLong();
            MutableLong other = new MutableLong(otherValue);
            expected *= otherValue;
            instance.dotTimesEquals(other);
            assertEquals(expected, instance.getValue());
            assertEquals(otherValue, other.getValue());
        }
    }


}
