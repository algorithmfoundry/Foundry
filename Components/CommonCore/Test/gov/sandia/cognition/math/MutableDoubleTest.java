/*
 * File:                MutableDoubleTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 13, 2011, Sandia Corporation.
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
 * Unit tests for class MutableDouble.
 *
 * @author  Justin Basilico
 * @since   3.2.0
 */
public class MutableDoubleTest
    extends RingTestHarness<MutableDouble>
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public MutableDoubleTest(
        final String testName)
    {
        super(testName);
    }

    protected double randomDouble()
    {
        return (this.RANDOM.nextDouble() - 0.5) * this.RANGE;
    }

    @Override
    protected MutableDouble createRandom()
    {
        return new MutableDouble(this.randomDouble());
    }

    protected double[] getGoodValues()
    {
        return new double[] { 1.0, -0.1, 3.14, Math.PI, Math.E, Double.NaN,
            Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
            Double.MIN_VALUE, Double.MAX_VALUE, 0.0, this.randomDouble() };
    }

    /**
     * Test of constructors of class MutableDouble.
     */
    public void testConstructors()
    {
        double value = 0.0;
        MutableDouble instance = new MutableDouble();
        assertEquals(value, instance.getValue(), 0.0);

        value = this.randomDouble();
        instance = new MutableDouble(value);
        assertEquals(value, instance.getValue(), 0.0);

        instance = new MutableDouble(instance);
        assertEquals(value, instance.getValue(), 0.0);
    }

    /**
     * Test of equals method, of class MutableDouble.
     */
    public void testEquals()
    {
        double[] values = this.getGoodValues();
        for (double xValue : values)
        {
            MutableDouble x = new MutableDouble(xValue);
            assertTrue(x.equals(x));

            for (double yValue : values)
            {
                MutableDouble y = new MutableDouble(yValue);

                assertEquals(new Double(xValue).equals(new Double(yValue)),
                    x.equals(y));
            }
        }
    }

    /**
     * Test of compareTo method, of class MutableDouble.
     */
    public void testCompareTo()
    {
        double[] values = this.getGoodValues();
        for (double xValue : values)
        {
            MutableDouble x = new MutableDouble(xValue);
            assertEquals(0, x.compareTo(x));

            for (double yValue : values)
            {
                MutableDouble y = new MutableDouble(yValue);

                assertEquals(new Double(xValue).compareTo(new Double(yValue)), x.compareTo(
                    y));
            }
        }
    }

    /**
     * Test of hashCode method, of class MutableDouble.
     */
    public void testHashCode()
    {
        MutableDouble instance = new MutableDouble();
        assertEquals(0, instance.hashCode());

        for (double value : this.getGoodValues())
        {
            instance.setValue(value);
            int expected = new Double(value).hashCode();
            assertEquals(expected, instance.hashCode());
            assertEquals(expected, instance.hashCode());
            assertEquals(expected, new MutableDouble(value).hashCode());
        }
    }

    /**
     * Test of intValue method, of class MutableDouble.
     */
    public void testIntValue()
    {
        double value = 0.0;
        MutableDouble instance = new MutableDouble();
        int expected = (int) value;
        assertEquals(expected, instance.intValue(), 0.0);

        for (double goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            expected = (int) value;
            assertEquals(expected, instance.intValue(), 0.0);
        }
    }

    /**
     * Test of longValue method, of class MutableDouble.
     */
    public void testLongValue()
    {
        double value = 0.0;
        MutableDouble instance = new MutableDouble();
        long expected = (long) value;
        assertEquals(expected, instance.longValue(), 0.0);

        for (double goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            expected = (long) value;
            assertEquals(expected, instance.longValue(), 0.0);
        }
    }

    /**
     * Test of floatValue method, of class MutableDouble.
     */
    public void testFloatValue()
    {
        double value = 0.0;
        MutableDouble instance = new MutableDouble();
        float expected = (float) value;
        assertEquals(expected, instance.floatValue(), 0.0);

        for (double goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            expected = (float) value;
            assertEquals(expected, instance.floatValue(), 0.0);
        }
    }

    /**
     * Test of doubleValue method, of class MutableDouble.
     */
    public void testDoubleValue()
    {
        double value = 0.0;
        MutableDouble instance = new MutableDouble();
        assertEquals(value, instance.doubleValue(), 0.0);

        for (double goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            assertEquals(value, instance.doubleValue(), 0.0);
        }

    }

    /**
     * Test of getValue method, of class MutableDouble.
     */
    public void testGetValue()
    {
        this.testSetValue();
    }

    /**
     * Test of setValue method, of class MutableDouble.
     */
    public void testSetValue()
    {
        double value = 0.0;
        MutableDouble instance = new MutableDouble();
        assertEquals(value, instance.value, 0.0);
        assertEquals(value, instance.getValue(), 0.0);

        for (double goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            assertEquals(value, instance.value, 0.0);
            assertEquals(value, instance.getValue(), 0.0);
        }
    }

    /**
     * Test of toString method, of class MutableDouble.
     */
    public void testToString()
    {
        String expected = "0.0";
        MutableDouble instance = new MutableDouble();
        assertEquals(expected, instance.toString());

        for (double value : this.getGoodValues())
        {
            instance.setValue(value);
            expected = new Double(value).toString();
            assertEquals(expected, instance.toString());
        }
    }

    /**
     * Test of convertToVector method, of class MutableDouble.
     */
    public void testConvertToVector()
    {
        MutableDouble instance = new MutableDouble();
        Vector1 result = instance.convertToVector();
        assertEquals(0.0, result.getX(), 0.0);

        double value = this.randomDouble();
        instance.setValue(value);
        assertEquals(0.0, result.getX(), 0.0);

        result = instance.convertToVector();
        assertEquals(value, result.getX(), 0.0);

        result.setX(this.randomDouble());
        assertEquals(value, instance.getValue(), 0.0);
    }

    /**
     * Test of convertFromVector method, of class MutableDouble.
     */
    public void testConvertFromVector()
    {
        MutableDouble instance = new MutableDouble();

        Vector vector = VectorFactory.getDefault().createVector(1);
        double value = 0.0;
        instance.convertFromVector(vector);
        assertEquals(value, instance.getValue(), 0.0);

        value = this.randomDouble();
        vector.setElement(0, value);
        instance.convertFromVector(vector);
        assertEquals(value, instance.getValue(), 0.0);

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
        double value = 0.0;
        double scale = this.randomDouble();
        double expected = 0.0;
        MutableDouble instance = new MutableDouble();
        expected = value * scale;
        instance.scaleEquals(scale);
        assertEquals(expected, instance.getValue(), 0.0);

        value = this.randomDouble();
        scale = this.randomDouble();
        instance.setValue(value);
        expected = value * scale;
        instance.scaleEquals(scale);
        assertEquals(expected, instance.getValue(), 0.0);

        for (int i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            scale = this.randomDouble();
            expected *= scale;
            instance.scaleEquals(scale);
            assertEquals(expected, instance.getValue(), 0.0);
        }

    }

    @Override
    public void testPlusEquals()
    {
        double value = 0.0;
        double otherValue = this.randomDouble();
        double expected = 0.0;
        MutableDouble instance = new MutableDouble();
        expected = value + otherValue;
        instance.plusEquals(new MutableDouble(otherValue));
        assertEquals(expected, instance.getValue(), 0.0);

        value = this.randomDouble();
        otherValue = this.randomDouble();
        instance.setValue(value);
        expected = value + otherValue;
        instance.plusEquals(new MutableDouble(otherValue));
        assertEquals(expected, instance.getValue(), 0.0);

        for (int i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            otherValue = this.randomDouble();
            MutableDouble other = new MutableDouble(otherValue);
            expected += otherValue;
            instance.plusEquals(other);
            assertEquals(expected, instance.getValue(), 0.0);
            assertEquals(otherValue, other.getValue(), 0.0);
        }
    }

    @Override
    public void testDotTimesEquals()
    {
        double value = 0.0;
        double otherValue = this.randomDouble();
        double expected = 0.0;
        MutableDouble instance = new MutableDouble();
        expected = value * otherValue;
        instance.dotTimesEquals(new MutableDouble(otherValue));
        assertEquals(expected, instance.getValue(), 0.0);

        value = this.randomDouble();
        otherValue = this.randomDouble();
        instance.setValue(value);
        expected = value * otherValue;
        instance.dotTimesEquals(new MutableDouble(otherValue));
        assertEquals(expected, instance.getValue(), 0.0);

        for (int i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            otherValue = this.randomDouble();
            MutableDouble other = new MutableDouble(otherValue);
            expected *= otherValue;
            instance.dotTimesEquals(other);
            assertEquals(expected, instance.getValue(), 0.0);
            assertEquals(otherValue, other.getValue(), 0.0);
        }
    }

    /**
     * Test of zero method, of class gov.sandia.isrc.math.Ring.
     */
    @Override
    public void testZero()
    {

        System.out.println( "zero" );
        MutableDouble r1 = this.createRandom();
        MutableDouble r1clone = r1.clone();
        MutableDouble r2 = r1.clone();
        MutableDouble r2clone = r2.clone();

        assertEquals( r1.value, r2.value );
        r1.zero();
        assertFalse( r1.equals( r1clone ) );
        assertEquals( r1.value, r2.scale( 0.0 ).value, 0.0);
        assertEquals( r2.value, r2clone.value );

    }

}