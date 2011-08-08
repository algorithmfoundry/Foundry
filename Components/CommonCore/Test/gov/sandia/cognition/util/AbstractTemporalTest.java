/*
 * File:                AbstractTemporalTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 13, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.util;

import java.util.Date;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for AbstractTemporalTest.
 *
 * @author krdixon
 */
public class AbstractTemporalTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public final double TOLERANCE = 1e-5;

    /**
     * Tests for class AbstractTemporalTest.
     * @param testName Name of the test.
     */
    public AbstractTemporalTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Dummy
     */
    public class DummyTemporal
        extends AbstractTemporal
    {

        /**
         *
         */
        public DummyTemporal()
        {
            super();
        }

        /**
         * 
         * @param time
         */
        public DummyTemporal(
            Date time )
        {
            super( time );
        }

    }

    /**
     * Creates a new AbstractTemporal
     * @return AbstractTemporal
     */
    public AbstractTemporal createInstance()
    {
        return new DummyTemporal( new Date( RANDOM.nextLong() ) );
    }

    /**
     *
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        AbstractTemporal t = new DummyTemporal();
        assertNull( t.getTime() );

        Date time = new Date();
        t = new DummyTemporal(time);
        assertSame( time, t.getTime() );
    }

    /**
     * Test of clone method, of class AbstractTemporal.
     */
    public void testClone()
    {
        System.out.println("clone");
        AbstractTemporal instance = this.createInstance();
        AbstractTemporal clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotNull( clone.getTime() );
        assertNotSame( instance.getTime(), clone.getTime() );
        assertEquals( 0, instance.compareTo(clone) );
    }

    /**
     * Test of compareTo method, of class AbstractTemporal.
     */
    public void testCompareTo()
    {
        System.out.println("compareTo");
        AbstractTemporal i1 = this.createInstance();
        AbstractTemporal i2 = this.createInstance();
        int result = i1.getTime().compareTo(i2.getTime());
        assertEquals( result, i1.compareTo(i2) );
    }

    /**
     * Test of getTime method, of class AbstractTemporal.
     */
    public void testGetTime()
    {
        System.out.println("getTime");
        AbstractTemporal instance = this.createInstance();
        assertNotNull( instance.getTime() );
    }

    /**
     * Test of setTime method, of class AbstractTemporal.
     */
    public void testSetTime()
    {
        System.out.println("setTime");
        AbstractTemporal instance = this.createInstance();
        Date time = instance.getTime();
        assertNotNull( time );
        instance.setTime(null);
        assertNull( instance.getTime() );
        instance.setTime(time);
        assertSame( time, instance.getTime() );
    }

}
