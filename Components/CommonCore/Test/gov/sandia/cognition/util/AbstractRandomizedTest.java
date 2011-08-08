/*
 * File:                AbstractRandomizedTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 24, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.util;

import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for AbstractRandomizedTest.
 *
 * @author krdixon
 */
public class AbstractRandomizedTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public static Random random = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public double EPS = 1e-5;

    /**
     * Tests for class AbstractRandomizedTest.
     * @param testName Name of the test.
     */
    public AbstractRandomizedTest(
        String testName)
    {
        super(testName);
    }


    public AbstractRandomized createInstance()
    {
        return new TestAbstractRandomized();
    }


    public static class TestAbstractRandomized
        extends AbstractRandomized
    {

        public TestAbstractRandomized()
        {
            super( AbstractRandomizedTest.random );
        }

    }

    /**
     * Tests the constructors of class AbstractRandomizedTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        AbstractRandomized r = this.createInstance();
        assertNotNull( r.getRandom() );
        assertSame( random, r.getRandom() );

    }

    /**
     * Test of clone method, of class AbstractRandomized.
     */
    public void testClone()
    {
        System.out.println("clone");
        AbstractRandomized instance = this.createInstance();
        
        AbstractRandomized clone = (AbstractRandomized) instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotSame( instance.getRandom(), clone.getRandom() );

        double v1 = instance.getRandom().nextDouble();
        double v2 = clone.getRandom().nextDouble();
        assertEquals( v1, v2 );

    }

    /**
     * Test of getRandom method, of class AbstractRandomized.
     */
    public void testGetRandom()
    {
        System.out.println("getRandom");
        AbstractRandomized instance = this.createInstance();
        assertNotNull( instance.getRandom() );
    }

    /**
     * Test of setRandom method, of class AbstractRandomized.
     */
    public void testSetRandom()
    {
        System.out.println("setRandom");
        Random r2 = new Random( 2 );
        AbstractRandomized instance = this.createInstance();
        assertNotNull( instance.getRandom() );
        instance.setRandom(r2);
        assertSame( r2, instance.getRandom() );
    }

}
