/*
 * File:                RingTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 1, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.util.ObjectUtil;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Abstract class that rigorously tests the methods in Ring using RANDOM
 * subclasses.
 * @param <RingType> Type of ring to test
 * @author krdixon
 */
abstract public class RingTestHarness<RingType extends Ring<RingType>>
    extends TestCase
{

    /**m
     * Random-number generator to use
     */
    public static final Random RANDOM = new Random( 0 );

    /**
     * Range of the RANDOM numbers
     */
    protected static double RANGE = RANDOM.nextDouble() * 100.0;

    /**
     * Default tolerance
     */
    protected static double TOLERANCE = 1e-5;

    /**
     * Constructor
     * @param testName name of the test
     */
    public RingTestHarness(
        String testName )
    {
        super( testName );
    }

    /**
     * Create a new random RingType object
     * @return Random RingType
     */
    abstract protected RingType createRandom();

    /**
     * Test of scaleEquals method, of class gov.sandia.isrc.math.Ring.
     */
    abstract public void testScaleEquals();

    /**
     * Test of plusEquals method, of class gov.sandia.isrc.math.Ring.
     */
    abstract public void testPlusEquals();

    /**
     * Test of dotTimesEquals method, of class gov.sandia.isrc.math.Ring.
     */
    abstract public void testDotTimesEquals();

    /**
     * Test of equals method, of class gov.sandia.isrc.math.Ring.
     */
    public void testEqualsRing()
    {

        System.out.println( "equals(Ring)" );

        RingType r1 = this.createRandom();
        assertFalse( r1.equals( null ) );
        assertFalse( r1.equals( "False, please!" ) );

        RingType r1clone = r1.clone();
        assertEquals( r1, r1 );
        assertEquals( r1, r1clone );

        double delta = RANDOM.nextDouble() + 2.0;
        RingType r2 = r1.scale( delta );
        assertEquals( r1, r1clone );

        assertFalse( r1.equals( r2 ) );
        assertFalse( r1.equals( r2, TOLERANCE ) );

        r1.zero();
        r2.zero();
        assertEquals( r1, r2 );

    }

    /**
     * Test of clone method, of class gov.sandia.isrc.math.Ring.
     */
    public void testClone()
    {

        System.out.println( "clone" );

        RingType r1 = this.createRandom();
        assertNotNull( r1 );

        RingType r2 = r1.clone();
        assertNotNull( r2 );

        assertNotSame( r1, r2 );
        assertEquals( r1, r2 );

        r2.scaleEquals( RANDOM.nextDouble() );
        assertFalse( r1.equals( r2 ) );

    }

    /**
     * Tests the ability of the Ring to serialize itself.
     */
    public void testSerialize() throws Exception
    {

        System.out.println( "serialize" );

        RingType r1 = this.createRandom();
        assertNotNull( r1 );

        RingType r2 = ObjectUtil.deepCopy(r1);
        assertNotNull( r2 );
        assertNotSame( r1, r2 );
        assertEquals( r1, r2 );

        r2.scaleEquals( RANDOM.nextDouble() );
        assertFalse( r1.equals( r2 ) );

    }
    

    /**
     * Test of plus method, of class gov.sandia.isrc.math.Ring.
     */
    public void testPlus()
    {
        System.out.println( "plus" );

        // This test assumes that plusEquals has been tested and verified, and
        // that the exception conditions are equivalent
        RingType r1 = this.createRandom();
        RingType r1clone = r1.clone();
        RingType r2 = r1.scale( RANDOM.nextDouble() * 2.0 * RANGE - RANGE );
        RingType r2clone = r2.clone();

        // Check that plus() doesn't modify r1 or r2
        r1.plus( r2 );
        assertEquals( r1, r1clone );
        assertEquals( r2, r2clone );

        // Check that plusEquals() doesn't modify r2, but does modify r1
        r1.plusEquals( r2 );
        assertEquals( r2, r2clone );
        assertFalse( r1.equals( r1clone ) );

        // See if the plusEquals() result is the same as plus() result
        assertEquals( r1, r1clone.plus( r2 ) );

        // Self-addition should equal a scale of 2.0
        assertEquals( r1.plus( r1 ), r1.scale( 2.0 ) );

        try
        {
            r1.plus( null );
            fail( "Should have thrown null-pointer exception: plus() " + r1.getClass() );
        }
        catch (NullPointerException e)
        {
        }

    }

    /**
     * Test of minus method, of class gov.sandia.isrc.math.Ring.
     */
    public void testMinus()
    {
        System.out.println( "minus" );


        // This test assumes that plus and scale have been tested and verified
        RingType r1 = this.createRandom();
        RingType r1clone = r1.clone();

        RingType r2 = r1.scale( RANDOM.nextDouble() * 2.0 * RANGE - RANGE );
        RingType r2clone = r2.clone();

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
     * Test of minusEquals method, of class gov.sandia.isrc.math.Ring.
     */
    public void testMinusEquals()
    {
        System.out.println( "minusEquals" );

        // This test assumes that plusEquals and scale have been tested and verified
        RingType r1 = this.createRandom();
        RingType r1clone = r1.clone();

        RingType r2 = r1.scale( RANDOM.nextDouble() * 2.0 * RANGE - RANGE );
        RingType r2clone = r2.clone();

        r1.minusEquals( r2 );
        assertEquals( r2, r2clone );
        assertFalse( r1.equals( r1clone ) );

        r1clone.plusEquals( r2.scale( -1.0 ) );
        assertEquals( r2, r2clone );

        assertTrue( r1.equals( r1clone, TOLERANCE ) );

        try
        {
            r1.minusEquals( null );
            fail( "Should have thrown null-pointer exception: minusEquals() " + r1.getClass() );
        }
        catch (NullPointerException e)
        {
        }

    }

    /**
     * Test of dotTimes method, of class gov.sandia.isrc.math.Ring.
     */
    public void testDotTimes()
    {
        System.out.println( "dotTimes" );

        // This test assumes that dotTimesEquals has been tested and verified
        RingType r1 = this.createRandom();
        RingType r1clone = r1.clone();

        RingType r2 = r1.scale( RANDOM.nextDouble() * 2.0 * RANGE - RANGE );
        RingType r2clone = r2.clone();

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

    /**
     * Test of scale method, of class gov.sandia.isrc.math.Ring.
     */
    public void testScale()
    {
        System.out.println( "scale" );

        // This test assumes that scaleEquals has been tested and verified
        double scaleFactor = RANDOM.nextDouble() * 2.0 * RANGE - RANGE;
        RingType r1 = this.createRandom();
        RingType r1clone = r1.clone();

        assertEquals( r1, r1clone );
        r1.scaleEquals( scaleFactor );
        assertTrue( r1.equals( r1clone.scale( scaleFactor ), TOLERANCE ) );
        assertFalse( r1.equals( r1clone ) );

        assertEquals( r1.scale( 2.0 ), r1.plus( r1 ) );

    }

    /**
     * Test of negative method, of class gov.sandia.isrc.math.Ring.
     */
    public void testNegative()
    {
        System.out.println( "negative" );

        // This test assumes that scale() has been tested and verified
        RingType r1 = this.createRandom();
        RingType r1clone = r1.clone();

        r1.negative();
        assertEquals( r1, r1clone );

        assertTrue( r1.negative().equals( r1.scale( -1.0 ), TOLERANCE ) );
    }

    /**
     * Test of negativeEquals method, of class gov.sandia.isrc.math.Ring.
     */
    public void testNegativeEquals()
    {

        System.out.println( "negativeEquals" );

        // This test assumes that scaleEquals() has been tested and verified
        RingType r1 = this.createRandom();
        RingType r1clone = r1.clone();

        RingType r2 = r1.clone();
        RingType r2clone = r2.clone();

        assertEquals( r1, r1clone );
        r1.negativeEquals();
        assertFalse( r1.equals( r1clone ) );

        assertEquals( r2, r2clone );
        r2.scaleEquals( -1.0 );
        assertFalse( r2.equals( r2clone ) );

        assertTrue( r1.equals( r2, TOLERANCE ) );

    }

    /**
     * Test of zero method, of class gov.sandia.isrc.math.Ring.
     */
    public void testZero()
    {

        System.out.println( "zero" );
        RingType r1 = this.createRandom();
        RingType r1clone = r1.clone();
        RingType r2 = r1.clone();
        RingType r2clone = r2.clone();

        assertEquals( r1, r2 );
        r1.zero();
        assertFalse( r1.equals( r1clone ) );
        assertEquals( r1, r2.scale( 0.0 ) );
        assertEquals( r2, r2clone );

    }

    public void testIsZero()
    {

        System.out.println( "zero" );
        RingType zero = this.createRandom();
        zero.zero();

        assertTrue(zero.isZero());
        assertTrue(zero.isZero(1.0));
        assertTrue(this.createRandom().scale(0.0).isZero());
        assertTrue(this.createRandom().scale(0.0).isZero(1.0));
        assertFalse(this.createRandom().isZero());
    }

}
