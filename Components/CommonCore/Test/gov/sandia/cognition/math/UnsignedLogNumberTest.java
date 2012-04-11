/*
 * File:                UnsignedLogNumberTest.java
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

/**
 * Unit tests for class UnsignedLogNumber.
 * 
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class UnsignedLogNumberTest
    extends RingTestHarness<UnsignedLogNumber>
{
    protected double epsilon = 1e-10;
    
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public UnsignedLogNumberTest(
        final String testName)
    {
        super(testName);
    }

    protected double randomDouble()
    {
        return this.RANDOM.nextDouble() * this.RANGE;
    }

    protected double[] getGoodValues()
    {
        return new double[] { 1.0, 0.0, 0.1, 3.14, Math.PI, Math.E,
            Double.POSITIVE_INFINITY,
            Double.MIN_VALUE, this.randomDouble(),
            this.randomDouble(), this.randomDouble(), this.randomDouble() };
    }

    @Override
    protected UnsignedLogNumber createRandom()
    {
        return new UnsignedLogNumber(this.randomDouble());
    }

    public void testConstructors()
    {
        assertEquals(0.0, new UnsignedLogNumber().getValue(), 0.0);

        for (double value : this.getGoodValues())
        {
            double logValue = Math.log(value);
            UnsignedLogNumber instance = new UnsignedLogNumber(logValue);
            assertEquals(logValue, instance.logValue, 0.0);

            instance = new UnsignedLogNumber(instance);
            assertEquals(logValue, instance.logValue, 0.0);
        }
    }

    public void testCreateFromValue()
    {
        for (double value : this.getGoodValues())
        {
            UnsignedLogNumber instance = UnsignedLogNumber.createFromValue(value);
            assertEquals(Math.log(value), instance.logValue, 0.0);
        }
    }

    public void testCreateFromLogValue()
    {
        for (double value : this.getGoodValues())
        {
            double logValue = Math.log(value);
            UnsignedLogNumber instance = UnsignedLogNumber.createFromLogValue(logValue);
            assertEquals(logValue, instance.logValue, 0.0);
        }
    }

    public void testEquals()
    {
        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                boolean expected = new Double(x).equals(new Double(y));
                UnsignedLogNumber instance = UnsignedLogNumber.createFromValue(x);
                UnsignedLogNumber other = UnsignedLogNumber.createFromValue(y);
                assertEquals(expected, instance.equals(other));
                assertEquals(x, instance.getValue(), epsilon * Math.abs(x));
                assertEquals(y, other.getValue(), epsilon * Math.abs(y));
            }
        }
    }

    public void testCompareTo()
    {
        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                int expected = Double.compare(x, y);
                UnsignedLogNumber instance = UnsignedLogNumber.createFromValue(x);
                UnsignedLogNumber other = UnsignedLogNumber.createFromValue(y);
                assertEquals(expected, instance.compareTo(other));
                assertEquals(x, instance.getValue(), epsilon * Math.abs(x));
                assertEquals(y, other.getValue(), epsilon * Math.abs(y));
            }
        }
    }

    @Override
    public void testScale()
    {
        System.out.println( "scale" );

        // This test assumes that scaleEquals has been tested and verified
        double scaleFactor = RANDOM.nextDouble() * RANGE;
        UnsignedLogNumber r1 = this.createRandom();
        UnsignedLogNumber r1clone = r1.clone();

        assertEquals( r1, r1clone );
        r1.scaleEquals( scaleFactor );
        assertTrue( r1.equals( r1clone.scale( scaleFactor ), epsilon ) );
        assertFalse( r1.equals( r1clone ) );

        assertEquals( r1.scale( 2.0 ), r1.plus( r1 ) );

    }

    @Override
    public void testScaleEquals()
    {
        double value = 0.0;
        double scale = this.randomDouble();
        double expected = 0.0;
        double scaledEpsilon = epsilon;
        UnsignedLogNumber instance = UnsignedLogNumber.createFromValue(value);
        expected = value * scale;
        scaledEpsilon *= scale;
        instance.scaleEquals(scale);
        assertEquals(expected, instance.getValue(), scaledEpsilon);

        value = this.randomDouble();
        scale = this.randomDouble();
        instance.setValue(value);
        expected = value * scale;
        scaledEpsilon = epsilon;
        instance.scaleEquals(scale);
        assertEquals(expected, instance.getValue(), scaledEpsilon);

        for (int i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            scale = this.randomDouble();
            expected *= scale;
            scaledEpsilon *= scale;
            instance.scaleEquals(scale);
            assertEquals(expected, instance.getValue(), 1e-3);
        }
    }

    @Override
    public void testPlusEquals()
    {
        double value = 0.0;
        double otherValue = this.randomDouble();
        double expected = 0.0;
        UnsignedLogNumber instance = new UnsignedLogNumber();
        expected = value + otherValue;
        instance.plusEquals(UnsignedLogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        value = this.randomDouble();
        otherValue = this.randomDouble();
        instance.setValue(value);
        expected = value + otherValue;
        instance.plusEquals(UnsignedLogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        for (int i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            otherValue = this.randomDouble();
            UnsignedLogNumber other = UnsignedLogNumber.createFromValue(otherValue);
            expected += otherValue;
            instance.plusEquals(other);
            assertEquals(expected, instance.getValue(), epsilon);
            assertEquals(otherValue, other.getValue(), epsilon);
        }
    }
    
    /**
     * Test of minus method, of class LogNumber.
     */
    @Override
    public void testMinus()
    {
        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                UnsignedLogNumber instance = UnsignedLogNumber.createFromValue(x);
                UnsignedLogNumber other = UnsignedLogNumber.createFromValue(y);
                double expectedValue = (x >= y) ? (x - y) : Double.NaN;
                UnsignedLogNumber result = instance.minus(other);
                assertNotSame(result, instance);
                assertNotSame(result, other);
                assertEquals(expectedValue, result.getValue(), epsilon * Math.abs(expectedValue));
                assertEquals(x, instance.getValue(), epsilon * Math.abs(x));
                assertEquals(y, other.getValue(), epsilon * Math.abs(y));
            }
        }
    }

    @Override
    public void testMinusEquals()
    {
        double value = this.randomDouble();
        double otherValue = 0.0;
        double expected = 0.0;
        UnsignedLogNumber instance = UnsignedLogNumber.createFromValue(value);
        expected = value - otherValue;
        instance.minusEquals(UnsignedLogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        value = this.randomDouble();
        otherValue = RANDOM.nextDouble() * value;
        instance.setValue(value);
        expected = value - otherValue;
        instance.minusEquals(UnsignedLogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        for (int i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            value = this.randomDouble();
            instance.setValue(value);
            otherValue = RANDOM.nextDouble() * value;
            UnsignedLogNumber other = UnsignedLogNumber.createFromValue(otherValue);
            expected = value - otherValue;
            instance.minusEquals(other);
            assertEquals(expected, instance.getValue(), epsilon);
            assertEquals(otherValue, other.getValue(), epsilon);
        }
    }

    @Override
    public void testDotTimesEquals()
    {
        double value = 0.0;
        double otherValue = this.randomDouble();
        double expected = 0.0;
        UnsignedLogNumber instance = new UnsignedLogNumber();
        expected = value * otherValue;
        instance.dotTimesEquals(UnsignedLogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        value = this.randomDouble();
        otherValue = this.randomDouble();
        instance.setValue(value);
        expected = value * otherValue;
        instance.dotTimesEquals(UnsignedLogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        for (int i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            value = this.randomDouble();
            instance.setValue(value);
            otherValue = this.randomDouble();
            UnsignedLogNumber other = UnsignedLogNumber.createFromValue(otherValue);
            expected = value * otherValue;
            instance.dotTimesEquals(other);
            assertEquals(expected, instance.getValue(), epsilon);
            assertEquals(otherValue, other.getValue(), epsilon);
        }
    }

    /**
     * Test of negative method, of class LogNumber.
     */
    @Override
    public void testNegative()
    {
        double value = 0.0;
        UnsignedLogNumber instance = new UnsignedLogNumber();
        UnsignedLogNumber result = instance.negative();
        assertTrue(Double.isNaN(result.getLogValue()));

        for (double goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            result = instance.negative();
            assertNotSame(result, instance);
            assertTrue(Double.isNaN(result.getLogValue()));
            assertEquals(value, instance.getValue(), epsilon * Math.abs(goodValue));
        }
    }

    @Override
    public void testNegativeEquals()
    {

        System.out.println( "negativeEquals" );

        // This test assumes that scaleEquals() has been tested and verified
        UnsignedLogNumber r1 = this.createRandom();
        r1.negativeEquals();
        assertTrue(Double.isNaN(r1.getLogValue()));
    }

    @Override
    public void testDotTimes()
    {
        System.out.println( "dotTimes" );

        // This test assumes that dotTimesEquals has been tested and verified
        UnsignedLogNumber r1 = this.createRandom();
        UnsignedLogNumber r1clone = r1.clone();

        UnsignedLogNumber r2 = r1.scale( RANDOM.nextDouble() * RANGE );
        UnsignedLogNumber r2clone = r2.clone();

        r1.dotTimes( r2 );
        assertEquals( r1, r1clone );
        assertEquals( r2, r2clone );


        r1.dotTimesEquals( r2 );
        assertFalse( r1.equals( r1clone ) );
        assertEquals( r2, r2clone );

        assertTrue( r1.equals( r1clone.dotTimes( r2 ), TOLERANCE ) );

        try
        {
            r1.dotTimes( null );
            fail( "Should have thrown null-pointer exception: dotTimes() " + r1.getClass() );
        }
        catch (NullPointerException e)
        {
        }
    }

    public void testTimes()
    {
        double value = 0.0;
        double otherValue = this.randomDouble();
        double expected = 0.0;
        UnsignedLogNumber instance = new UnsignedLogNumber();
        UnsignedLogNumber result = null;
        expected = value * otherValue;
        result = instance.times(UnsignedLogNumber.createFromValue(otherValue));
        assertEquals(expected, result.getValue(), epsilon);

        value = this.randomDouble();
        otherValue = this.randomDouble();
        instance.setValue(value);
        expected = value * otherValue;
        result = instance.times(UnsignedLogNumber.createFromValue(otherValue));
        assertEquals(expected, result.getValue(), epsilon);

        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                instance = UnsignedLogNumber.createFromValue(x);
                UnsignedLogNumber other = UnsignedLogNumber.createFromValue(y);
                expected = x * y;
                result = instance.times(other);
                assertEquals(expected, result.getValue(), epsilon * Math.abs(expected));
                assertEquals(y, other.getValue(), epsilon * Math.abs(y));
            }
        }
    }

    public void testTimesEquals()
    {
        double value = 0.0;
        double otherValue = this.randomDouble();
        double expected = 0.0;
        UnsignedLogNumber instance = new UnsignedLogNumber();
        expected = value * otherValue;
        instance.timesEquals(UnsignedLogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        value = this.randomDouble();
        otherValue = this.randomDouble();
        instance.setValue(value);
        expected = value * otherValue;
        instance.timesEquals(UnsignedLogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        for (int i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            otherValue = this.randomDouble();
            UnsignedLogNumber other = UnsignedLogNumber.createFromValue(otherValue);
            expected *= otherValue;
            instance.timesEquals(other);
            assertEquals(expected, instance.getValue(), epsilon * Math.abs(expected));
            assertEquals(otherValue, other.getValue(), epsilon * Math.abs(otherValue));
        }

        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                instance = UnsignedLogNumber.createFromValue(x);
                UnsignedLogNumber other = UnsignedLogNumber.createFromValue(y);
                expected = x * y;
                instance.timesEquals(other);
                assertEquals(expected, instance.getValue(), epsilon * Math.abs(expected));
                assertEquals(y, other.getValue(), epsilon * Math.abs(y));
            }
        }
    }

    public void testDivide()
    {
        double value = 0.0;
        double otherValue = this.randomDouble();
        double expected = 0.0;
        UnsignedLogNumber instance = new UnsignedLogNumber();
        UnsignedLogNumber result = null;
        expected = value / otherValue;
        result = instance.divide(UnsignedLogNumber.createFromValue(otherValue));
        assertEquals(expected, result.getValue(), epsilon);

        value = this.randomDouble();
        otherValue = this.randomDouble();
        instance.setValue(value);
        expected = value / otherValue;
        result = instance.divide(UnsignedLogNumber.createFromValue(otherValue));
        assertEquals(expected, result.getValue(), epsilon);

        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                instance = UnsignedLogNumber.createFromValue(x);
                UnsignedLogNumber other = UnsignedLogNumber.createFromValue(y);
                expected = x / y;
                result = instance.divide(other);
                assertEquals(expected, result.getValue(), epsilon * Math.abs(expected));
                assertEquals(y, other.getValue(), epsilon * Math.abs(y));
            }
        }
    }

    public void testDivideEquals()
    {
        double value = 0.0;
        double otherValue = this.randomDouble();
        double expected = 0.0;
        UnsignedLogNumber instance = new UnsignedLogNumber();
        expected = value / otherValue;
        instance.divideEquals(UnsignedLogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        value = this.randomDouble();
        otherValue = this.randomDouble();
        instance.setValue(value);
        expected = value / otherValue;
        instance.divideEquals(UnsignedLogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        for (int i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            otherValue = this.randomDouble();
            UnsignedLogNumber other = UnsignedLogNumber.createFromValue(otherValue);
            expected /= otherValue;
            instance.divideEquals(other);
            assertEquals(expected, instance.getValue(), epsilon * Math.abs(expected));
            assertEquals(otherValue, other.getValue(), epsilon * Math.abs(otherValue));
        }

        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                instance = UnsignedLogNumber.createFromValue(x);
                UnsignedLogNumber other = UnsignedLogNumber.createFromValue(y);
                expected = x / y;
                instance.divideEquals(other);
                assertEquals(expected, instance.getValue(), epsilon * Math.abs(expected));
                assertEquals(y, other.getValue(), epsilon * Math.abs(y));
            }
        }
    }

    /**
     * Test of min method, of class UnsignedLogNumber.
     */
    public void testMin()
    {
        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                UnsignedLogNumber instance = UnsignedLogNumber.createFromValue(x);
                UnsignedLogNumber other = UnsignedLogNumber.createFromValue(y);
                double expectedValue = Math.min(x, y);
                UnsignedLogNumber expected = expectedValue == x ? instance : other;
                UnsignedLogNumber result = instance.min(other);
                assertNotSame(expected, result);
                assertEquals(expected, result);
                assertEquals(expectedValue, result.getValue(), epsilon * Math.abs(expectedValue));
                assertEquals(x, instance.getValue(), epsilon * Math.abs(x));
                assertEquals(y, other.getValue(), epsilon * Math.abs(y));
            }
        }
    }

    /**
     * Test of minEquals method, of class UnsignedLogNumber.
     */
    public void testMinEquals()
    {
        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                UnsignedLogNumber instance = UnsignedLogNumber.createFromValue(x);
                UnsignedLogNumber other = UnsignedLogNumber.createFromValue(y);
                double expectedValue = Math.min(x, y);
                UnsignedLogNumber expected = expectedValue == x ? instance : other;
                instance.minEquals(other);
                assertEquals(expected, instance);
                assertEquals(expectedValue, instance.getValue(), epsilon * Math.abs(expectedValue));
                assertEquals(y, other.getValue(), epsilon * Math.abs(y));
            }
        }
    }

    /**
     * Test of max method, of class UnsignedLogNumber.
     */
    public void testMax()
    {
        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                UnsignedLogNumber instance = UnsignedLogNumber.createFromValue(x);
                UnsignedLogNumber other = UnsignedLogNumber.createFromValue(y);
                double expectedValue = Math.max(x, y);
                UnsignedLogNumber expected = expectedValue == x ? instance : other;
                UnsignedLogNumber result = instance.max(other);
                assertNotSame(expected, result);
                assertEquals(expected, result);
                assertEquals(expectedValue, result.getValue(), epsilon * Math.abs(expectedValue));
                assertEquals(x, instance.getValue(), epsilon * Math.abs(x));
                assertEquals(y, other.getValue(), epsilon * Math.abs(y));
            }
        }
    }

    /**
     * Test of maxEquals method, of class LogNumber.
     */
    public void testMaxEquals()
    {
        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                UnsignedLogNumber instance = UnsignedLogNumber.createFromValue(x);
                UnsignedLogNumber other = UnsignedLogNumber.createFromValue(y);
                double expectedValue = Math.max(x, y);
                UnsignedLogNumber expected = expectedValue == x ? instance : other;
                instance.maxEquals(other);
                assertEquals(expected, instance);
                assertEquals(expectedValue, instance.getValue(), epsilon * Math.abs(expectedValue));
                assertEquals(y, other.getValue(), epsilon * Math.abs(y));
            }
        }
    }

    /**
     * Test of power method, of class UnsignedLogNumber.
     */
    public void testPower()
    {
        UnsignedLogNumber instance = new UnsignedLogNumber();

        for (double goodValue : this.getGoodValues())
        {
            for (double power : this.getGoodValues())
            {
                double value = goodValue;
                instance.setValue(value);
                double expected = Math.pow(value, power);
                UnsignedLogNumber result = instance.power(power);
                assertEquals(expected, result.getValue(), epsilon * Math.abs(expected));
            }
        }
    }

    /**
     * Test of powerEquals method, of class UnsignedLogNumber.
     */
    public void testPowerEquals()
    {
        UnsignedLogNumber instance = new UnsignedLogNumber();

        for (double goodValue : this.getGoodValues())
        {
            for (double power : this.getGoodValues())
            {
                double value = goodValue;
                instance.setValue(value);
                double expected = Math.pow(value, power);
                instance.powerEquals(power);
                assertEquals(expected, instance.getValue(), epsilon * Math.abs(expected));
            }
        }
    }

    /**
     * Test of intValue method, of class UnsignedLogNumber.
     */
    public void testIntValue()
    {
        double value = 0.0;
        UnsignedLogNumber instance = new UnsignedLogNumber();
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
     * Test of longValue method, of class UnsignedLogNumber.
     */
    public void testLongValue()
    {
        double value = 0.0;
        UnsignedLogNumber instance = new UnsignedLogNumber();
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
     * Test of floatValue method, of class UnsignedLogNumber.
     */
    public void testFloatValue()
    {
        double value = 0.0;
        UnsignedLogNumber instance = new UnsignedLogNumber();
        float expected = (float) value;
        assertEquals(expected, instance.floatValue(), epsilon);

        for (double goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            expected = (float) value;
            assertEquals(expected, instance.floatValue(), epsilon);
        }
    }

    /**
     * Test of doubleValue method, of class UnsignedLogNumber.
     */
    public void testDoubleValue()
    {
        double value = 0.0;
        UnsignedLogNumber instance = new UnsignedLogNumber();
        assertEquals(value, instance.doubleValue(), epsilon);

        for (double goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            assertEquals(value, instance.doubleValue(), epsilon);
        }

    }

    /**
     * Test of getValue method, of class UnsignedLogNumber.
     */
    public void testGetValue()
    {
        this.testSetValue();
    }

    /**
     * Test of setValue method, of class UnsignedLogNumber.
     */
    public void testSetValue()
    {
        double value = 0.0;
        UnsignedLogNumber instance = new UnsignedLogNumber();
        assertEquals(Math.log(value), instance.logValue, 0.0);
        assertEquals(value, instance.getValue(), epsilon);

        for (double goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            assertEquals(Math.log(value), instance.logValue, 0.0);
            assertEquals(value, instance.getValue(), epsilon);
        }
    }

    /**
     * Test of getLogValue method, of class UnsignedLogNumber.
     */
    public void testGetLogValue()
    {
        this.testSetLogValue();
    }

    /**
     * Test of setLogValue method, of class UnsignedLogNumber.
     */
    public void testSetLogValue()
    {
        double value = 0.0;
        double logValue = Math.log(value);
        UnsignedLogNumber instance = new UnsignedLogNumber();
        assertEquals(logValue, instance.getLogValue(), 0.0);

        for (double goodValue : this.getGoodValues())
        {
            value = goodValue;
            logValue = Math.log(value);
            instance.setLogValue(logValue);
            assertEquals(logValue, instance.getLogValue(), 0.0);
        }
    }
}
