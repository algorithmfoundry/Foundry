/*
 * File:                MutableIntegerTest.java
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
 * Unit tests for class MutableInteger.
 * 
 * @author  Justin Basilico
 * @since   3.1.2
 */
public class MutableIntegerTest
    extends EuclideanRingTestHarness<MutableInteger>
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public MutableIntegerTest(
        final String testName)
    {
        super(testName);
    }

    protected int randomInt()
    {
        final int range = (int) this.RANGE + 1;
        return 2 * this.RANDOM.nextInt(range) - range;
    }

    @Override
    protected MutableInteger createRandom()
    {
        return new MutableInteger(this.randomInt());
    }

    protected int[] getGoodValues()
    {
        return new int[] { 1, 2, 3, 4, 5, 10, -1, -2, -3, -4, -10,
            Integer.MIN_VALUE, Integer.MAX_VALUE, 0, this.randomInt() };
    }

    /**
     * Test of constructors of class MutableInteger.
     */
    public void testConstructors()
    {
        int value = 0;
        MutableInteger instance = new MutableInteger();
        assertEquals(value, instance.getValue());

        value = this.randomInt();
        instance = new MutableInteger(value);
        assertEquals(value, instance.getValue());

        instance = new MutableInteger(instance);
        assertEquals(value, instance.getValue());
    }

    /**
     * Test of equals method, of class MutableInteger.
     */
    public void testEquals()
    {
        int[] values = this.getGoodValues();
        for (int xValue : values)
        {
            MutableInteger x = new MutableInteger(xValue);
            assertTrue(x.equals(x));

            for (int yValue : values)
            {
                MutableInteger y = new MutableInteger(yValue);

                assertEquals(xValue == yValue, x.equals(y));
                assertEquals(new Integer(xValue).equals(new Integer(yValue)),
                    x.equals(y));
            }
        }
    }

    /**
     * Test of compareTo method, of class MutableInteger.
     */
    public void testCompareTo()
    {
        int[] values = this.getGoodValues();
        for (int xValue : values)
        {
            MutableInteger x = new MutableInteger(xValue);
            assertEquals(0, x.compareTo(x));

            for (int yValue : values)
            {
                MutableInteger y = new MutableInteger(yValue);
                
                assertEquals(new Integer(xValue).compareTo(new Integer(yValue)),
                    x.compareTo(y));
            }
        }
    }

    /**
     * Test of hashCode method, of class MutableInteger.
     */
    public void testHashCode()
    {
        MutableInteger instance = new MutableInteger();
        assertEquals(0, instance.hashCode());

        for (int value : this.getGoodValues())
        {
            instance.setValue(value);
            int expected = new Integer(value).hashCode();
            assertEquals(expected, instance.hashCode());
            assertEquals(expected, instance.hashCode());
            assertEquals(expected, new MutableInteger(value).hashCode());
        }
    }

    /**
     * Test of intValue method, of class MutableInteger.
     */
    public void testIntValue()
    {
        int value = 0;
        MutableInteger instance = new MutableInteger();
        int expected = value;
        assertEquals(expected, instance.intValue());

        for (int goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            expected = value;
            assertEquals(expected, instance.intValue());
        }
    }

    /**
     * Test of longValue method, of class MutableInteger.
     */
    public void testLongValue()
    {
        int value = 0;
        MutableInteger instance = new MutableInteger();
        long expected = (long) value;
        assertEquals(expected, instance.longValue());

        for (int goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            expected = (long) value;
            assertEquals(expected, instance.longValue());
        }
    }

    /**
     * Test of floatValue method, of class MutableInteger.
     */
    public void testFloatValue()
    {
        int value = 0;
        MutableInteger instance = new MutableInteger();
        float expected = (float) value;
        assertEquals(expected, instance.floatValue());

        for (int goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            expected = (float) value;
            assertEquals(expected, instance.floatValue());
        }
    }

    /**
     * Test of doubleValue method, of class MutableInteger.
     */
    public void testDoubleValue()
    {
        int value = 0;
        MutableInteger instance = new MutableInteger();
        double expected = (double) value;
        assertEquals(expected, instance.doubleValue());

        for (int goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            expected = (double) value;
            assertEquals(expected, instance.doubleValue());
        }
    }
    
    /**
     * Test of getValue method, of class MutableInteger.
     */
    public void testGetValue()
    {
        this.testSetValue();
    }

    /**
     * Test of setValue method, of class MutableInteger.
     */
    public void testSetValue()
    {
        int value = 0;
        MutableInteger instance = new MutableInteger();
        assertEquals(value, instance.value);
        assertEquals(value, instance.getValue());

        for (int goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            assertEquals(value, instance.value);
            assertEquals(value, instance.getValue());
        }
    }

    /**
     * Test of toString method, of class MutableInteger.
     */
    public void testToString()
    {
        String expected = "0";
        MutableInteger instance = new MutableInteger();
        assertEquals(expected, instance.toString());

        for (int value : this.getGoodValues())
        {
            instance.setValue(value);
            expected = new Integer(value).toString();
            assertEquals(expected, instance.toString());
        }
    }

    /**
     * Test of convertToVector method, of class MutableInteger.
     */
    public void testConvertToVector()
    {
        MutableInteger instance = new MutableInteger();
        Vector1 result = instance.convertToVector();
        assertEquals(0, (int) result.getX());

        int value = this.randomInt();
        instance.setValue(value);
        assertEquals(0, (int) result.getX());

        result = instance.convertToVector();
        assertEquals(value, (int) result.getX());

        result.setX(this.randomInt());
        assertEquals(value, instance.getValue());
    }

    /**
     * Test of convertFromVector method, of class MutableInteger.
     */
    public void testConvertFromVector()
    {
        MutableInteger instance = new MutableInteger();

        Vector vector = VectorFactory.getDefault().createVector(1);
        int value = 0;
        instance.convertFromVector(vector);
        assertEquals(value, instance.getValue());

        value = this.randomInt();
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
        int value = 0;
        int scale = this.randomInt();
        int expected = 0;
        MutableInteger instance = new MutableInteger();
        expected = value * scale;
        instance.scaleEquals(scale);
        assertEquals(expected, instance.getValue());

        value = this.randomInt();
        scale = this.randomInt();
        instance.setValue(value);
        expected = value * scale;
        instance.scaleEquals(scale);
        assertEquals(expected, instance.getValue());

        for (int i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            scale = this.randomInt();
            expected *= scale;
            instance.scaleEquals(scale);
            assertEquals(expected, instance.getValue());
        }

    }

    @Override
    public void testPlusEquals()
    {
        int value = 0;
        int otherValue = this.randomInt();
        int expected = 0;
        MutableInteger instance = new MutableInteger();
        expected = value + otherValue;
        instance.plusEquals(new MutableInteger(otherValue));
        assertEquals(expected, instance.getValue());

        value = this.randomInt();
        otherValue = this.randomInt();
        instance.setValue(value);
        expected = value + otherValue;
        instance.plusEquals(new MutableInteger(otherValue));
        assertEquals(expected, instance.getValue());

        for (int i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            otherValue = this.randomInt();
            MutableInteger other = new MutableInteger(otherValue);
            expected += otherValue;
            instance.plusEquals(other);
            assertEquals(expected, instance.getValue());
            assertEquals(otherValue, other.getValue());
        }
    }

    @Override
    public void testTimesEquals()
    {
        int value = 0;
        int otherValue = this.randomInt();
        int expected = 0;
        MutableInteger instance = new MutableInteger();
        expected = value * otherValue;
        instance.timesEquals(new MutableInteger(otherValue));
        assertEquals(expected, instance.getValue());

        value = this.randomInt();
        otherValue = this.randomInt();
        instance.setValue(value);
        expected = value * otherValue;
        instance.timesEquals(new MutableInteger(otherValue));
        assertEquals(expected, instance.getValue());

        for (int i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            otherValue = this.randomInt();
            MutableInteger other = new MutableInteger(otherValue);
            expected *= otherValue;
            instance.timesEquals(other);
            assertEquals(expected, instance.getValue());
            assertEquals(otherValue, other.getValue());
        }
    }

    @Override
    public void testDivideEquals()
    {
        int value = 0;
        int otherValue = this.randomInt();
        int expected = 0;
        MutableInteger instance = new MutableInteger();
        expected = value / otherValue;
        instance.divideEquals(new MutableInteger(otherValue));
        assertEquals(expected, instance.getValue());

        value = this.randomInt();
        otherValue = this.randomInt();
        instance.setValue(value);
        expected = value / otherValue;
        instance.divideEquals(new MutableInteger(otherValue));
        assertEquals(expected, instance.getValue());

        for (int i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            otherValue = this.randomInt();
            MutableInteger other = new MutableInteger(otherValue);

            if (otherValue != 0)
            {

                expected /= otherValue;
                instance.divideEquals(other);
                assertEquals(expected, instance.getValue());
                assertEquals(otherValue, other.getValue());
            }
            else
            {
                boolean exceptionThrown = false;
                try
                {
                    instance.divideEquals(other);
                }
                catch (ArithmeticException e)
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

    @Override
    public void testDotTimesEquals()
    {
        int value = 0;
        int otherValue = this.randomInt();
        int expected = 0;
        MutableInteger instance = new MutableInteger();
        expected = value * otherValue;
        instance.dotTimesEquals(new MutableInteger(otherValue));
        assertEquals(expected, instance.getValue());

        value = this.randomInt();
        otherValue = this.randomInt();
        instance.setValue(value);
        expected = value * otherValue;
        instance.dotTimesEquals(new MutableInteger(otherValue));
        assertEquals(expected, instance.getValue());

        for (int i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            otherValue = this.randomInt();
            MutableInteger other = new MutableInteger(otherValue);
            expected *= otherValue;
            instance.dotTimesEquals(other);
            assertEquals(expected, instance.getValue());
            assertEquals(otherValue, other.getValue());
        }
    }


}