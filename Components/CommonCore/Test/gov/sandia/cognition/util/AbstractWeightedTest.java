/*
 * File:                AbstractWeightedTest.java
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

import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for AbstractWeightedTest.
 *
 * @author krdixon
 */
public class AbstractWeightedTest
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
     * Tests for class AbstractWeightedTest.
     * @param testName Name of the test.
     */
    public AbstractWeightedTest(
        String testName)
    {
        super(testName);
    }

    /**
     * 
     */
    public static class DummyWeighted
        extends AbstractWeighted
    {

        /**
         * 
         */
        public DummyWeighted()
        {
            super();
        }
        
        /**
         *
         * @param weight
         */
        public DummyWeighted(
            double weight )
        {
            super( weight );
        }
        
    }

    /**
     * Creates an instance
     * @return
     */
    public AbstractWeighted createInstance()
    {
        return new DummyWeighted( RANDOM.nextGaussian() );
    }

    /**
     * Tests the constructors of class AbstractWeightedTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        AbstractWeighted w = new DummyWeighted();
        assertEquals( AbstractWeighted.DEFAULT_WEIGHT, w.getWeight() );

        double weight = RANDOM.nextGaussian();
        w = new DummyWeighted( weight );
        assertEquals( weight, w.getWeight() );

    }

    /**
     * Test of getWeight method, of class AbstractWeighted.
     */
    public void testGetWeight()
    {
        System.out.println("getWeight");
        AbstractWeighted instance = new AbstractWeighted();
        double weight = RANDOM.nextGaussian();
        instance.setWeight(weight);
        assertEquals( weight, instance.getWeight() );
    }

    /**
     * Test of setWeight method, of class AbstractWeighted.
     */
    public void testSetWeight()
    {
        System.out.println("setWeight");
        AbstractWeighted instance = new AbstractWeighted();
        double weight = RANDOM.nextGaussian();
        instance.setWeight(weight);
        assertEquals( weight, instance.getWeight() );
    }



}
