/*
 * File:                LogNumberTest.java
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
 * Unit tests for class LogNumber.
 * 
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class LogNumberTest
    extends RingTestHarness<LogNumber>
{
    protected double epsilon = 1e-10;
    
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public LogNumberTest(
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
    protected LogNumber createRandom()
    {
        return new LogNumber(this.randomDouble());
    }

    @Override
    public void testScale()
    {
        System.out.println( "scale" );

        // This test assumes that scaleEquals has been tested and verified
        double scaleFactor = RANDOM.nextDouble() * RANGE;
        LogNumber r1 = this.createRandom();
        LogNumber r1clone = r1.clone();

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
        LogNumber instance = LogNumber.createFromValue(value);
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
    }

    @Override
    public void testMinusEquals()
    {
        double value = this.randomDouble();
        double otherValue = 0.0;
        double expected = 0.0;
        LogNumber instance = LogNumber.createFromValue(value);
        expected = value - otherValue;
        instance.minusEquals(LogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        value = this.randomDouble();
        otherValue = RANDOM.nextDouble() * value;
        instance.setValue(value);
        expected = value - otherValue;
        instance.minusEquals(LogNumber.createFromValue(otherValue));
        assertEquals(expected, instance.getValue(), epsilon);

        for (int i = 0; i < 1 + RANDOM.nextInt(10); i++)
        {
            value = this.randomDouble();
            instance.setValue(value);
            otherValue = RANDOM.nextDouble() * value;
            LogNumber other = LogNumber.createFromValue(otherValue);
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
            value = this.randomDouble();
            instance.setValue(value);
            otherValue = this.randomDouble();
            LogNumber other = LogNumber.createFromValue(otherValue);
            expected = value * otherValue;
            instance.dotTimesEquals(other);
            assertEquals(expected, instance.getValue(), epsilon);
            assertEquals(otherValue, other.getValue(), epsilon);
        }
    }

    @Override
    public void testNegativeEquals()
    {

        System.out.println( "negativeEquals" );

        // This test assumes that scaleEquals() has been tested and verified
        LogNumber r1 = this.createRandom();
        r1.negativeEquals();
        assertTrue(Double.isNaN(r1.getLogValue()));
    }

    @Override
    public void testDotTimes()
    {
        System.out.println( "dotTimes" );

        // This test assumes that dotTimesEquals has been tested and verified
        LogNumber r1 = this.createRandom();
        LogNumber r1clone = r1.clone();

        LogNumber r2 = r1.scale( RANDOM.nextDouble() * RANGE );
        LogNumber r2clone = r2.clone();

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

    @Override
    public void testMinus()
    {
        System.out.println( "minus" );


        // This test assumes that plus and scale have been tested and verified
        LogNumber r1 = this.createRandom();
        LogNumber r1clone = r1.clone();

        LogNumber r2 = r1.scale( RANDOM.nextDouble() * RANGE);
        LogNumber r2clone = r2.clone();

        assertTrue( r1.minus( r2 ).equals( r1.plus( r2.scale( -1.0 ) ), TOLERANCE ) );

        // This makes sure that the minus operator didn't modify the result
        assertEquals( r1, r1clone );
        assertEquals( r2, r2clone );

        // Make sure minus and minusEquals return the same value
        // and that the r2 doesn't get modified by the methods
        r1.minusEquals( r2 );
        assertEquals( r2clone, r2 );
        assertEquals( r1, r1clone.minus( r2 ) );
        assertEquals( r2clone, r2 );

        // Self minus should equal zero-ing
        r2.minusEquals( r2 );
        r1.zero();
        assertEquals( r1, r2 );

        try
        {
            r1.minus( null );
            fail( "Should have thrown null-pointer exception: minus() " + r1.getClass() );
        }
        catch (NullPointerException e)
        {
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
            assertEquals(expected, instance.floatValue(), epsilon);
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
            assertEquals(value, instance.doubleValue(), epsilon);
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
            assertEquals(Math.log(value), instance.logValue, 0.0);
            assertEquals(value, instance.getValue(), epsilon);
        }
    }
}
