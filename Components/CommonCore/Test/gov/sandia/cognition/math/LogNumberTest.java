/*
 * File:            LogNumberTest.java
 * Authors:         Justin Basilico
 * Project:         Community Foundry
 * 
 * Copyright 2011 Justin Basilico. All rights reserved.
 */

package gov.sandia.cognition.math;

/**
 * Unit tests for class LogNumber
 *
 * @author Justin Basilico
 * @since 3.4.0
 */
public class LogNumberTest
    extends RingTestHarness<LogNumber>
{

    /** Tolerance for numerical precision. */
    protected double epsilon = 1e-10;

    /**
     * Creates a new test.
     *
     * @param   name The test name.
     */
    public LogNumberTest(
        String name)
    {
        super(name);
    }

    /**
     * Creates a random double in the range of the test.
     *
     * @return
     *      A random double for the test.
     */
    protected double randomDouble()
    {
        return (this.RANDOM.nextDouble() - 0.5) * this.RANGE;
    }

    /**
     * Gets the good values that the object can take.
     *
     * @return
     *      The good values the object can take.
     */
    protected double[] getGoodValues()
    {
        return new double[] { 1.0, 0.0, 0.1, 3.14, Math.PI, Math.E, 10.0,
            Double.POSITIVE_INFINITY,
            Double.MIN_VALUE, this.randomDouble(),
            this.randomDouble(), this.randomDouble(), this.randomDouble(),
            this.RANDOM.nextGaussian(), this.RANDOM.nextGaussian(),
            this.RANDOM.nextGaussian(), this.RANDOM.nextGaussian(),
            -1.0, -0.1, -3.14, -10.0, -Math.PI, -Math.E,
            Double.MAX_VALUE, -Double.MAX_VALUE, Double.MIN_NORMAL,
            Double.NEGATIVE_INFINITY,
            -Double.MIN_VALUE, -RANDOM.nextDouble(), -this.randomDouble()};
    }

    @Override
    protected LogNumber createRandom()
    {
        return LogNumber.createFromValue(this.randomDouble());
    }


    /**
     * Test of createFromValue method, of class LogNumber.
     */
    public void testCreateFromValue()
    {
        double value = 0.0;
        LogNumber instance = LogNumber.createFromValue(value);
        assertEquals(Math.log(value), instance.logValue, 0.0);
        assertEquals(value, instance.getValue(), epsilon);

        for (double goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance = LogNumber.createFromValue(value);
            assertEquals(value < 0.0, instance.negative);
            assertEquals(Math.log(Math.abs(value)), instance.logValue, 0.0);
            assertEquals(value, instance.getValue(), epsilon * Math.abs(goodValue));
        }
    }

    /**
     * Test of createFromLogValue method, of class LogNumber.
     */
    public void testCreateFromLogValue()
    {
        boolean negative = false;
        double logValue = 0.0;
        LogNumber instance = LogNumber.createFromLogValue(
            negative, logValue);
        assertEquals(logValue, instance.logValue, 0.0);
        assertEquals(logValue, instance.getLogValue(), epsilon);

        for (double goodValue : this.getGoodValues())
        {
            negative = false;
            logValue = goodValue;

            instance = LogNumber.createFromLogValue(logValue);
            assertEquals(negative, instance.negative);
            assertEquals(logValue, instance.logValue, 0.0);

            instance = LogNumber.createFromLogValue(negative, logValue);
            assertEquals(negative, instance.negative);
            assertEquals(logValue, instance.logValue, 0.0);
            
            negative = true;
            instance = LogNumber.createFromLogValue(negative, logValue);
            assertEquals(negative, instance.negative);
            assertEquals(logValue, instance.logValue, 0.0);
        }
    }

    /**
     * Test of equals method, of class LogNumber.
     */
    public void testEquals()
    {
        for (double x : this.getGoodValues())
        {
            LogNumber instance = LogNumber.createFromValue(x);
            assertTrue(instance.equals(instance));
            assertTrue(instance.equals(LogNumber.createFromValue(x)));
            assertTrue(instance.equals(LogNumber.createFromValue(x), 0.0));
            assertFalse(instance.equals(null));

            for (double y : this.getGoodValues())
            {
                LogNumber other = LogNumber.createFromValue(y);
                boolean expected = (x == y);
                assertEquals(expected, instance.equals(other));
                assertEquals(expected, other.equals(instance));
            }

            for (int i = 0; i < 10; i++)
            {
                double effectiveZero = RANDOM.nextDouble();
                double y = x + (RANDOM.nextDouble() - 0.5) * 2.0;
                boolean expected = (Math.abs(x - y) <= effectiveZero)
                    || (x == y);
                LogNumber other = LogNumber.createFromValue(y);

                assertEquals(expected, instance.equals(other, effectiveZero));
                assertEquals(expected, other.equals(instance, effectiveZero));
            }
        }
    }

    /**
     * Test of compareTo method, of class LogNumber.
     */
    public void testCompareTo()
    {
        for (double x : this.getGoodValues())
        {
            LogNumber instance = LogNumber.createFromValue(x);
            assertEquals(0, instance.compareTo(instance));
            assertEquals(0, instance.compareTo(LogNumber.createFromValue(x)));

            for (double y : this.getGoodValues())
            {
                LogNumber other = LogNumber.createFromValue(y);
                int expected = Double.compare(x, y);
                assertEquals(expected, instance.compareTo(other));
                assertEquals(-expected, other.compareTo(instance));
            }
        }
    }

    @Override
    public void testPlusEquals()
    {
        double value = 0.0;
        double otherValue = this.randomDouble();
        double expected = 0.0;
        LogNumber instance = new LogNumber();
        expected = value + otherValue;
        instance.plusEquals(LogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        value = this.randomDouble();
        otherValue = this.randomDouble();
        instance.setValue(value);
        expected = value + otherValue;
        instance.plusEquals(LogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        for (int i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            otherValue = this.randomDouble();
            LogNumber other = LogNumber.createFromValue(otherValue);
            expected += otherValue;
            instance.plusEquals(other);
            assertEquals(expected, instance.getValue(), epsilon);
            assertEquals(otherValue, other.getValue(), epsilon);
        }


        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                instance = LogNumber.createFromValue(x);
                LogNumber other = LogNumber.createFromValue(y);
                expected = x + y;
                instance.plusEquals(other);
                assertEquals(expected, instance.getValue(), epsilon * Math.abs(expected));
                assertEquals(y, other.getValue(), epsilon * Math.abs(y));
            }
        }
    }


    @Override
    public void testMinusEquals()
    {
        super.testMinusEquals();
        
        double value = 0.0;
        double otherValue = this.randomDouble();
        double expected = 0.0;
        LogNumber instance = new LogNumber();
        expected = value - otherValue;
        instance.minusEquals(LogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        value = this.randomDouble();
        otherValue = this.randomDouble();
        instance.setValue(value);
        expected = value - otherValue;
        instance.minusEquals(LogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        for (int i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            otherValue = this.randomDouble();
            LogNumber other = LogNumber.createFromValue(otherValue);
            expected -= otherValue;
            instance.minusEquals(other);
            assertEquals(expected, instance.getValue(), epsilon);
            assertEquals(otherValue, other.getValue(), epsilon);
        }

        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                instance = LogNumber.createFromValue(x);
                LogNumber other = LogNumber.createFromValue(y);
                expected = x - y;
                instance.minusEquals(other);
                assertEquals(expected, instance.getValue(), epsilon * Math.abs(expected));
                assertEquals(y, other.getValue(), epsilon * Math.abs(y));
            }
        }
    }

    public void testTimes()
    {
        double value = 0.0;
        double otherValue = this.randomDouble();
        double expected = 0.0;
        LogNumber instance = new LogNumber();
        LogNumber result = null;
        expected = value * otherValue;
        result = instance.times(LogNumber.createFromValue(otherValue));
        assertEquals(expected, result.getValue(), epsilon);

        value = this.randomDouble();
        otherValue = this.randomDouble();
        instance.setValue(value);
        expected = value * otherValue;
        result = instance.times(LogNumber.createFromValue(otherValue));
        assertEquals(expected, result.getValue(), epsilon);

        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                instance = LogNumber.createFromValue(x);
                LogNumber other = LogNumber.createFromValue(y);
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
        LogNumber instance = new LogNumber();
        expected = value * otherValue;
        instance.timesEquals(LogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        value = this.randomDouble();
        otherValue = this.randomDouble();
        instance.setValue(value);
        expected = value * otherValue;
        instance.timesEquals(LogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        for (int i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            otherValue = this.randomDouble();
            LogNumber other = LogNumber.createFromValue(otherValue);
            expected *= otherValue;
            instance.timesEquals(other);
            assertEquals(expected, instance.getValue(), epsilon * Math.abs(expected));
            assertEquals(otherValue, other.getValue(), epsilon * Math.abs(otherValue));
        }

        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                instance = LogNumber.createFromValue(x);
                LogNumber other = LogNumber.createFromValue(y);
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
        LogNumber instance = new LogNumber();
        LogNumber result = null;
        expected = value / otherValue;
        result = instance.divide(LogNumber.createFromValue(otherValue));
        assertEquals(expected, result.getValue(), epsilon);

        value = this.randomDouble();
        otherValue = this.randomDouble();
        instance.setValue(value);
        expected = value / otherValue;
        result = instance.divide(LogNumber.createFromValue(otherValue));
        assertEquals(expected, result.getValue(), epsilon);

        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                instance = LogNumber.createFromValue(x);
                LogNumber other = LogNumber.createFromValue(y);
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
        LogNumber instance = new LogNumber();
        expected = value / otherValue;
        instance.divideEquals(LogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        value = this.randomDouble();
        otherValue = this.randomDouble();
        instance.setValue(value);
        expected = value / otherValue;
        instance.divideEquals(LogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        for (int i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            otherValue = this.randomDouble();
            LogNumber other = LogNumber.createFromValue(otherValue);
            expected /= otherValue;
            instance.divideEquals(other);
            assertEquals(expected, instance.getValue(), epsilon * Math.abs(expected));
            assertEquals(otherValue, other.getValue(), epsilon * Math.abs(otherValue));
        }

        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                instance = LogNumber.createFromValue(x);
                LogNumber other = LogNumber.createFromValue(y);
                expected = x / y;
                instance.divideEquals(other);
                assertEquals(expected, instance.getValue(), epsilon * Math.abs(expected));
                assertEquals(y, other.getValue(), epsilon * Math.abs(y));
            }
        }
    }

    @Override
    public void testDotTimes()
    {
        super.testDotTimes();
        
        double value = 0.0;
        double otherValue = this.randomDouble();
        double expected = 0.0;
        LogNumber instance = new LogNumber();
        LogNumber result = null;
        expected = value * otherValue;
        result = instance.dotTimes(LogNumber.createFromValue(otherValue));
        assertEquals(expected, result.getValue(), epsilon);

        value = this.randomDouble();
        otherValue = this.randomDouble();
        instance.setValue(value);
        expected = value * otherValue;
        result = instance.dotTimes(LogNumber.createFromValue(otherValue));
        assertEquals(expected, result.getValue(), epsilon);

        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                instance = LogNumber.createFromValue(x);
                LogNumber other = LogNumber.createFromValue(y);
                expected = x * y;
                result = instance.dotTimes(other);

                assertEquals(expected, result.getValue(), epsilon * Math.abs(expected));
                assertEquals(y, other.getValue(), epsilon * Math.abs(y));
            }
        }
    }

    @Override
    public void testDotTimesEquals()
    {        
        double value = 0.0;
        double otherValue = this.randomDouble();
        double expected = 0.0;
        LogNumber instance = new LogNumber();
        expected = value * otherValue;
        instance.dotTimesEquals(LogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        value = this.randomDouble();
        otherValue = this.randomDouble();
        instance.setValue(value);
        expected = value * otherValue;
        instance.dotTimesEquals(LogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        for (int i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            otherValue = this.randomDouble();
            LogNumber other = LogNumber.createFromValue(otherValue);
            expected *= otherValue;
            instance.dotTimesEquals(other);
            assertEquals(expected, instance.getValue(), epsilon * Math.abs(expected));
            assertEquals(otherValue, other.getValue(), epsilon * Math.abs(otherValue));
        }

        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                instance = LogNumber.createFromValue(x);
                LogNumber other = LogNumber.createFromValue(y);
                expected = x * y;
                instance.dotTimesEquals(other);
                assertEquals(expected, instance.getValue(), epsilon * Math.abs(expected));
                assertEquals(y, other.getValue(), epsilon * Math.abs(y));
            }
        }
    }

    @Override
    public void testScale()
    {
        super.testScale();
        
        double value = 0.0;
        double otherValue = this.randomDouble();
        double expected = 0.0;
        LogNumber instance = new LogNumber();
        LogNumber result = null;
        expected = value * otherValue;
        result = instance.scale(otherValue);
        assertEquals(expected, result.getValue(), epsilon);

        value = this.randomDouble();
        otherValue = this.randomDouble();
        instance.setValue(value);
        expected = value * otherValue;
        result = instance.scale(otherValue);
        assertEquals(expected, result.getValue(), epsilon);

        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                instance = LogNumber.createFromValue(x);
                expected = x * y;
                result = instance.scale(y);
                assertEquals(expected, result.getValue(), epsilon * Math.abs(expected));
            }
        }
    }

    @Override
    public void testScaleEquals()
    {        
        double value = 0.0;
        double otherValue = this.randomDouble();
        double expected = 0.0;
        LogNumber instance = new LogNumber();
        expected = value * otherValue;
        instance.scaleEquals(otherValue);
        assertEquals(expected, instance.getValue(), epsilon);

        value = this.randomDouble();
        otherValue = this.randomDouble();
        instance.setValue(value);
        expected = value * otherValue;
        instance.scaleEquals(otherValue);
        assertEquals(expected, instance.getValue(), epsilon);

        for (int i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            otherValue = this.randomDouble();
            expected *= otherValue;
            instance.scaleEquals(otherValue);
            assertEquals(expected, instance.getValue(), epsilon * Math.abs(expected));
        }

        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                instance = LogNumber.createFromValue(x);
                expected = x * y;
                instance.scaleEquals(y);
                assertEquals(expected, instance.getValue(), epsilon * Math.abs(expected));
            }
        }
    }

    /**
     * Test of negative method, of class LogNumber.
     */
    @Override
    public void testNegative()
    {
        super.testNegative();

        double value = 0.0;
        LogNumber instance = new LogNumber();
        double expected = -value;
        LogNumber result = instance.negative();
        assertNotSame(result, instance);
        assertEquals(expected, result.getValue(), epsilon);
        assertEquals(value, instance.getValue(), epsilon);

        for (double goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            expected = -value;
            result = instance.negative();
            assertNotSame(result, instance);
            assertEquals(expected, result.getValue(), epsilon * Math.abs(goodValue));
            assertEquals(value, instance.getValue(), epsilon * Math.abs(goodValue));
        }
    }

    /**
     * Test of negativeEquals method, of class LogNumber.
     */
    @Override
    public void testNegativeEquals()
    {
        super.testNegativeEquals();
        
        double value = 0.0;
        LogNumber instance = new LogNumber();
        double expected = -value;
        instance.negativeEquals();
        assertEquals(expected, instance.getValue(), epsilon);

        for (double goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            expected = -value;
            instance.negativeEquals();
            assertEquals(expected, instance.getValue(), epsilon * Math.abs(goodValue));
        }
    }

    /**
     * Test of absoluteValue method, of class LogNumber.
     */
    public void testAbsoluteValue()
    {
        double value = 0.0;
        LogNumber instance = new LogNumber();
        double expected = Math.abs(value);
        LogNumber result = instance.absoluteValue();
        assertNotSame(result, instance);
        assertEquals(expected, result.getValue(), epsilon);
        assertEquals(value, instance.getValue(), epsilon);
        assertTrue(!result.negative);

        for (double goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            expected = Math.abs(value);
            result = instance.absoluteValue();
            assertNotSame(result, instance);
            assertEquals(expected, result.getValue(), epsilon * Math.abs(goodValue));
            assertEquals(value, instance.getValue(), epsilon * Math.abs(goodValue));
            assertTrue(!result.negative);
        }
    }

    /**
     * Test of absoluteValueEquals method, of class LogNumber.
     */
    public void testAbsoluteValueEquals()
    {
        double value = 0.0;
        LogNumber instance = new LogNumber();
        double expected = Math.abs(value);
        instance.absoluteValueEquals();
        assertEquals(expected, instance.getValue(), epsilon);
        assertTrue(!instance.negative);

        for (double goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            expected = Math.abs(value);
            instance.absoluteValueEquals();
            assertEquals(expected, instance.getValue(), epsilon * Math.abs(goodValue));
            assertTrue(!instance.negative);
        }
    }


    /**
     * Test of power method, of class LogNumber.
     */
    public void testPower()
    {
        LogNumber instance = new LogNumber();

//        double[] goodPowers = { 1.0, 2.0, 2.5, -1, -2};
        for (double goodValue : this.getGoodValues())
        {
            for (double power : this.getGoodValues())
            {
                double value = goodValue;
                instance.setValue(value);
                double expected = Math.pow(value, power);
                LogNumber result = instance.power(power);
System.out.println("value: " + value + " power: " + power + " expected: " + expected + " was " + result + " = " + result.getValue() + " from " + instance);
                assertEquals(expected, result.getValue(), epsilon * Math.abs(expected));
            }
        }
    }

    /**
     * Test of powerEquals method, of class LogNumber.
     */
    public void testPowerEquals()
    {
        LogNumber instance = new LogNumber();

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
     * Test of min method, of class LogNumber.
     */
    public void testMin()
    {
        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                LogNumber instance = LogNumber.createFromValue(x);
                LogNumber other = LogNumber.createFromValue(y);
                double expectedValue = Math.min(x, y);
                LogNumber expected = new Double(expectedValue).equals(x) ? instance : other;
                LogNumber result = instance.min(other);
                assertEquals(expected, result);
                assertNotSame(expected, result);
                assertEquals(expectedValue, result.getValue(), epsilon * Math.abs(expectedValue));
                assertEquals(x, instance.getValue(), epsilon * Math.abs(x));
                assertEquals(y, other.getValue(), epsilon * Math.abs(y));
            }
        }
    }

    /**
     * Test of minEquals method, of class LogNumber.
     */
    public void testMinEquals()
    {
        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                LogNumber instance = LogNumber.createFromValue(x);
                LogNumber other = LogNumber.createFromValue(y);
                double expectedValue = Math.min(x, y);
                LogNumber expected = expectedValue == x ? instance : other;
                instance.minEquals(other);
                assertEquals(expected, instance);
                assertEquals(expectedValue, instance.getValue(), epsilon * Math.abs(expectedValue));
                assertEquals(y, other.getValue(), epsilon * Math.abs(y));
            }
        }
    }

    /**
     * Test of max method, of class LogNumber.
     */
    public void testMax()
    {
        for (double x : this.getGoodValues())
        {
            for (double y : this.getGoodValues())
            {
                LogNumber instance = LogNumber.createFromValue(x);
                LogNumber other = LogNumber.createFromValue(y);
                double expectedValue = Math.max(x, y);
                LogNumber expected = new Double(expectedValue).equals(x) ? instance : other;
                LogNumber result = instance.max(other);
                assertEquals(expected, result);
                assertNotSame(expected, result);
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
                LogNumber instance = LogNumber.createFromValue(x);
                LogNumber other = LogNumber.createFromValue(y);
                double expectedValue = Math.max(x, y);
                LogNumber expected = expectedValue == x ? instance : other;
                instance.maxEquals(other);
                assertEquals(expected, instance);
                assertEquals(expectedValue, instance.getValue(), epsilon * Math.abs(expectedValue));
                assertEquals(y, other.getValue(), epsilon * Math.abs(y));
            }
        }
    }

    /**
     * Test of intValue method, of class LogNumber.
     */
    public void testIntValue()
    {
        double value = 0.0;
        LogNumber instance = new LogNumber();
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
     * Test of longValue method, of class LogNumber.
     */
    public void testLongValue()
    {
        double value = 0.0;
        LogNumber instance = new LogNumber();
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
     * Test of floatValue method, of class LogNumber.
     */
    public void testFloatValue()
    {
        double value = 0.0;
        LogNumber instance = new LogNumber();
        float expected = (float) value;
        assertEquals(expected, instance.floatValue(), epsilon);

        for (double goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            expected = (float) value;
            assertEquals(expected, instance.floatValue(), epsilon * Math.abs(goodValue));
        }
    }

    /**
     * Test of doubleValue method, of class LogNumber.
     */
    public void testDoubleValue()
    {
        double value = 0.0;
        LogNumber instance = new LogNumber();
        assertEquals(value, instance.doubleValue(), epsilon);

        for (double goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            assertEquals(value, instance.doubleValue(), epsilon * Math.abs(goodValue));
        }

    }

    /**
     * Test of getValue method, of class LogNumber.
     */
    public void testGetValue()
    {
        this.testSetValue();
    }

    /**
     * Test of setValue method, of class LogNumber.
     */
    public void testSetValue()
    {
        double value = 0.0;
        LogNumber instance = new LogNumber();
        assertEquals(Math.log(value), instance.logValue, 0.0);
        assertEquals(value, instance.getValue(), epsilon);

        for (double goodValue : this.getGoodValues())
        {
            value = goodValue;
            instance.setValue(value);
            assertEquals(value < 0.0, instance.negative);
            assertEquals(Math.log(Math.abs(value)), instance.logValue, 0.0);
            assertEquals(value, instance.getValue(), epsilon * Math.abs(goodValue));
        }
    }

    public void testIsNegative()
    {
        this.testSetNegative();
    }

    public void testSetNegative()
    {

        double value = 0.0;
        boolean negative = false;
        LogNumber instance = new LogNumber();
        assertEquals(negative, instance.isNegative());

        for (double goodValue : this.getGoodValues())
        {
            value = goodValue;
            negative = value < 0.0;
            instance.setValue(value);
            assertEquals(negative, instance.isNegative());

            negative = false;
            instance.setNegative(negative);
            assertEquals(negative, instance.isNegative());
            
            negative = true;
            instance.setNegative(negative);
            assertEquals(negative, instance.isNegative());
        }
    }

    /**
     * Test of getLogValue method, of class LogNumber.
     */
    public void testGetLogValue()
    {
        this.testSetLogValue();
    }

    /**
     * Test of setLogValue method, of class LogNumber.
     */
    public void testSetLogValue()
    {
        double value = 0.0;
        double logValue = Math.log(value);
        LogNumber instance = new LogNumber();
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