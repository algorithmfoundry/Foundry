/*
 * File:            FieldTestHarness.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Common Core
 * 
 * Copyright 2013, Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.math;

/**
 * Test harness for interface {@link Field}.
 *
 * @param   <RingType>
 *      The type of ring to test.
 * @author  Justin Basilico
 * @version 3.3.3
 */
public abstract class EuclideanRingTestHarness<RingType extends EuclideanRing<RingType>>
    extends RingTestHarness<RingType>
{

    /**
     * Creates a new test harness.
     */
    public EuclideanRingTestHarness(
        final String testName)
    {
        super(testName);
    }

    protected abstract RingType createRandom();
    
    public abstract void testTimesEquals();
    public abstract void testDivideEquals();



    public void testTimes()
    {
        System.out.println("times");

        // This test assumes that dotTimesEquals has been tested and verified
        RingType r1 = this.createRandom();
        RingType r1clone = r1.clone();

        RingType r2 = r1.scale(RANDOM.nextDouble() * 2.0 * RANGE - RANGE);
        RingType r2clone = r2.clone();

        r1.times(r2);
        assertEquals(r1, r1clone);
        assertEquals(r2, r2clone);

        r1.timesEquals(r2);
        assertFalse(r1.equals(r1clone));
        assertEquals(r2, r2clone);

        assertTrue(r1.equals(r1clone.times(r2), TOLERANCE));

        boolean exceptionThrown = false;
        try
        {
            r1.times(null);
        }
        catch (NullPointerException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }

    }

    public void testDivide()
    {
        System.out.println("divide");

        // This test assumes that dotTimesEquals has been tested and verified
        RingType r1 = this.createRandom();
        RingType r1clone = r1.clone();

        RingType r2 = r1.scale(RANDOM.nextDouble() * 2.0 * RANGE - RANGE);
        RingType r2clone = r2.clone();

        r1.divide(r2);
        assertEquals(r1, r1clone);
        assertEquals(r2, r2clone);

        r1.divideEquals(r2);
        assertFalse(r1.equals(r1clone));
        assertEquals(r2, r2clone);

        assertTrue(r1.equals(r1clone.divide(r2), TOLERANCE));

        boolean exceptionThrown = false;
        try
        {
            r1.divide(null);
        }
        catch (NullPointerException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }

    }

}
